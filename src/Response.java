import java.io.IOException;
import java.io.OutputStream;

public interface Response {

    boolean writeResponse(OutputStream outputStream) throws IOException;

    boolean writeResponseHead(OutputStream stream) throws IOException;

    long getContentLength();

    boolean needWriteContent();

    boolean writeResponseContent(OutputStream stream) throws IOException;

    boolean addResponseHeader(String key, String value);

}
