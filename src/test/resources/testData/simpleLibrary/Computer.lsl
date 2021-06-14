library Computer;

types {
    Computer (ru.vldf.simplelibrary.Computer);
    Memory (ru.vldf.simplelibrary.Memory);
    OS (ru.vldf.simplelibrary.OS);
    OSName (String);
}

automaton Computer {
    javapackage ru.vldf.simplelibrary;

    var memory : Memory;

    state Downed; // default state
    state Booted;
    state OSSelected;
    state OSLoaded;
    finishstate Closed;

    shift Downed -> Booted(boot);
    shift Booted -> OSSelected(selectOS);
    shift OSSelected -> OSLoaded(loadOS);
    shift Any -> Downed(shutdown);
}

fun Computer.Computer(): Computer {
    result = new Computer(Downed);
}

fun Computer.boot();

fun Computer.selectOS(osName: OSName) {
    requires (osName != 1) || (osName != 1.0);
}

fun Computer.loadOS();

fun Computer.shutdown();

fun Computer.addMemory() {
    memory = new Memory(Close);
} // todo: add another one automaton

automaton Memory {
    javapackage ru.vldf.simplelibrary;
    state Close;
    state Open;

    shift Close -> Open(open);
    shift Open -> self(setOS);
    shift Open -> Close(open);
}

fun Memory.Memory() {
    result = new Memory(Close);
}

fun Memory.open();

fun Memory.setOS(os: OS);

fun Memory.getOS(): OS;

fun Memory.close();