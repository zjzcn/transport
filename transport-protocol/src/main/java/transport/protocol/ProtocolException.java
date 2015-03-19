/**
 *
 * 日    期：12-2-13
 */
package transport.protocol;

/**
 * <pre>
 *   ROP的异常。
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class ProtocolException extends RuntimeException {
    public ProtocolException() {
    }

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }
}

