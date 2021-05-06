public class SPIDER$SHIFTS {
    private final int STATE$$CONST$$Computer$$DOWNED = 0;

    private final int STATE$$CONST$$Computer$$BOOTED = 1;

    private final int STATE$$CONST$$Computer$$OSSELECTED = 2;

    private final int STATE$$CONST$$Computer$$OSLOADED = 3;

    private final int STATE$$CONST$$Memory$$CLOSE = 4;

    private final int STATE$$CONST$$Memory$$OPEN = 5;

    public int STATE$$COMPUTER = STATE$CONST$Computer$DOWNED;

    public int STATE$$MEMORY = STATE$CONST$Memory$CLOSE;

    void transitionComputerCallBoot() {
        if (STATE$COMPUTER == STATE$CONST$Computer$DOWNED)
            STATE$COMPUTER = STATE$CONST$Computer$BOOTED;
        } else {
            org.jetbrains.research.kex.Intrinsics.kexAssert("id0", false);
        }
    }

    void transitionComputerCallSelectOS() {
        if (STATE$COMPUTER == STATE$CONST$Computer$BOOTED)
            STATE$COMPUTER = STATE$CONST$Computer$OSSELECTED;
        } else {
            org.jetbrains.research.kex.Intrinsics.kexAssert("id1", false);
        }
    }

    void transitionComputerCallLoadOS() {
        if (STATE$COMPUTER == STATE$CONST$Computer$OSSELECTED)
            STATE$COMPUTER = STATE$CONST$Computer$OSLOADED;
        } else {
            org.jetbrains.research.kex.Intrinsics.kexAssert("id2", false);
        }
    }

    void transitionComputerCallShutdown() {
        if (STATE$COMPUTER == STATE$CONST$Computer$OSLOADED)
            STATE$COMPUTER = STATE$CONST$Computer$DOWNED;
        } else {
            org.jetbrains.research.kex.Intrinsics.kexAssert("id3", false);
        }
    }

    void transitionComputerCallAddMemory() {
    }

    void transitionMemoryCallOpen() {
        if (STATE$MEMORY == STATE$CONST$Memory$CLOSE)
            STATE$MEMORY = STATE$CONST$Memory$OPEN;
        } else if (STATE$MEMORY == STATE$CONST$Memory$OPEN)
            STATE$MEMORY = STATE$CONST$Memory$CLOSE;
        } else {
            org.jetbrains.research.kex.Intrinsics.kexAssert("id4", false);
        }
    }

    void transitionMemoryCallSetOS() {
        if (STATE$MEMORY == STATE$CONST$Memory$OPEN)
            STATE$MEMORY = STATE$CONST$Memory$SELF;
        } else {
            org.jetbrains.research.kex.Intrinsics.kexAssert("id5", false);
        }
    }

    void transitionMemoryCallClose() {
    }
}
