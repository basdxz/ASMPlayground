package com.github.basdxz.asmplayground;

public class TestClass {
    // Method targeted by ASM
    public boolean targetMethod(int x, int y) {
        return x > y;
    }

    // Expected injection result
    public boolean expectedResult(int x, int y) {
        if (injectedMethod(x, y))
            return false;
        return x > y;
    }

    // Dummy method to inject
    public static boolean injectedMethod(int x, int y) {
        return x > y + 3;
    }
}
