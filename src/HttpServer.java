import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Created by lijian on 2017/12/8.
 */
public class HttpServer {

    private static final int DEFAULT_SERVER_PORT = 80;
    private static final int MAX_ATTEMPT_TIMES = 5;
    private final int serverPort;

    private String httpDocs = "htdocs/";
    private ServerSocket serverSocket;
    private int attemptTimes = 0;
    private boolean closeServer = false;

    ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), Util.threadFactory("Http Server", false));

    public HttpServer() throws IOException {
        this(DEFAULT_SERVER_PORT);
    }

    public HttpServer(int serverPort) throws IOException {
        this.serverPort = serverPort;
        newServerSocket(serverPort);
    }

    private void newServerSocket(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
    }

    public void setHttpDocs(String httpDocs) {
        this.httpDocs = httpDocs;
    }

    public boolean acceptClientSocket() throws IOException {
        if (serverSocket != null) {
            while (!closeServer) {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> {
                    try {
                        Http1Connection connection = new Http1Connection(socket);
                        connection.readRequest();
                        connection.writeResponse();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

                });
            }
        } else {
            ++attemptTimes;
            if (attemptTimes > MAX_ATTEMPT_TIMES) {
                throw new RuntimeException("error acceptClientSocket because socket is null");
            }
            newServerSocket(serverPort);
            acceptClientSocket();
        }
        return false;

    }

}
