package ru.vldf.testlibrary;

import spider.SPIDER$SHIFTS;

class Computer {
    SPIDER$SHIFTS SHIFTS_MANAGER = new spider.SPIDER$SHIFTS();

    Memory memory;

    void boot() {
        SHIFTS_MANAGER.transitionComputerCallBoot();
    }

    void selectOS(String osName) {
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
