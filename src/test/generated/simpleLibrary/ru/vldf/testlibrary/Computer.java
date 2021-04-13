package ru.vldf.testlibrary;

import org.jetbrains.research.kex.Intrinsics;

public class Computer {
    private Memory VARIABLE$memory;
    int state = 0;

    final static int STATE$DOWNED = 0;
    final static int STATE$BOOTED = 1;
    final static int STATE$OSSELECTED = 2;
    final static int STATE$OSLOADED = 3;

    void boot() {
        if (state == STATE$DOWNED) {
            state = STATE$BOOTED;
        } else {
            Intrinsics.kexAssert(false);
        }
    }

    void selectOS(String osName) {
        if (state == STATE$BOOTED) {
            state = STATE$OSSELECTED;
        } else {
            Intrinsics.kexAssert(false);
        }
    }

    void loadOS() {
        if (state == STATE$OSSELECTED) {
            state = STATE$OSLOADED;
        } else {
            Intrinsics.kexAssert(false);
        }
    }

    void shutdown() {
        if (state == STATE$OSLOADED) {
            state = STATE$DOWNED;
        } else {
            Intrinsics.kexAssert(false);
        }
    }

    void addMemory() {
        VARIABLE$memory = new Memory();
        VARIABLE$memory.state = Memory.STATE$CLOSE;
    }

}
