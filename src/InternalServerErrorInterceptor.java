import constant.Code;
import constant.HttpHeaderConst;
import util.TimeUtil;

/**
 * Created by lijian on 2017/12/15.
 */
public class InternalServerErrorInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) {
        Response response = new HttpResponse.Builder()
                .setProtocol(HttpProtocol.HTTP_1_1)
                .setResponseCode(Code.HTTP_INTERNAL_ERROR)
                .addHeader(HttpHeaderConst.DATE, TimeUtil.getGMT())
                .build();
        return response;
    }
}
