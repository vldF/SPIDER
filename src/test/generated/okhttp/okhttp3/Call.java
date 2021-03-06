package okhttp3;

import org.jetbrains.research.kex.Intrinsics;

public class Call {
    private final int STATE$CONST$Call$CREATED = 0;

    private final int STATE$CONST$Call$EXECUTED = 1;

    public int STATE;

    public Call() {
        STATE = STATE$CONST$Call$CREATED;
    }

    public okhttp3.Response execute() {
        Intrinsics.kexAssert("id6", STATE != STATE$CONST$Call$EXECUTED);
        if (STATE == STATE$CONST$Call$CREATED) {
            STATE = STATE$CONST$Call$EXECUTED;
        } else {
            Intrinsics.kexAssert("id10", false);
        }
        Response tmpRes = new Response();
        tmpRes.STATE = 6;
        return tmpRes;
    }
}
