package okhttp3;

import org.jetbrains.research.kex.Intrinsics;

public class ResponseBody {
    private final int STATE$CONST$ResponseBody$CREATED = 0;

    private final int STATE$CONST$ResponseBody$RESULTRETRIEVED = 1;

    public int STATE = STATE$CONST$ResponseBody$CREATED;

    public String string() {
        Intrinsics.kexAssert("id8", STATE != STATE$CONST$ResponseBody$RESULTRETRIEVED);
        STATE = STATE$CONST$ResponseBody$RESULTRETRIEVED;
        return org.jetbrains.research.kex.Objects.kexUnknown();
    }
}
