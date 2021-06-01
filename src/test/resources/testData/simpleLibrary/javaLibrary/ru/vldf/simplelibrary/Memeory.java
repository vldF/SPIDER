package ru.vldf.simplelibrary;

import ru.vldf.simplelibrary.OS;

class Memory {
    private OS currentOS = null;
    private boolean isOpen = false;

    void open() {
        isOpen = true;
    }

    void setOS(OS os) {
        currentOS = os;
    }

    void close() {
        isOpen = false;
    }

    OS getOS() {
        return currentOS;
    }
}