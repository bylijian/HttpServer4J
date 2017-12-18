import constant.HttpHeaderConst;
import util.Log;

import java.io.*;

/**
 * Created by lijian on 2017/12/15.
 */
public class FileHttpResponse extends HttpResponse {
    private static final String TAG = "FileHttpResponse";
    private File file;
    private byte[] buf = new byte[1024];

    public FileHttpResponse(File file, HttpProtocol protocol, int responseCode, Headers headers) {
        super(protocol, responseCode, headers);
        this.file = file;
    }

    @Override
    public boolean writeResponseContent(OutputStream stream) throws IOException {
        Log.debug(TAG, "FileHttpResponse,writeResponseContent()");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            while (true) {
                int length = bufferedInputStream.read(buf);
                if (length < 0) {
                    break;
                }
                stream.write(buf, 0, length);
            }
            stream.flush();
            bufferedInputStream.close();
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean needWriteContent() {
        return true;
    }

    @Override
    public long getContentLength() {
        return file != null && file.canRead() ? file.length() : 0;
    }
}
