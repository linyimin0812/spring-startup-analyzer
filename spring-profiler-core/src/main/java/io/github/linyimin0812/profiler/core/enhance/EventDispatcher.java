package io.github.linyimin0812.profiler.core.enhance;

import com.alibaba.deps.org.objectweb.asm.Type;
import io.github.linyimin0812.profiler.core.container.IocContainer;
import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.AtExceptionExitEvent;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.Bridge;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author linyimin
 **/
public class EventDispatcher extends Bridge.AbstractBridge {

    private static final AtomicLong invokeIdSequencer = new AtomicLong(1000);

    private static final ThreadLocal<InvokeStack> invokeRef = ThreadLocal.withInitial(InvokeStack::new);

    @Override
    public void atEnter(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args) {

        InvokeStack invokeStack = invokeRef.get();
        // 当前调用id
        long invokeId = invokeIdSequencer.incrementAndGet();
        invokeStack.pushInvokeId(invokeId);

        // 调用过程id
        long processId = invokeStack.getProcessId();

        InvokeEvent event = new AtEnterEvent(processId,
                invokeId,
                clazz,
                target,
                methodName,
                methodDesc,
                args
        );

        handleEvent(event);

    }

    @Override
    public void atExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Object returnObject) {
        handleOnEnd(clazz, target, methodName, methodDesc, args, returnObject, false);
    }

    @Override
    public void atExceptionExit(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Throwable throwable) {
        handleOnEnd(clazz, target, methodName, methodDesc, args, throwable, true);
    }

    private void handleOnEnd(Class<?> clazz, Object target, String methodName, String methodDesc, Object[] args, Object object, boolean isException) {
        InvokeStack invokeStack = invokeRef.get();

        // atBefore和atExit错位
        if (invokeStack.isEmpty()) {
            invokeRef.remove();
            return;
        }

        long processId = invokeStack.getProcessId();
        long invokeId = invokeStack.popInvokeId();

        InvokeEvent event = isException
                ? new AtExceptionExitEvent(processId, invokeId, clazz, target, methodName, methodDesc, args, (Throwable) object)
                : new AtExitEvent(processId, invokeId, clazz, target, methodName, methodDesc, args, object);

        handleEvent(event);
    }

    private void handleEvent(InvokeEvent event) {

        for (EventListener listener : IocContainer.getComponents(EventListener.class)) {

            if (!listener.listen().contains(event.type)) {
                continue;
            }

            String className = event.clazz.getName();
            String[] argTypes = getArgTypes(event.methodDesc);

            if (!listener.filter(className) || !listener.filter(event.methodName, argTypes)) {
                continue;
            }

            listener.onEvent(event);
        }
    }

    private String[] getArgTypes(String methodDesc) {

        Type methodType = Type.getMethodType(methodDesc);
        Type[] types = methodType.getArgumentTypes();

        String[] argTypes = new String[types.length];

        for (int i = 0; i < types.length; i++) {
            argTypes[i] = types[i].getClassName();
        }

        return argTypes;

    }

    /**
     * 调用栈
     */
    static class InvokeStack {

        /**
         * 调用栈
         */
        private final Deque<Long> stack = new LinkedList<>();

        /**
         * 压入调用id
         * @param invokeId 一次调用id
         */
        public void pushInvokeId(long invokeId) {
            stack.push(invokeId);
        }

        /**
         * 弹出调用id
         * @return 调用id
         */
        public long popInvokeId() {
            long invokeId = stack.pop();
            if (stack.isEmpty()) {
                invokeRef.remove();
            }

            return invokeId;
        }

        /**
         * 获取调用过程id(链路开始的id)，processId + invokeId即可将AtBeforeEvent和AtExitEvent关联到一起
         * @return 调用过程id
         */
        long getProcessId() {
            return stack.getFirst();
        }

        boolean isEmpty() {
            return stack.isEmpty();
        }
    }
}
