import constant.Code;
import constant.HttpHeaderConst;
import util.Log;
import util.TimeUtil;

import java.io.File;


/**
 * 只支持text/html ,text/plain;
 * Created by lijian on 2017/12/15.
 */
public class FileInterceptor implements Interceptor {
    private static final String TAG = "FileInterceptor";

    @Override
    public Response intercept(Chain chain) {
        Log.debug(TAG, "intercept()");
        Request request = chain.getRequest();
        String url = request.getUrl();
        File file = null;
        if (url != null) {
            if (url.equals("/") || url.equals("")) {
                file = new File("/Private/HttpServer/src/htdocs", "/index.html");
            } else {
                file = new File("/Private/HttpServer/src/htdocs", url);
            }
        }
        Log.debug(TAG, "FileInterceptor file=" + file);
        if (file != null && file.canRead()) {
            Log.debug(TAG, file.getAbsolutePath());
            Headers headers = new Headers();
            headers.add(HttpHeaderConst.CACHE_CONTROL, "private");
            headers.add(HttpHeaderConst.DATE, TimeUtil.getGMT());
            if (file.getAbsolutePath().endsWith(".html") || file.getAbsolutePath().endsWith(".htm")) {
                headers.add(HttpHeaderConst.CONTENT_TYPE, "text/html; charset=utf8-8");
            } else {
                headers.add(HttpHeaderConst.CONTENT_TYPE, "text/plain;");
            }

            return new FileHttpResponse(file, HttpProtocol.HTTP_1_1, Code.HTTP_OK, headers);
        }
        if (file != null && !file.exists()) {
            return new HttpResponse.Builder()
                    .setResponseCode(Code.HTTP_NOT_FOUND)
                    .setProtocol(HttpProtocol.HTTP_1_1)
                    .build();
        }
        return chain.proceedNext();
    }

}
