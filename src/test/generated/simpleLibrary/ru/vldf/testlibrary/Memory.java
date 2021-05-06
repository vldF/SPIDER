package ru.vldf.testlibrary;

class Memory {
    SPIDER$SHIFTS SHIFTS_MANAGER = new SPIDER$SHIFTS();

    void open() {
        SHIFTS_MANAGER.transitionMemoryCallOpen();
    }

    void setOS(OS os) {
        SHIFTS_MANAGER.transitionMemoryCallSetOS();
    }

    void close() {
        SHIFTS_MANAGER.transitionMemoryCallClose();
    }
}
