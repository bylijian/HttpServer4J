import com.sun.javafx.binding.StringFormatter;

import java.io.*;

/**
 * Created by lijian on 2017/12/8.
 */
public class Http1Response {
    BufferedWriter writer;
    private String indextHtml = "HTTP/1.1 200 OK\r\n" +
            "Cache-Control: private\r\n" +
            "Content-Type: text/html; charset=utf8-8\r\n" +
            "Date: Fri, 08 Dec 2017 07:38:44 GMT\r\n" +
            "Content-Length: %d\r\n\r\n";

    public boolean writeResponse(OutputStream stream) throws IOException {
        System.out.println("begin writeResponse =");
        writer = new BufferedWriter(new OutputStreamWriter(stream));
        File file = new File("G:\\Private\\HttpServer\\src\\htdocs\\index.html");
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        do {
            line = bufferedReader.readLine();
            if (line != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        while (line != null);

        String content = String.format(indextHtml, stringBuilder.toString().toCharArray().length).concat(stringBuilder.toString());
        System.out.println("Response =" + content);
        writer.write(content);
        writer.flush();
        writer.close();
        return true;
    }
}
