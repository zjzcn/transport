package transport.protocol;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInvoker {
    private static final Logger logger = LoggerFactory.getLogger(MethodInvoker.class);
    //处理器对象
    private Object target;

    //处理器的处理方法
    private Method method;

    public Object invoke(Object arg){
        Object ret = null;
        try {
            ret = method.invoke(target, arg);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        logger.debug("Executed method invoke:" + target.getClass().getName() + "." + method.getName());

        return ret;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}

