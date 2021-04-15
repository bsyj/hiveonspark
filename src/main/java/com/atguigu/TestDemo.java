package com.atguigu;

import sun.misc.ProxyGenerator;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestDemo {
    public static void main(String[] args) throws Exception {
        IUserService userService = UserServiceFactory.getInstance();
        userService.findUser(new User());
        userService.updateUser(new User());
        // 对 UserService生成一个代理类，
        byte[] bytes = ProxyGenerator.generateProxyClass("Proxy$", new Class[]{UserService.class});
        new FileOutputStream("./Proxy$.class").write(bytes);
    }
}

// 用户的增删改查
class User {}

interface IUserService {

    public User findUser(User user);

    public boolean addUser(User user);

    public Boolean updateUser(User user);

    public boolean delUser(User user);
}

class UserServiceFactory {
    // 返回IUserService接口对象
    public static IUserService getInstance() {
        return ((IUserService) new MyProxy().bind(new UserService()));
    }
}

class UserService implements IUserService {

    @Override
    public User findUser(User user) {
        System.out.println("UserService.findUser");
        return null;
    }

    @Override
    public boolean addUser(User user) {
        System.out.println("UserService.addUser");
        return false;
    }

    @Override
    public Boolean updateUser(User user) {
        System.out.println("UserService.updateUser");
        return null;
    }

    @Override
    public boolean delUser(User user) {
        System.out.println("UserService.delUser");
        return false;
    }
}


class ProxyUserService implements IUserService {
    //被代理类的实例
    private IUserService userService;

    public ProxyUserService(IUserService userService) {
        this.userService = userService;
    }
    public User findUser(User user) {
        System.out.println("增加的业务逻辑，记录日志");
        userService.findUser(user);
        return null;
    }

    public boolean addUser(User user) {
        System.out.println("增加的业务逻辑，记录日志，添加事务控制");
        userService.addUser(user);
        return true;
    }

    public Boolean updateUser(User user) {
        System.out.println("增加的业务逻辑，记录日志，添加事务控制");
        userService.updateUser(user);
        return true;
    }

    public boolean delUser(User user) {
        System.out.println("增加的业务逻辑，记录日志，添加事务控制");
        userService.delUser(user);
        return true;
    }
}

class MyProxy {
//    被代理类的对象
    private Object realObj;
    public  Object bind(Object realObj) {
        this.realObj = realObj;
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true"); //设置系统属性
        Object proxyInstance = Proxy.newProxyInstance(
                realObj.getClass().getClassLoader(),
                realObj.getClass().getInterfaces(),
                new MyInvocationHandler()
        );
        return proxyInstance;
    }
    class MyInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("增加的业务逻辑，记录日志");
            if (method.getName().contains("add")) {
                System.out.println("增加的业务逻辑，记录日志，添加事务控制");
            }
            Object invoke = method.invoke(realObj, args);
            return invoke;
        }
    }
}