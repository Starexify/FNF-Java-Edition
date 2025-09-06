package com.nova.fnfjava;

public class TestClass {
    static {
        System.out.println("TestClass static block executed!");
    }

    public TestClass() {
        System.out.println("TestClass constructor called");
    }

    public void testMethod() {
        System.out.println("TestClass.testMethod() - ORIGINAL");
    }

    public static void staticTestMethod() {
        System.out.println("TestClass.staticTestMethod() - ORIGINAL");
    }

    public String getString() {
        return "original string";
    }
}
