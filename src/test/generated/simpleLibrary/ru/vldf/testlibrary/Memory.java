package ru.vldf.testlibrary;

import org.jetbrains.research.kex.Intrinsics;

class Memory {
    private final int STATE$CONST$Memory$CLOSE = 0;

    private final int STATE$CONST$Memory$OPEN = 1;

    public int STATE = STATE$CONST$Memory$CLOSE;

    void open() {
        if (STATE == STATE$CONST$Memory$CLOSE) {
            STATE = STATE$CONST$Memory$OPEN;
        } else if (STATE == STATE$CONST$Memory$OPEN) {
            STATE = STATE$CONST$Memory$CLOSE;
        } else {
            Intrinsics.kexAssert("id3", false);
        }
    }

    void setOS(ru.vldf.testlibrary.OS os) {
        if (STATE == STATE$CONST$Memory$OPEN) {
        } else {
            Intrinsics.kexAssert("id4", false);
        }
    }

    ru.vldf.testlibrary.OS getOS() {
        return org.jetbrains.research.kex.Objects.kexUnknown();
    }

    void close() {
    }
}
