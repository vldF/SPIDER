package okhttp3;

public class Response {
    private final int STATE$CONST$Response$CREATED = 0;

    private final int STATE$CONST$Response$CLOSED = 1;

    public int STATE = STATE$CONST$Response$CREATED;

    public okhttp3.ResponseBody body() {
        result = new ResponseBody();
        result.STATE = 8;
        return org.jetbrains.research.kex.Objects.kexUnknown();
    }

    public void close() {
        STATE = STATE$CONST$Response$CLOSED;
    }
}
