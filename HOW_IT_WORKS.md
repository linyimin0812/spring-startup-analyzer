# 应用启动数据采集原理

刚开始的时候，只想做Spring bean加载耗时timeline可视化分析，实现了一个简单的版本[spring-bean-timeline](https://github.com/linyimin0812/spring-bean-timeline)，但是随着需求的增多，直接和应用源码耦合的方式不再适用，容易产生依赖冲突。于是开始引入java agent技术。

java agent是一种代理技术，通过jvm的Instrumentation api实现，这个api提供了在jvm加载类之前或之后修改字节码的能力。通常被用于java应用程序的监控、诊断、性能分析、代码注入等。java agent提供了两个入口点：`premain`和`main`方法。其中`premain`方法可以实现**在java应用程序的类被加载之前对它们进行转换**。

要观测应用启动过程，需要在应用类被加载之前对其进行增强，然后加载增强后的类。所以选择了java agent的`premain`实现。由于通过asm进行字节码增强细节太多，又不好理解，所以选择了[ByteKit](https://github.com/alibaba/bytekit)进行字节码增强，ByteKit一个基于ASM提供更高层的字节码处理能力，主要面向诊断/APM领域的字节码库，提供了一套简洁的API，开发人员可以轻松的完成字节码增强。

其他实现主要参考了[jvm-sandbox](https://github.com/alibaba/jvm-sandbox)和[arthas](https://github.com/alibaba/arthas)的实现。


## 类/方法增强策略

增强类`ProfilerClassFileTransformer`是接口`ClassFileTransformer`的实现类，其`transform`方法会在类被加载到jvm之前执行，所以在这个方法中实现对指定类的增强。

参考jvm-sandbox的思想：任何一个Java方法的调用都可以分解为BEFORE、RETURN和THROWS三个环节，由此在三个环节上引申出对应环节的事件探测和流程控制机制。结合ByteKit提供的API

<details>
    <summary></summary>
</details>

待完成

## 类隔离策略

待完成

## 扩展策略

待完成

# 应用启动加速原理

## Spring Bean异步加载原理

待完成