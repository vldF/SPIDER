library OkHttp;

types {
    OkHttpClient(okhttp3.OkHttpClient)
    RequestBuilder(okhttp3.Request.Builder);
    String(String);
    Request(okhttp3.Request);
    Response(okhttp3.Response);
    Client(okhttp3.OkHttpClient);
    Call(okhttp3.Call);
    ResponseBody(okhttp3.ResponseBody);
    RequestBody(okhttp3.RequestBody);
    HttpUrl(okhttp3.HttpUrl);

    URL(java.net.URL);
    List(java.util.ArrayList);
}

automaton Request {
    javapackage okhttp3;

    state created;

}

fun Request.Request() {
    result = Request(created);
}

fun Request.getBody() : RequestBody; // kotlin property (getter)

fun Request.header(name: String) : String;
fun Request.headers(name: String) : List<String>;
fun Request.newBuilder() : Builder {
    result = new Builder(created);
}


automaton Builder {
    javapackage okhttp3;

    state Created, HasURL;

    shift Created -> HasURL(url);
    shift HasURL -> self(delete, get, head, header, method, patch, post, put, removeHeader);
}

fun Builder.Builder() : Builder {
    result = new Builder();
}

fun Builder.build() : Request {
    result = new Request(Created);
}

fun Builder.delete() : Builder;

fun Builder.get() : Builder;

fun Builder.head() : Builder;

fun Builder.header(name: String, value: String) : Builder;

fun Builder.header(headers: Headers) : Builder;

fun Builder.method(method: String, body: RequestBody) : Builder;

fun Builder.patch(body: RequestBody) : Builder;

fun Builder.post(body: RequestBody) : Builder;

fun Builder.put(body: RequestBody) : Builder;

fun Builder.removeHeader(name: String) : Builder;

// todo: add `tag` function

fun Builder.url(url: HttpUrl) : Builder;

fun Builder.url(url: URL) : Builder;

automaton Request {
    javapackage okhttp3;
    state Created;
}

fun Request.Request() : Request {
    result = new Request(Created);
}


automaton OkHttpClient {
    javapackage okhttp3;

    state created;
}