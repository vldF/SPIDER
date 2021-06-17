library OkHttp;

types {
    RequestBuilder(okhttp3.Request.Builder);
    String(String);
    Request(okhttp3.Request);
    Response(okhttp3.Response);
    Client(okhttp3.OkHttpClient);
    Call(okhttp3.Call);
    ResponseBody(okhttp3.ResponseBody);
    HttpUrl(okhttp3.HttpUrl);
}

fun RequestBuilder.RequestBuilder() : RequestBuilder {
    result = new RequestBuilder(Created);
}

fun RequestBuilder.url(urlValue: String) : RequestBuilder;

fun RequestBuilder.url(urlValue: HttpUrl) : RequestBuilder;

fun RequestBuilder.build() : Request {
    result = new Request(Created);
}

fun Client.Client() : Client {
    result = new Client(Created);
}

fun Client.newCall(request: Request) : Call {
    result = new Call(Created);
}

fun Call.execute() : Response {
    result = new Response(Created);
}

fun Response.body() : ResponseBody {
    result = new ResponseBody(Created);
}

fun ResponseBody.string() : String {
    //post("ONE", "String-result of response body should not be empty", result != null && !result.isEmpty());
}

fun Response.close();

automaton RequestBuilder {
    javapackage okhttp3;
    state Created, UrlSet;
    finishstate Built;

    shift Created->UrlSet (url);
    shift UrlSet->Built (build);
}

fun RequestBuilder.RequestBuilder() {
    result = new RequestBuilder(Created);
}

automaton Request {
    javapackage okhttp3;
    state Created;
}

fun Request.Request() {
    result = new Request(Created);
}

automaton Client {
    javapackage okhttp3;
    state Created;
}

fun Client.Client() {
    result = new Client(Created);
}

automaton Response {
    javapackage okhttp3;
    state Created;
    finishstate Closed;

    shift any->Closed (close);
}

fun Response.Response() {
    result = new Response(Created);
}

automaton ResponseBody {
    javapackage okhttp3;
    state Created;
    finishstate ResultRetrieved;

    shift any->ResultRetrieved (string);
}

fun ResponseBody.ResponseBody() {
    result = new ResponseBody(Created);
}

automaton Call {
    javapackage okhttp3;
    state Created;
    finishstate Executed;

    shift Created->Executed (execute);
}

fun Call.Call() {
    result = new Call(Created);
}