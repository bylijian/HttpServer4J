import util.TextUtils;

/**
 * Created by lijian on 2017/12/11.
 */
public enum HttpProtocol {

    HTTP_1("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    UNKNOWN("");

    private final String protocol;

    HttpProtocol(String text) {
        protocol = text;
    }

    public static HttpProtocol fromString(String protocol) {
        HttpProtocol httpProtocol;
        if (TextUtils.isEmpty(protocol)) {
            return UNKNOWN;
        }
        switch (protocol) {
            case "HTTP/1.0":
                httpProtocol = HTTP_1;
                break;
            case "HTTP/1.1":
                httpProtocol = HTTP_1_1;
                break;
            default:
                httpProtocol = UNKNOWN;
        }
        return httpProtocol;
    }

    @Override
    public String toString() {
        return protocol;
    }
}
