package generated;

public class Computer {
    int state = 0;

    final static int STATE$DOWNED = 0;
    final static int STATE$BOOTED = 1;
    final static int STATE$OSSELECTED = 2;
    final static int STATE$OSLOADED = 3;

    void boot() {
        if (state == STATE$DOWNED) {
            state = STATE$BOOTED;
        } else {
            throw new IllegalStateException("Wrong state");
        }
    }

    void selectOS(String osName) {
        if (state == STATE$BOOTED) {
            state = STATE$OSSELECTED;
        } else {
            throw new IllegalStateException("Wrong state");
        }
    }

    void loadOS() {
        if (state == STATE$OSSELECTED) {
            state = STATE$OSLOADED;
        } else {
            throw new IllegalStateException("Wrong state");
        }
    }

    void shutdown() {
        if (state == STATE$OSLOADED) {
            state = STATE$DOWNED;
        } else {
            throw new IllegalStateException("Wrong state");
        }
    }

    void addMemory() {
    }

}