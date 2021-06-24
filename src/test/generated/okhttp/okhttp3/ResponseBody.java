package okhttp3;

import org.jetbrains.research.kex.Intrinsics;
import org.jetbrains.research.kex.Objects;

public class ResponseBody {
    private final int STATE$CONST$ResponseBody$CREATED = 0;

    private final int STATE$CONST$ResponseBody$RESULTRETRIEVED = 1;

    public int STATE;

    public String string() {
        Intrinsics.kexAssert("id5", STATE != STATE$CONST$ResponseBody$RESULTRETRIEVED);
        STATE = STATE$CONST$ResponseBody$RESULTRETRIEVED;
        return Objects.kexUnknown();
    }
}
