import java.io.IOException;
import java.net.Socket;

/**
 * Created by lijian on 2017/12/8.
 */
public class Http1Connection {
    Socket socket;

    HttpRequest request;
    Response response;

    public Http1Connection(Socket socket) throws IOException {
        this.socket = socket;
        request = new HttpRequest(socket.getInputStream());
    }

    public String getRequestStartLine() {
        return request.getStartLine();
    }

    public boolean writeResponse(Response response) throws IOException {
        if (response != null) {
            response.writeResponse(socket.getOutputStream());
            return true;
        }
        return false;
    }


}
