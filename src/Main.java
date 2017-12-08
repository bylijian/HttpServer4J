import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello HttpServer!");
        try {
            HttpServer server = new HttpServer(8008);
            server.acceptClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
