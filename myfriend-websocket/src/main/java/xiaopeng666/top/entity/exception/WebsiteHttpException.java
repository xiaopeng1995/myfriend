package xiaopeng666.top.entity.exception;

/**
 * WEB异常
 * Created by cactus on 2015/11/23.
 */
public class WebsiteHttpException extends Exception {

    public WebsiteHttpException() {
    }

    public WebsiteHttpException(final String method, final String message) {
        super("METHOD:" + method + ";MSG:" + message);
    }

    public WebsiteHttpException(final Throwable cause) {
        super(cause);
    }

    public WebsiteHttpException(final String method, final String message, final Throwable cause) {
        super("METHOD:" + method + ";MSG:" + message, cause);
    }


}