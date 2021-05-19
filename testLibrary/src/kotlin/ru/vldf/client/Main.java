package ru.vldf.client;

import org.jetbrains.research.kex.Intrinsics;
import ru.vldf.testlibrary.Computer;

public class Main {
    public static void main(String[] args) {
        Intrinsics.kexAssert("id0", false);
        Computer computer = new Computer();
        computer.shutdown();
        computer.boot();
        computer.loadOS();
    }
}