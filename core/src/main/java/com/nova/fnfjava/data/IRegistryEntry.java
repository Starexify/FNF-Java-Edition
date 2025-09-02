package com.nova.fnfjava.data;

public interface IRegistryEntry<T> {
    String getId();
    void destroy();
    T getData();
    void loadData(T data);
}
