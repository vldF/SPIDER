package ru.vldf.simplelibrary;

import org.jetbrains.research.kex.Intrinsics;

public class Computer {
    private final int STATE$CONST$Computer$DOWNED = 0;

    private final int STATE$CONST$Computer$BOOTED = 1;

    private final int STATE$CONST$Computer$OSSELECTED = 2;

    private final int STATE$CONST$Computer$OSLOADED = 3;

    private final int STATE$CONST$Computer$CLOSED = 4;

    public int STATE;

    public ru.vldf.simplelibrary.Memory memory;

    public Computer() {
        STATE = STATE$CONST$Computer$DOWNED;
    }

    public void boot() {
        Intrinsics.kexAssert("id1", STATE != STATE$CONST$Computer$CLOSED);
        if (STATE == STATE$CONST$Computer$DOWNED) {
            STATE = STATE$CONST$Computer$BOOTED;
        } else {
            Intrinsics.kexAssert("id5", false);
        }
        memory = new Memory();
        memory.STATE = 6;
    }

    public void selectOS(String osName) {
        Intrinsics.kexAssert("id0", (osName == "win" || osName == "linux") && memory != null);
        Intrinsics.kexAssert("id2", STATE != STATE$CONST$Computer$CLOSED);
        if (STATE == STATE$CONST$Computer$BOOTED) {
            STATE = STATE$CONST$Computer$OSSELECTED;
        } else {
            Intrinsics.kexAssert("id6", false);
        }
    }

    public void loadOS() {
        Intrinsics.kexAssert("id3", STATE != STATE$CONST$Computer$CLOSED);
        if (STATE == STATE$CONST$Computer$OSSELECTED) {
            STATE = STATE$CONST$Computer$OSLOADED;
        } else {
            Intrinsics.kexAssert("id7", false);
        }
    }

    public void shutdown() {
        Intrinsics.kexAssert("id4", STATE != STATE$CONST$Computer$CLOSED);
        STATE = STATE$CONST$Computer$CLOSED;
    }
}
