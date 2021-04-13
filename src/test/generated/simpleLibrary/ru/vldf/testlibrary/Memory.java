package ru.vldf.testlibrary;

import org.jetbrains.research.kex.Intrinsics;

public class Memory {
    int state = 0;

    final static int STATE$CLOSE = 0;
    final static int STATE$OPEN = 1;

    void open() {
        if (state == STATE$CLOSE) {
            state = STATE$OPEN;
        } else if (state == STATE$OPEN) {
            state = STATE$CLOSE;
        } else {
            Intrinsics.kexAssert(true);
        }
    }

    void setOS(OS os) {
        if (state == STATE$OPEN) {
            state = STATE$OPEN;
        } else {
            Intrinsics.kexAssert(true);
        }
    }

    void close() {
    }

}
