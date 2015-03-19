package transport.protocol;

/**
 * Created by Administrator on 2014/6/17.
 */
public interface ProtocolContext {
    MethodInvoker getEncoderMethod(String msgId, String version);

    MethodInvoker getDecoderMethod(String msgId, String version);

    MethodInvoker getHandlerMethod(String msgId, String version);

    MethodInvoker getHeadEncoderMethod();

    MethodInvoker getHeadDecoderMethod();
}
