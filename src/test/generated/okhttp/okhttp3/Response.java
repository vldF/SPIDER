package okhttp3;

import org.jetbrains.research.kex.Intrinsics;

public class Response {
    private final int STATE$CONST$Response$CREATED = 0;

    private final int STATE$CONST$Response$CLOSED = 1;

    public int STATE;

    public okhttp3.ResponseBody body() {
        Intrinsics.kexAssert("id3", STATE != STATE$CONST$Response$CLOSED);
        ResponseBody tmpRes = new ResponseBody();
        tmpRes.STATE = 8;
        return tmpRes;
    }

    public void close() {
        Intrinsics.kexAssert("id4", STATE != STATE$CONST$Response$CLOSED);
        STATE = STATE$CONST$Response$CLOSED;
    }
}
