package ru.vldf.testlibrary;

import org.jetbrains.research.kex.Intrinsics;

public class Main {
    public static void main(String[] args) {
        Intrinsics.kexAssert(args[0].equals("test"));
        Computer computer = new Computer();
        computer.shutdown();
        computer.boot();
        computer.loadOS();
    }
}