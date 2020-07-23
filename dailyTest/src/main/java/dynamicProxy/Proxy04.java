package dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * proxy03  能够实现前后通知, 但是对于待增强的类来说  只需要知道他插入了什么拦截器即可
 */
public class Proxy04 {

    public static void main(String[] args) {
        Test04Service service = new Test04ServiceImpl();
        My04InterceptorImpl interceptor = new My04InterceptorImpl();
        Test04Service plugin = (Test04Service) interceptor.plugin(service);

        My04InterceptorImpl1 interceptorImpl1 = new My04InterceptorImpl1();
        Test04Service plugin1 = (Test04Service) interceptorImpl1.plugin(plugin);

        plugin1.test();
    }


    ////// 一下代码基本就是proxy03的
    interface Test04Service {
        void test();
    }

    static class Test04ServiceImpl implements Test04Service {
        @Override
        public void test() {
            System.out.println("test04Serviceimpl print");
        }
    }

    /// 对拦截对象做了封装
    static class My04Invocation {
        private Object target;
        private Method method;
        private Object[] args;

        // 执行目标方法
        public Object process() throws Exception {
            return method.invoke(target, args);
        }

        public My04Invocation(Object target, Method method, Object[] args) {
            this.target = target;
            this.method = method;
            this.args = args;
        }
    }

    /// 拦截器 用来写业务代码
    interface My04Interceptor {
        Object interceptor(My04Invocation invocation) throws Exception;

        // 插入目标类
        Object plugin(Object target);
    }

    static class My04InterceptorImpl implements My04Interceptor {
        @Override
        public Object interceptor(My04Invocation invocation) throws Exception {
            System.out.println("前置通知");
            Object process = invocation.process();
            System.out.println("后置通知");
            return process;
        }

        @Override
        public Object plugin(Object target) {
            return TargetProxy.wrap(target, this);
        }
    }

    static class My04InterceptorImpl1 implements My04Interceptor {
        @Override
        public Object interceptor(My04Invocation invocation) throws Exception {
            System.out.println("前置通知1");
            Object process = invocation.process();
            System.out.println("后置通知1");
            return process;
        }

        @Override
        public Object plugin(Object target) {
            return TargetProxy.wrap(target, this);
        }
    }

    /// handler
    static class My04InvocationHandler implements InvocationHandler {
        Object target;
        My04Interceptor interceptor;

        public My04InvocationHandler(Object target, My04Interceptor interceptor) {
            this.target = target;
            this.interceptor = interceptor;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            My04Invocation invocation = new My04Invocation(target, method, args);
            return interceptor.interceptor(invocation);
        }
    }

    /// 生成代理工具类

    static class TargetProxy {
        static Object wrap(Object target, My04Interceptor interceptor) {
            return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
                    new My04InvocationHandler(target, interceptor));
        }
    }
}
