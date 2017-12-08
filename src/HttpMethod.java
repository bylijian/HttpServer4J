
/**
 * Created by lijian on 2017/12/8.
 */
public enum HttpMethod {

    GET("GET"),
    POST("POST"),

    UNKNOWN("");

    private final String string;

    HttpMethod(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    public static HttpMethod fromString(String string) {
        if (string == null || string.length() == 0) {
            return UNKNOWN;
        }
        HttpMethod method;

        switch (string) {
            case "GET":
                method = GET;
                break;
            case "POST":
                method = POST;
                break;
            default:
                method = UNKNOWN;

        }
        return method;
    }
}
