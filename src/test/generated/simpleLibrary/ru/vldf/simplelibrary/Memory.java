package ru.vldf.simplelibrary;

import org.jetbrains.research.kex.Intrinsics;

public class Memory {
    private final int STATE$CONST$Memory$CLOSE = 0;

    private final int STATE$CONST$Memory$OPEN = 1;

    public int STATE = STATE$CONST$Memory$CLOSE;

    public void open() {
        if (STATE == STATE$CONST$Memory$CLOSE) {
            STATE = STATE$CONST$Memory$OPEN;
        } else if (STATE == STATE$CONST$Memory$OPEN) {
            STATE = STATE$CONST$Memory$CLOSE;
        } else {
            Intrinsics.kexAssert("id3", false);
        }
    }

    public void setOS(ru.vldf.simplelibrary.OS os) {
        if (STATE == STATE$CONST$Memory$OPEN) {
        } else {
            Intrinsics.kexAssert("id4", false);
        }
    }

    public ru.vldf.simplelibrary.OS getOS() {
        return org.jetbrains.research.kex.Objects.kexUnknown();
    }

    public void close() {
    }
}
