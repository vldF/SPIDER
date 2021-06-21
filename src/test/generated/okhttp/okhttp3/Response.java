package okhttp3;

import org.jetbrains.research.kex.Intrinsics;

public class Response {
    private final int STATE$CONST$Response$CREATED = 0;

    private final int STATE$CONST$Response$CLOSED = 1;

    public int STATE = STATE$CONST$Response$CREATED;

    public okhttp3.ResponseBody body() {
        Intrinsics.kexAssert("id6", STATE != STATE$CONST$Response$CLOSED);
        result = new ResponseBody();
        result.STATE = 8;
        return org.jetbrains.research.kex.Objects.kexUnknown();
    }

    public void close() {
        Intrinsics.kexAssert("id7", STATE != STATE$CONST$Response$CLOSED);
        STATE = STATE$CONST$Response$CLOSED;
    }
}
