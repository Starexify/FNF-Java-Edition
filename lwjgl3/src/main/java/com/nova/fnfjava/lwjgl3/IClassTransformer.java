package com.nova.fnfjava.lwjgl3;

public interface IClassTransformer {
    byte[] transform(String name, String transformedName, byte[] basicClass);
}
