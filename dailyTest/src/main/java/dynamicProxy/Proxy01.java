package dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 最基础的代理
 */
public class Proxy01 {


    public static void main(String[] args) {
        TestService service =  (TestService)MyInvocationHandler.wrap(new TestServiceImpl());
        service.test();
    }



    static interface TestService {
        void test();
    }

    static class TestServiceImpl implements TestService{
        @Override
        public void test() {
            System.out.println("impl print");
        }
    }

    static class MyInvocationHandler implements InvocationHandler {
        Object target;

        public MyInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("前置通知");
            Object invoke = method.invoke(target, args);
            System.out.println("后置通知");
            return invoke;
        }

        public static Object wrap (Object object) {
            return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(),
                    new MyInvocationHandler(object));
        }
    }
}
