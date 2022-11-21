package com.github.basdxz.asmplayground;

public class TestClass {
    public boolean targetMethod(int x, int y) {
        return x > y;
    }

    public boolean expectedResult(int x, int y) {
        if (injectedMethod(x, y))
            return false;
        return x > y;
    }

    public static boolean injectedMethod(int x, int y) {
        return x > y + 3;
    }
}
