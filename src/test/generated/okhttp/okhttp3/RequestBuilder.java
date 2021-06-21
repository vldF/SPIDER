package okhttp3;

import org.jetbrains.research.kex.Intrinsics;

public class RequestBuilder {
    private final int STATE$CONST$RequestBuilder$CREATED = 0;

    private final int STATE$CONST$RequestBuilder$URLSET = 1;

    private final int STATE$CONST$RequestBuilder$BUILT = 2;

    public int STATE = STATE$CONST$RequestBuilder$CREATED;

    public okhttp3.Request.Builder url(String urlValue) {
        Intrinsics.kexAssert("id0", STATE != STATE$CONST$RequestBuilder$BUILT);
        if (STATE == STATE$CONST$RequestBuilder$CREATED) {
            STATE = STATE$CONST$RequestBuilder$URLSET;
        } else {
            Intrinsics.kexAssert("id1", false);
        }
        return org.jetbrains.research.kex.Objects.kexUnknown();
    }

    public okhttp3.Request.Builder url(okhttp3.HttpUrl urlValue) {
        Intrinsics.kexAssert("id2", STATE != STATE$CONST$RequestBuilder$BUILT);
        if (STATE == STATE$CONST$RequestBuilder$CREATED) {
            STATE = STATE$CONST$RequestBuilder$URLSET;
        } else {
            Intrinsics.kexAssert("id3", false);
        }
        return org.jetbrains.research.kex.Objects.kexUnknown();
    }

    public okhttp3.Request build() {
        Intrinsics.kexAssert("id4", STATE != STATE$CONST$RequestBuilder$BUILT);
        if (STATE == STATE$CONST$RequestBuilder$URLSET) {
            STATE = STATE$CONST$RequestBuilder$BUILT;
        } else {
            Intrinsics.kexAssert("id5", false);
        }
        result = new Request();
        result.STATE = 4;
        return org.jetbrains.research.kex.Objects.kexUnknown();
    }
}
