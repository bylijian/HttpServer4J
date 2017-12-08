import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lijian on 2017/12/8.
 */
public final class HttpRequest {
    private BufferedReader reader;
    private String protocol;
    private HttpMethod method;
    private String path;


    public HttpRequest(InputStream stream) {
        this.reader = new BufferedReader(new InputStreamReader(stream));

    }

    public boolean decodeRequest() {
        boolean decodeSuccess = true;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String line;
            do {
                line = reader.readLine();

                method = HttpMethod.fromString(line);
                System.out.println("readHttpRequest line=" + line);
                stringBuilder.append(line).append("/n");
            } while (line != null && line.length() > 0);

        } catch (IOException e) {
            e.printStackTrace();
            decodeSuccess = false;
        } finally {
//            Util.closeQuietly(reader);
        }
        System.out.println("requtst=" + stringBuilder.toString());
        return decodeSuccess;
    }


}
