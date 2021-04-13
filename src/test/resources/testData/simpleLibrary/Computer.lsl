library Computer;

types {
    Computer (ru.vldf.testlibrary.Computer);
    Memory (ru.vldf.testlibrary.Memory);
    OS (ru.vldf.testlibrary.OS);
    OSName (String);
}

automaton Computer {
    javapackage ru.vldf.testlibrary;

    var memory : Memory;

    state Downed; // default state
    state Booted;
    state OSSelected;
    state OSLoaded;

    shift Downed -> Booted(boot);
    shift Booted -> OSSelected(selectOS);
    shift OSSelected -> OSLoaded(loadOS);
    shift OSLoaded -> Downed(shutdown);
}

fun Computer.boot();

fun Computer.selectOS(osName: OSName);

fun Computer.loadOS();

fun Computer.shutdown();

fun Computer.addMemory() {
    memory = new Memory(Close);
} // todo: add another one automaton

automaton Memory {
    javapackage ru.vldf.testlibrary;
    state Close;
    state Open;

    shift Close -> Open(open);
    shift Open -> self(setOS);
    shift Open -> Close(open);
}

fun Memory.open();

fun Memory.setOS(os: OS);

fun Memory.close();