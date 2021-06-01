package ru.vldf.simplelibrary;

import ru.vldf.simplelibrary.Memory;
import ru.vldf.simplelibrary.OS;

class Computer {
    Memory memory = null;
    boolean isOsLoad = false;

    void boot() {
        memory = new Memory();
    }

    void selectOs(String osName) {
        if (memory == null) {
            throw new IllegalStateException("Os wasn't loaded");
        }
        if (osName == "win") {
            memory.setOS(OS.WIN);
        } else if (osName == "linux") {
            memory.setOS(OS.LINUX);
        } else {
            throw new IllegalArgumentException("Invalid OS name");
        }
    }

    void loadOS() {
        isOsLoad = true;
    }

    void shutdown() {
        memory = null;
        isOsLoad = false;
    }
}