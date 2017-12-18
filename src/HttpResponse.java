import constant.Code;
import constant.HttpHeaderConst;
import util.Log;
import util.TextUtils;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by lijian on 2017/12/8.
 */
public class HttpResponse implements Response {
    private static final String TAG = "HttpResponse";
    BufferedWriter writer;

    private HttpProtocol protocol;
    private int responseCode;
    private Headers headers;

    private String indextHtml = "HTTP/1.1 200 OK\r\n" +
            "Cache-Control: private\r\n" +
            "Content-Type: text/html; charset=utf8-8\r\n" +
            "Date: Fri, 08 Dec 2017 07:38:44 GMT\r\n" +
            "Content-Length: %d\r\n\r\n";

    public HttpResponse(HttpProtocol protocol, int responseCode, Headers headers) {
        this.protocol = protocol;
        this.responseCode = responseCode;
        this.headers = headers;
    }

    @Override
    public boolean writeResponse(OutputStream outputStream) throws IOException {
        if (writeResponseHead(outputStream)) {
            if (needWriteContent()) {
                outputStream.flush();
                String contentLength = String.format("%s: %s\r\n\r\n", HttpHeaderConst.CONTENT_LENGTH, String.valueOf(getContentLength()));
                outputStream.write(contentLength.getBytes());
                outputStream.flush();
                writeResponseContent(outputStream);
            } else {
                outputStream.close();
            }
        }
        return true;
    }

    @Override
    public boolean writeResponseHead(OutputStream stream) throws IOException {
        System.out.println("begin writeResponse =");
        String startLine = String.format("%s %d %s\r\n", protocol.toString(), responseCode, Code.msg(responseCode));
        writer = new BufferedWriter(new OutputStreamWriter(stream));
        writer.write(startLine);
        Log.debug(TAG, "startLine=" + startLine);
        StringBuffer buffer = new StringBuffer();
        Set<String> keys = headers.keySet();
        if (keys != null && keys.size() > 0) {
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                buffer.append(key).append(": ");
                List<String> values = headers.get(key);
                if (values != null && values.size() > 0) {
                    for (int i = 0; i < values.size(); i++) {
                        if (i > 0) {
                            buffer.append("; ");
                        }
                        String value = values.get(i);
                        buffer.append(value);
                    }
                    buffer.append("\r\n");
                }
            }
        }
        Log.debug(TAG, "header String =" + buffer.toString());
        writer.write(buffer.toString());
        writer.flush();
        stream.flush();
        return true;
    }

    @Override
    public long getContentLength() {
        return -1;
    }


    @Override
    public boolean needWriteContent() {
        return false;
    }

    @Override
    public boolean writeResponseContent(OutputStream stream) throws IOException {
        return false;
    }

    @Override
    public boolean addResponseHeader(String key, String value) {
        if (headers != null && !TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            headers.add(key, value);
            return true;
        }
        return false;
    }

    public Builder newBuilder() {
        return new Builder().setProtocol(protocol).setResponseCode(responseCode).setHeaders(headers);
    }

    public static class Builder {

        private HttpProtocol protocol = HttpProtocol.HTTP_1_1;
        private int responseCode = Code.HTTP_OK;
        private Headers headers = new Headers();

        public Builder setProtocol(HttpProtocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setResponseCode(int responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder setHeaders(Headers headers) {
            this.headers = headers;
            return this;
        }

        public Builder addHeader(String key, String value) {
            if (headers != null) {
                headers.add(key, value);
            }
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(protocol, responseCode, headers);
        }

    }
}
