import constant.Code;
import constant.HttpHeaderConst;
import util.TimeUtil;

public class BadRequestInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) {
        Request request = chain.getRequest();
        if (request == null || request.getMethod() == null || request.getProtocol() == null) {
            return new HttpResponse.Builder()
                    .setResponseCode(Code.HTTP_BAD_REQUEST)
                    .addHeader(HttpHeaderConst.DATE, TimeUtil.getGMT())
                    .build();
        }
        return chain.proceedNext();
    }
}
