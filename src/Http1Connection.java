import java.io.IOException;
import java.net.Socket;

/**
 * Created by lijian on 2017/12/8.
 */
public class Http1Connection {
    Socket socket;

    HttpRequest request;
    Http1Response response;

    public Http1Connection(Socket socket) throws IOException {
        this.socket = socket;
        request = new HttpRequest(socket.getInputStream());
        response = new Http1Response();
    }

    public HttpRequest readRequest() {
        request.decodeRequest();
        return request;
    }

    public boolean writeResponse() throws IOException {
        return response.writeResponse(socket.getOutputStream());
    }

}
