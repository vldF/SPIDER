package ru.vldf.testlibrary;

class Computer {
    SPIDER$SHIFTS SHIFTS_MANAGER = new SPIDER$SHIFTS();

    Memory memory;

    void boot() {
        SHIFTS_MANAGER.transitionComputerCallBoot();
    }

    void selectOS(OSName osName) {
        SHIFTS_MANAGER.transitionComputerCallSelectOS();
    }

    void loadOS() {
        SHIFTS_MANAGER.transitionComputerCallLoadOS();
    }

    void shutdown() {
        SHIFTS_MANAGER.transitionComputerCallShutdown();
    }

    void addMemory() {
        SHIFTS_MANAGER.transitionComputerCallAddMemory();
        memory = new Memory();
        memory.SHIFTS_MANAGER.STATE$MEMORY = 4;
    }
}
