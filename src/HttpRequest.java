
import constant.SpecialAscii;
import util.TextUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lijian on 2017/12/8.
 */
public final class HttpRequest implements Request {
    private static final int BUF_LEN = 2048;

    private InputStream inputStream;

    private String startLine;
    private Headers headers;
    private HttpProtocol protocol;
    private HttpMethod method;
    private String url;


    private char[] buf = new char[BUF_LEN];
    private int pos;
    private StringBuffer lineBuf;


    public HttpRequest(InputStream stream) throws IOException {
        inputStream = stream;
        do {
            startLine = readLine();
            if (startLine == null) {
                return;
            }
        } while ("".equals(startLine));
        decodeStartLine();
    }

    private boolean decodeStartLine() {
        boolean success = false;
        if (!TextUtils.isEmpty(startLine)) {
            int spaceIndex = startLine.indexOf(" ");
            if (spaceIndex < 0) {
                return success;
            }
            method = HttpMethod.fromString(startLine.substring(0, spaceIndex));
            int lastSpaceIndex = startLine.lastIndexOf(" ");
            if (lastSpaceIndex < 0) {
                return success;
            }
            url = startLine.substring(spaceIndex + 1, lastSpaceIndex);
            protocol = HttpProtocol.fromString(startLine.substring(lastSpaceIndex + 1));

            success = true;
        }
        return success;
    }

    @Override
    public Headers getRequestHeaders() throws IOException {
        if (headers != null) {
            return headers;
        }
        headers = new Headers();
        char s[] = new char[10];
        int len = 0;

        int firstc = inputStream.read();

        // check for empty headers
        if (firstc == SpecialAscii.CR || firstc == SpecialAscii.LF) {
            int c = inputStream.read();
            if (c == SpecialAscii.CR || c == SpecialAscii.LF) {
                return headers;
            }
            s[0] = (char) firstc;
            len = 1;
            firstc = c;
        }

        while (firstc != SpecialAscii.LF && firstc != SpecialAscii.CR && firstc >= 0) {
            int keyend = -1;
            int c;
            boolean inKey = firstc > ' ';
            s[len++] = (char) firstc;
            parseloop:
            {
                while ((c = inputStream.read()) >= 0) {
                    switch (c) {
                        /*fallthrough*/
                        case ':':
                            if (inKey && len > 0)
                                keyend = len;
                            inKey = false;
                            break;
                        case '\t':
                            c = ' ';
                        case ' ':
                            inKey = false;
                            break;
                        case SpecialAscii.CR:
                        case SpecialAscii.LF:
                            firstc = inputStream.read();
                            if (c == SpecialAscii.CR && firstc == SpecialAscii.LF) {
                                firstc = inputStream.read();
                                if (firstc == SpecialAscii.CR)
                                    firstc = inputStream.read();
                            }
                            if (firstc == SpecialAscii.LF || firstc == SpecialAscii.CR || firstc > ' ')
                                break parseloop;
                            /* continuation */
                            c = ' ';
                            break;
                    }
                    if (len >= s.length) {
                        char ns[] = new char[s.length * 2];
                        System.arraycopy(s, 0, ns, 0, len);
                        s = ns;
                    }
                    s[len++] = (char) c;
                }
                firstc = -1;
            }
            while (len > 0 && s[len - 1] <= ' ')
                len--;
            String k;
            if (keyend <= 0) {
                k = null;
                keyend = 0;
            } else {
                k = String.copyValueOf(s, 0, keyend);
                if (keyend < len && s[keyend] == ':')
                    keyend++;
                while (keyend < len && s[keyend] <= ' ')
                    keyend++;
            }
            String v;
            if (keyend >= len)
                v = new String();
            else
                v = String.copyValueOf(s, keyend, len - keyend);

            if (headers.size() >= ServerConfig.getMaxReqHeaders()) {
                throw new IOException("Maximum number of request headers (" +
                        "sun.net.httpserver.maxReqHeaders) exceeded, " +
                        ServerConfig.getMaxReqHeaders() + ".");
            }

            headers.add(k, v);
            len = 0;
        }
        return headers;
    }

    /**
     * read a line from the stream returning as a String.
     * Not used for reading headers.
     */

    @Override
    public String readLine() throws IOException {
        boolean gotCR = false, gotLF = false;
        pos = 0;
        lineBuf = new StringBuffer();
        while (!gotLF) {
            int c = inputStream.read();
            if (c == -1) {
                return null;
            }
            if (gotCR) {
                if (c == SpecialAscii.LF) {
                    gotLF = true;
                } else {
                    gotCR = false;
                    consume(SpecialAscii.CR);
                    consume(c);
                }
            } else {
                if (c == SpecialAscii.CR) {
                    gotCR = true;
                } else {
                    consume(c);
                }
            }
        }
        lineBuf.append(buf, 0, pos);
        return new String(lineBuf);
    }

    private void consume(int c) {
        if (pos == BUF_LEN) {
            lineBuf.append(buf);
            pos = 0;
        }
        buf[pos++] = (char) c;
    }


    @Override
    public String getStartLine() {
        return startLine;
    }


    @Override
    public HttpProtocol getProtocol() {
        return protocol;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
