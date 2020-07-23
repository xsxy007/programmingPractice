package dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * j基于Proxy04  添加责任链
 */
public class Proxy05 {

    public static void main(String[] args) {
        Test04Service service = new Test04ServiceImpl();
        My04InterceptorImpl interceptor = new My04InterceptorImpl();
        My04InterceptorImpl1 interceptorImpl1 = new My04InterceptorImpl1();


        InterceptorChain chain = new InterceptorChain();
        chain.addInterceptor(interceptor);
        chain.addInterceptor(interceptorImpl1);
        Test04Service o = (Test04Service)chain.pluginAll(service);
        o.test();

    }



    //
    static class InterceptorChain {
        private List<My04Interceptor> chains = new ArrayList<>();

        public Object pluginAll(Object target){
            for (My04Interceptor chain : chains) {
                target = chain.plugin(target);
            }
            return target;
        }

        public void addInterceptor(My04Interceptor interceptor){
            chains.add(interceptor);
        }

        public List<My04Interceptor> getChains(){
            return Collections.unmodifiableList(chains);
        }
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
