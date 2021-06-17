package okhttp3;

public class ResponseBody {
    private final int STATE$CONST$ResponseBody$CREATED = 0;

    private final int STATE$CONST$ResponseBody$RESULTRETRIEVED = 1;

    public int STATE = STATE$CONST$ResponseBody$CREATED;

    public String string() {
        STATE = STATE$CONST$ResponseBody$RESULTRETRIEVED;
        return org.jetbrains.research.kex.Objects.kexUnknown();
    }
}
