package com.nova.fnfjava.data;

public interface IRegistryEntry<T> {
    String getId();
    T getData();
    void loadData(T data);
    void destroy();
}
