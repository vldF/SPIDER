package spider;

import org.jetbrains.research.kex.Intrinsics;

public class SPIDER$SHIFTS {
    private final int STATE$CONST$Computer$DOWNED = 0;

    private final int STATE$CONST$Computer$BOOTED = 1;

    private final int STATE$CONST$Computer$OSSELECTED = 2;

    private final int STATE$CONST$Computer$OSLOADED = 3;

    private final int STATE$CONST$Memory$CLOSE = 4;

    private final int STATE$CONST$Memory$OPEN = 5;

    public int STATE$COMPUTER = STATE$CONST$Computer$DOWNED;

    public int STATE$MEMORY = STATE$CONST$Memory$CLOSE;

    public void transitionComputerCallBoot() {
        if (STATE$COMPUTER == STATE$CONST$Computer$DOWNED) {
            STATE$COMPUTER = STATE$CONST$Computer$BOOTED;
        } else {
            Intrinsics.kexAssert("id0", false);
        }
    }

    public void transitionComputerCallSelectOS() {
        if (STATE$COMPUTER == STATE$CONST$Computer$BOOTED) {
            STATE$COMPUTER = STATE$CONST$Computer$OSSELECTED;
        } else {
            Intrinsics.kexAssert("id1", false);
        }
    }

    public void transitionComputerCallLoadOS() {
        if (STATE$COMPUTER == STATE$CONST$Computer$OSSELECTED) {
            STATE$COMPUTER = STATE$CONST$Computer$OSLOADED;
        } else {
            Intrinsics.kexAssert("id2", false);
        }
    }

    public void transitionComputerCallShutdown() {
        if (STATE$COMPUTER == STATE$CONST$Computer$OSLOADED) {
            STATE$COMPUTER = STATE$CONST$Computer$DOWNED;
        } else {
            Intrinsics.kexAssert("id3", false);
        }
    }

    public void transitionComputerCallAddMemory() {
    }

    public void transitionMemoryCallOpen() {
        if (STATE$MEMORY == STATE$CONST$Memory$CLOSE) {
            STATE$MEMORY = STATE$CONST$Memory$OPEN;
        } else if (STATE$MEMORY == STATE$CONST$Memory$OPEN) {
            STATE$MEMORY = STATE$CONST$Memory$CLOSE;
        } else {
            Intrinsics.kexAssert("id4", false);
        }
    }

    public void transitionMemoryCallSetOS() {
        if (STATE$MEMORY == STATE$CONST$Memory$OPEN) {
        } else {
            Intrinsics.kexAssert("id5", false);
        }
    }

    public void transitionMemoryCallClose() {
    }
}
