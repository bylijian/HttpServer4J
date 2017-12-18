import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;

public interface Request {
    Headers getRequestHeaders() throws IOException;

    String readLine() throws IOException;

    String getStartLine();

    HttpProtocol getProtocol();

    HttpMethod getMethod();

    String getUrl();
}
