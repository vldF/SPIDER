package ru.vldf.testlibrary;

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
            throw new IllegalStateException("Wrong state");
        }
    }

    void setOS(ru.vldf.testlibrary.OS os) {
        if (state == STATE$OPEN) {
            state = STATE$OPEN;
        } else {
            throw new IllegalStateException("Wrong state");
        }
    }

    void close() {
    }

}
