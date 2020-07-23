package dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Proxy02 虽然进行了优化, 但是只能做到前置通知
 * 再优化: 把拦截对象进行封装, 最为拦截方法的参数, 这样就能做到前后通知, 并且能在拦截方法中对参数进行处理
 */
public class Proxy03 {
    public static void main(String[] args) {
        Test03Service service = (Test03Service) My03InvocationHandler.wrap(new Test03ServiceImpl(), new My03InterceptorImpl());
        service.test();
    }


    ////// 需要代理的类
    interface Test03Service {
        void test();
    }

    static class Test03ServiceImpl implements Test03Service {
        @Override
        public void test() {
            System.out.println("test03ServiceImpl print");
        }
    }

    ///// 对拦截对象进行封装
    static class MyInvocation {
        private Object target;
        private Method method;
        private Object[] args;


        // 执行目标方法
        public Object process() throws Exception {
            return method.invoke(target, args);
        }

        public MyInvocation(Object target, Method method, Object[] args) {
            this.target = target;
            this.method = method;
            this.args = args;
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

        public Object[] getArgs() {
            return args;
        }

        public void setArgs(Object[] args) {
            this.args = args;
        }
    }

    ///// 自定义拦截器 用来写业务代码
    interface My03Interceptor {
        Object interceptor(MyInvocation invocation) throws Exception;
    }

    static class My03InterceptorImpl implements My03Interceptor {
        @Override
        public Object interceptor(MyInvocation invocation) throws Exception {
            System.out.println("前置通知");
            Object process = invocation.process();
            System.out.println("后置通知");
            return process;
        }
    }


    /////
    static class My03InvocationHandler implements InvocationHandler {
        Object target;
        My03Interceptor interceptor;

        public My03InvocationHandler(Object target, My03Interceptor interceptor) {
            this.target = target;
            this.interceptor = interceptor;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            MyInvocation invocation = new MyInvocation(target, method, args);
            return interceptor.interceptor(invocation);
        }


        //
        public static Object wrap(Object object, My03Interceptor in) {
            return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(),
                    new My03InvocationHandler(object, in));
        }
    }


}
