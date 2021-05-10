package ru.vldf.testlibrary;

import spider.SPIDER$SHIFTS;

class Memory {
    SPIDER$SHIFTS SHIFTS_MANAGER = new spider.SPIDER$SHIFTS();

    void open() {
        SHIFTS_MANAGER.transitionMemoryCallOpen();
    }

    void setOS(ru.vldf.testlibrary.OS os) {
        SHIFTS_MANAGER.transitionMemoryCallSetOS();
    }

    void close() {
        SHIFTS_MANAGER.transitionMemoryCallClose();
    }
}
