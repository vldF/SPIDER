package okhttp3;

import org.jetbrains.research.kex.Intrinsics;
import org.jetbrains.research.kex.Objects;

public class RequestBuilder {
    private final int STATE$CONST$RequestBuilder$CREATED = 0;

    private final int STATE$CONST$RequestBuilder$URLSET = 1;

    private final int STATE$CONST$RequestBuilder$BUILT = 2;

    public int STATE;

    public RequestBuilder() {
        STATE = STATE$CONST$RequestBuilder$CREATED;
    }

    public RequestBuilder() {
        STATE = STATE$CONST$RequestBuilder$CREATED;
    }

    public okhttp3.Request.Builder url(String urlValue) {
        Intrinsics.kexAssert("id0", STATE != STATE$CONST$RequestBuilder$BUILT);
        if (STATE == STATE$CONST$RequestBuilder$CREATED) {
            STATE = STATE$CONST$RequestBuilder$URLSET;
        } else {
            Intrinsics.kexAssert("id7", false);
        }
        return Objects.kexUnknown();
    }

    public okhttp3.Request.Builder url(okhttp3.HttpUrl urlValue) {
        Intrinsics.kexAssert("id1", STATE != STATE$CONST$RequestBuilder$BUILT);
        if (STATE == STATE$CONST$RequestBuilder$CREATED) {
            STATE = STATE$CONST$RequestBuilder$URLSET;
        } else {
            Intrinsics.kexAssert("id8", false);
        }
        return Objects.kexUnknown();
    }

    public okhttp3.Request build() {
        Intrinsics.kexAssert("id2", STATE != STATE$CONST$RequestBuilder$BUILT);
        if (STATE == STATE$CONST$RequestBuilder$URLSET) {
            STATE = STATE$CONST$RequestBuilder$BUILT;
        } else {
            Intrinsics.kexAssert("id9", false);
        }
        Request tmpRes = new Request();
        tmpRes.STATE = 4;
        return tmpRes;
    }
}
