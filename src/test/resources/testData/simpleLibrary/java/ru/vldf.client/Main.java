package ru.vldf.client;

import org.jetbrains.research.kex.Intrinsics;
import ru.vldf.simplelibrary.Computer;

public class Main {
    public static void main(String[] args) {
        Computer computer = new Computer();
        computer.shutdown();
        computer.boot();
        computer.loadOS();
    }
}