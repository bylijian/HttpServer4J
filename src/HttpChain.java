import java.util.List;

public class HttpChain implements Interceptor.Chain {
    private Request request;
    private int index = 0;
    private List<Interceptor> interceptors;

    public HttpChain(List<Interceptor> interceptors, int index, Request request) {
        this.interceptors = interceptors;
        this.index = index;
        this.request = request;
    }

    @Override
    public Response proceedNext() {
        Interceptor interceptor = interceptors.get(index);
        index++;
        HttpChain nextChain = null;
        if (index < interceptors.size()) {
            nextChain = new HttpChain(interceptors, index, request);
        }

        return interceptor.intercept(nextChain);
    }

    @Override
    public Request getRequest() {
        return request;
    }
}
