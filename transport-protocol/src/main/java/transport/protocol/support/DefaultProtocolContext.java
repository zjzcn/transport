package transport.protocol.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import transport.buffer.ChannelBuffer;
import transport.protocol.*;
import transport.protocol.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2014/6/18.
 */
public class DefaultProtocolContext implements ProtocolContext {

    private static final Logger logger = LoggerFactory.getLogger(DefaultProtocolContext.class);

    private final Map<String, MethodInvoker> encoderMap = new HashMap<String, MethodInvoker>();

    private final Set<String> encoderMethods = new HashSet<String>();

    private final Map<String, MethodInvoker> decoderMap = new HashMap<String, MethodInvoker>();

    private final Set<String> decoderMethods = new HashSet<String>();

    private final Map<String, MethodInvoker> handlerMap = new HashMap<String, MethodInvoker>();

    private final Set<String> handlerMethods = new HashSet<String>();

    private MethodInvoker headEncoderMethod;

    private MethodInvoker headDecoderMethod;

    private final String protocol;

    public DefaultProtocolContext(String protocol, ApplicationContext context){
        Assert.hasText(protocol);
        Assert.notNull(context);

        this.protocol = protocol;
        registerFromContext(context);
    }

    @Override
    public MethodInvoker getEncoderMethod(String msgId, String version) {
        Assert.hasText(msgId);
        Assert.hasText(version);

        return encoderMap.get(buildKey(msgId, version));
    }

    @Override
    public MethodInvoker getDecoderMethod(String msgId, String version) {
        Assert.hasText(msgId);
        Assert.hasText(version);

        return decoderMap.get(buildKey(msgId, version));
    }

    @Override
    public MethodInvoker getHandlerMethod(String msgId, String version) {
        Assert.hasText(msgId);
        Assert.hasText(version);

        return handlerMap.get(buildKey(msgId, version));
    }

    @Override
    public MethodInvoker getHeadEncoderMethod() {
        return headEncoderMethod;
    }

    @Override
    public MethodInvoker getHeadDecoderMethod() {
        return headDecoderMethod;
    }

    private void registerFromContext(final ApplicationContext context) throws BeansException {
        logger.debug("对Spring上下文中的Bean进行扫描，查找ROP服务方法: " + context);

        String[] beanNames = context.getBeanNamesForType(Object.class);
        for (final String beanName : beanNames) {
            Class<?> handlerType = context.getType(beanName);
            //只对标注 ServiceMethodBean的Bean进行扫描
            ReflectionUtils.doWithMethods(handlerType, new ReflectionUtils.MethodCallback() {
                        public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                            ReflectionUtils.makeAccessible(method);

                            if (AnnotationUtils.findAnnotation(method, EncoderMapping.class) != null) {
                                if (method.getParameterTypes().length > 1) {//handler method's parameter
                                    throw new ProtocolException(method.getDeclaringClass().getName() + "." + method.getName()
                                            + "的入参只能是" + Request.class.getName() + "或无入参。");
                                } else if (method.getParameterTypes().length == 1) {
                                    Class<?> paramType = method.getParameterTypes()[0];
                                    if (!ClassUtils.isAssignable(Message.class, paramType)) {
                                        throw new ProtocolException(method.getDeclaringClass().getName() + "." + method.getName()
                                                + "的入参必须是" + Message.class.getName());
                                    }
                                } else {
                                    logger.info(method.getDeclaringClass().getName() + "." + method.getName() + "无入参");
                                }
                                EncoderMapping ann = AnnotationUtils.findAnnotation(method, EncoderMapping.class);
//                                String protocol = ann.protocol();
                                String msgId = ann.msgId();
                                String version = ann.version();

                                MethodInvoker methodInvoker = new MethodInvoker();
                                methodInvoker.setTarget(context.getBean(beanName)); //holder
                                methodInvoker.setMethod(method); //holder method

                                encoderMap.put(buildKey(msgId, version), methodInvoker);
                                encoderMethods.add(msgId);

                                if (logger.isDebugEnabled()) {
                                    logger.debug("注册服务方法：" + method.getDeclaringClass().getCanonicalName() +
                                            "#" + method.getName() + "(..)");
                                }
                            } else if (AnnotationUtils.findAnnotation(method, DecoderMapping.class) != null) {
                                if (method.getParameterTypes().length > 1) {//handler method's parameter
                                    throw new ProtocolException(method.getDeclaringClass().getName() + "." + method.getName()
                                            + "的入参只能是" + Request.class.getName() + "或无入参。");
                                } else if (method.getParameterTypes().length == 1) {
                                    Class<?> paramType = method.getParameterTypes()[0];
                                    if (!ClassUtils.isAssignable(Message.class, paramType)) {
                                        throw new ProtocolException(method.getDeclaringClass().getName() + "." + method.getName()
                                                + "的入参必须是" + Message.class.getName());
                                    }
                                } else {
                                    logger.info(method.getDeclaringClass().getName() + "." + method.getName() + "无入参");
                                }
                                DecoderMapping ann = AnnotationUtils.findAnnotation(method, DecoderMapping.class);
//                                String protocol = ann.protocol();
                                String msgId = ann.msgId();
                                String version = ann.version();

                                MethodInvoker methodInvoker = new MethodInvoker();
                                methodInvoker.setTarget(context.getBean(beanName)); //holder
                                methodInvoker.setMethod(method); //holder method

                                decoderMap.put(buildKey(msgId, version), methodInvoker);
                                decoderMethods.add(msgId);

                                if (logger.isDebugEnabled()) {
                                    logger.debug("注册服务方法：" + method.getDeclaringClass().getCanonicalName() +
                                            "#" + method.getName() + "(..)");
                                }
                            } else if (AnnotationUtils.findAnnotation(method, HandlerMapping.class) != null) {
                                if (method.getParameterTypes().length > 1) {//handler method's parameter
                                    throw new ProtocolException(method.getDeclaringClass().getName() + "." + method.getName()
                                            + "的入参只能是" + Request.class.getName() + "或无入参。");
                                } else if (method.getParameterTypes().length == 1) {
                                    Class<?> paramType = method.getParameterTypes()[0];
                                    if (!ClassUtils.isAssignable(Request.class, paramType)) {
                                        throw new ProtocolException(method.getDeclaringClass().getName() + "." + method.getName()
                                                + "的入参必须是" + Request.class.getName());
                                    }
                                } else {
                                    logger.info(method.getDeclaringClass().getName() + "." + method.getName() + "无入参");
                                }
                                HandlerMapping ann = AnnotationUtils.findAnnotation(method, HandlerMapping.class);
//                                String protocol = ann.protocol();
                                String msgId = ann.msgId();
                                String version = ann.version();

                                MethodInvoker methodInvoker = new MethodInvoker();
                                methodInvoker.setTarget(context.getBean(beanName)); //holder
                                methodInvoker.setMethod(method); //holder method

                                handlerMap.put(buildKey(msgId, version), methodInvoker);
                                handlerMethods.add(msgId);

                                if (logger.isDebugEnabled()) {
                                    logger.debug("注册服务方法：" + method.getDeclaringClass().getCanonicalName() +
                                            "#" + method.getName() + "(..)");
                                }
                            } else if (AnnotationUtils.findAnnotation(method, MessageHeadEncoder.class) != null) {
                                if (method.getParameterTypes().length > 1) {//handler method's parameter
                                    throw new ProtocolException(method.getDeclaringClass().getName() + "." + method.getName()
                                            + "的入参只能是" + Request.class.getName() + "或无入参。");
                                } else if (method.getParameterTypes().length == 1) {
                                    Class<?> paramType = method.getParameterTypes()[0];
                                    if (!ClassUtils.isAssignable(Message.class, paramType)) {
                                        throw new ProtocolException(method.getDeclaringClass().getName() + "." + method.getName()
                                                + "的入参必须是" + Request.class.getName());
                                    }
                                } else {
                                    logger.info(method.getDeclaringClass().getName() + "." + method.getName() + "无入参");
                                }

                                if(headEncoderMethod != null){
                                    throw new ProtocolException("@" + MessageHeadEncoder.class.getName() + "must have one:" + method.getDeclaringClass().getName() + "." + method.getName()
                                            + ", " + headEncoderMethod.getTarget().getClass() + "." + headEncoderMethod.getMethod().getName());
                                }
                                headEncoderMethod = new MethodInvoker();
                                headEncoderMethod.setTarget(context.getBean(beanName)); //holder
                                headEncoderMethod.setMethod(method); //holder method

                                if (logger.isDebugEnabled()) {
                                    logger.debug("注册服务方法：" + method.getDeclaringClass().getCanonicalName() +
                                            "#" + method.getName() + "(..)");
                                }
                            } else if (AnnotationUtils.findAnnotation(method, MessageHeadDecoder.class) != null) {
                                if (method.getParameterTypes().length > 1) {//handler method's parameter
                                    throw new ProtocolException(method.getDeclaringClass().getName() + "." + method.getName()
                                            + "的入参只能是" + Request.class.getName() + "或无入参。");
                                } else if (method.getParameterTypes().length == 1) {
                                    Class<?> paramType = method.getParameterTypes()[0];
                                    if (!ClassUtils.isAssignable(ChannelBuffer.class, paramType)) {
                                        throw new ProtocolException(method.getDeclaringClass().getName() + "." + method.getName()
                                                + "的入参必须是" + ChannelBuffer.class.getName());
                                    }
                                } else {
                                    logger.info(method.getDeclaringClass().getName() + "." + method.getName() + "无入参");
                                }

                                if(headDecoderMethod != null){
                                    throw new ProtocolException("@" + MessageHeadDecoder.class.getName() + "must have one:" + method.getDeclaringClass().getName() + "." + method.getName()
                                            + ", " + headDecoderMethod.getTarget().getClass() + "." + headDecoderMethod.getMethod().getName());
                                }
                                headDecoderMethod = new MethodInvoker();
                                headDecoderMethod.setTarget(context.getBean(beanName)); //holder
                                headDecoderMethod.setMethod(method); //holder method

                                if (logger.isDebugEnabled()) {
                                    logger.debug("注册服务方法：" + method.getDeclaringClass().getCanonicalName() +
                                            "#" + method.getName() + "(..)");
                                }
                            }
                        }
                    }, new ReflectionUtils.MethodFilter() {
                        public boolean matches(Method method) {
                            return (AnnotationUtils.findAnnotation(method, EncoderMapping.class) != null
                                    && protocol.equals(AnnotationUtils.findAnnotation(method, EncoderMapping.class).protocol()))
                                    || (AnnotationUtils.findAnnotation(method, DecoderMapping.class) != null
                                    && protocol.equals(AnnotationUtils.findAnnotation(method, DecoderMapping.class).protocol()))
                                    || (AnnotationUtils.findAnnotation(method, HandlerMapping.class) != null
                                    && protocol.equals(AnnotationUtils.findAnnotation(method, HandlerMapping.class).protocol()))
                                    || (AnnotationUtils.findAnnotation(method, MessageHeadEncoder.class) != null
                                    && protocol.equals(AnnotationUtils.findAnnotation(method, MessageHeadEncoder.class).protocol()))
                                    || (AnnotationUtils.findAnnotation(method, MessageHeadDecoder.class) != null
                                    && protocol.equals(AnnotationUtils.findAnnotation(method, MessageHeadDecoder.class).protocol()));
                        }
                    }

            );
        }

        if (context.getParent() != null){
            registerFromContext(context.getParent());
        }

        logger.info("共注册: Encoder: " + encoderMap.size() + ", Decoder: " + decoderMap.size() + ", Handler:" + handlerMap.size());
    }

    private String buildKey(String msgId, String version){
        return msgId + "#" + version;
    }
}

