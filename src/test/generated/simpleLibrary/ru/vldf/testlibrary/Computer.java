package ru.vldf.testlibrary;

import org.jetbrains.research.kex.Intrinsics;

class Computer {
    private final int STATE$CONST$Computer$DOWNED = 0;

    private final int STATE$CONST$Computer$BOOTED = 1;

    private final int STATE$CONST$Computer$OSSELECTED = 2;

    private final int STATE$CONST$Computer$OSLOADED = 3;

    Memory memory;

    public int STATE = STATE$CONST$Computer$DOWNED;

    void boot() {
        if (STATE == STATE$CONST$Computer$DOWNED) {
            STATE = STATE$CONST$Computer$BOOTED;
        } else {
            Intrinsics.kexAssert("id0", false);
        }
    }

    void selectOS(String osName) {
        if (STATE == STATE$CONST$Computer$BOOTED) {
            STATE = STATE$CONST$Computer$OSSELECTED;
        } else {
            Intrinsics.kexAssert("id1", false);
        }
    }

    void loadOS() {
        if (STATE == STATE$CONST$Computer$OSSELECTED) {
            STATE = STATE$CONST$Computer$OSLOADED;
        } else {
            Intrinsics.kexAssert("id2", false);
        }
    }

    void shutdown() {
        STATE = STATE$CONST$Computer$DOWNED;
    }

    void addMemory() {
        memory = new Memory();
        memory.STATE = 5;
    }
}
