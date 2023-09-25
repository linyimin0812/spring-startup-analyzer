package io.github.linyimin0812.spring.startup.jdwp.command;

/**
 * @author linyimin
 **/
public abstract class Package<T> {

    private final int length;
    private final int id;
    private final byte flag;

    private T data;

    public Package(int length, int id, byte flag) {
        this(length, id, flag, null);
    }

    public Package(int length, int id, byte flag, T data) {
        this.length = length;
        this.id = id;
        this.flag = flag;
        this.data = data;
    }

    public int getLength() {
        return length;
    }

    public int getId() {
        return id;
    }

    public byte getFlag() {
        return flag;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
