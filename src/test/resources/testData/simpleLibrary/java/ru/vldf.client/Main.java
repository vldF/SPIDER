package ru.vldf.client;

import org.jetbrains.research.kex.Intrinsics;
import ru.vldf.simplelibrary.Computer;

public class Main {
    public static void main(String[] args) {
        Computer computer2 = new Computer();
        computer2.shutdown();
        computer2.boot();
        computer2.selectOS("win");
        computer2.loadOS();
    }
}