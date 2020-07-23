package dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * 对Proxy01 进行了优化
 * desc: Proxy01 代理的功能实现了, 但是像增强代码("前置""后置"通知)全都嵌套到代理类中, 不符合面向对象的思想
 * 因此,实现一个MyInterceptor接口  需要做什么增强,直接在拦截器接口实现
 */
public class Proxy02 {

    public static void main(String[] args) {

        List<MyInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new MyInterceptorImpl01());
        interceptors.add(new MyInterceptorImpl02());
        Test02Service service = (Test02Service) My02InvocationHandler.wrap(new Test02ServiceImpl(), interceptors);
        service.test();
    }


    interface MyInterceptor {
        /**
         * 具体拦截处理
         */
        void interceptor();
    }

    static class MyInterceptorImpl01 implements MyInterceptor {
        @Override
        public void interceptor() {
            System.out.println("前置通知");
        }
    }

    static class MyInterceptorImpl02 implements MyInterceptor {
        @Override
        public void interceptor() {
            System.out.println("后置通知");
        }
    }


    ////////  一下代码为Proxy01中的  仅仅对Handler做了修改  加了两个list,用来保存Myinterceptor (自定义增强业务逻辑)

    interface Test02Service {
        void test();
    }

    static class Test02ServiceImpl implements Test02Service {
        @Override
        public void test() {
            System.out.println("test02ServiceImpl print");
        }
    }

    static class My02InvocationHandler implements InvocationHandler {

        Object target;
        List<MyInterceptor> myInterceptors;

        public My02InvocationHandler(Object target, List<MyInterceptor> myInterceptors) {
            this.target = target;
            this.myInterceptors = myInterceptors;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for (MyInterceptor myInterceptor : myInterceptors) {
                myInterceptor.interceptor();
            }
            Object invoke = method.invoke(target, args);
            return invoke;
        }


        /**ø
         * 生成代理类 (这块可以提出)
         */
        public static Object wrap(Object object, List<MyInterceptor> myInterceptors) {
            return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(),
                    new My02InvocationHandler(object, myInterceptors));
        }
    }
}

