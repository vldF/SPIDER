package okhttp3;

public class Client {
    private final int STATE$CONST$Client$CREATED = 0;

    public int STATE;

    public okhttp3.Call newCall(okhttp3.Request request) {
        Call tmpRes = new Call();
        tmpRes.STATE = 10;
        return tmpRes;
    }
}
