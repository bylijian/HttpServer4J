

public interface Interceptor {

    Response intercept(Chain chain);

    interface Chain {
        Response proceedNext();

        Request getRequest();
    }
}
