package ru.vldf.testlibrary;

import org.jetbrains.research.kex.Intrinsics;

public class Main {
    public static void main(String[] args) {
        Intrinsics.kexAssert("id0", false);
        Computer computer = new Computer();
        computer.shutdown();
        computer.boot();
        computer.loadOS();
    }
}