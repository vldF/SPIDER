package ru.vldf.simplelibrary;

import org.jetbrains.research.kex.Intrinsics;

public class Computer {
    private final int STATE$CONST$Computer$DOWNED = 0;

    private final int STATE$CONST$Computer$BOOTED = 1;

    private final int STATE$CONST$Computer$OSSELECTED = 2;

    private final int STATE$CONST$Computer$OSLOADED = 3;

    private final int STATE$CONST$Computer$CLOSED = 4;

    public Memory memory;

    public int STATE = STATE$CONST$Computer$DOWNED;

    public void boot() {
        if (STATE == STATE$CONST$Computer$DOWNED) {
            STATE = STATE$CONST$Computer$BOOTED;
        } else {
            Intrinsics.kexAssert("id0", false);
        }
    }

    public void selectOS(String osName) {
        Intrinsics.kexAssert("id1", (STATE == 0));
        if (STATE == STATE$CONST$Computer$BOOTED) {
            STATE = STATE$CONST$Computer$OSSELECTED;
        } else {
            Intrinsics.kexAssert("id2", false);
        }
    }

    public void loadOS() {
        if (STATE == STATE$CONST$Computer$OSSELECTED) {
            STATE = STATE$CONST$Computer$OSLOADED;
        } else {
            Intrinsics.kexAssert("id3", false);
        }
    }

    public void shutdown() {
        STATE = STATE$CONST$Computer$DOWNED;
    }

    public void addMemory() {
        memory = new Memory();
        memory.STATE = 6;
    }
}
