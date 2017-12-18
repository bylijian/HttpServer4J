

import util.Log;
import util.Util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by lijian on 2017/12/8.
 */
public class HttpServer {
    private static final String TAG = "HttpServer";
    private static final int DEFAULT_SERVER_PORT = 80;
    private static final int MAX_ATTEMPT_TIMES = 5;
    private final int serverPort;

    private String httpDocs = "htdocs/";
    private ServerSocket serverSocket;
    private int attemptTimes = 0;
    private boolean closeServer = false;

    private ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), Util.threadFactory("Http Server", false));

    private List<Interceptor> interceptors;


    public HttpServer() throws IOException {
        this(DEFAULT_SERVER_PORT);
    }

    public HttpServer(int serverPort) throws IOException {
        this.serverPort = serverPort;
        initInterceptors();
        newServerSocket(serverPort);
    }

    private void initInterceptors() {
        interceptors = new ArrayList<>();
        interceptors.add(new BadRequestInterceptor());
        interceptors.add(new FileInterceptor());
        interceptors.add(new InternalServerErrorInterceptor());
    }

    private void newServerSocket(int serverPort) throws IOException {
        serverSocket = new ServerSocket(serverPort);
    }

    public void setHttpDocs(String httpDocs) {
        this.httpDocs = httpDocs;
    }

    public boolean start() throws IOException {
        if (serverSocket != null) {
            while (!closeServer) {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> {
                    try {
                        Http1Connection connection = new Http1Connection(socket);
                        Log.debug(TAG, connection.request.getStartLine());
                        Log.debug(TAG, connection.request.getMethod().toString());
                        Log.debug(TAG, connection.request.getUrl());
                        Log.debug(TAG, connection.request.getProtocol().toString());

                        Log.debug(TAG, connection.request.getRequestHeaders().toString());
                        Log.debug(TAG, "connection arrive");
                        HttpChain chain = new HttpChain(interceptors, 0, connection.request);
                        Response response = chain.proceedNext();
                        connection.writeResponse(response);
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
            start();
        }
        return false;

    }

    public void closeServer(boolean shutdownNow) {
        closeServer = true;
        if (shutdownNow) {
            executorService.shutdownNow();
        } else {
            executorService.shutdown();
        }
    }

}
