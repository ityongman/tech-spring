package com.ityongman.threadlocal;

public class ThreadLocalSingleton {
    //1.
    private ThreadLocalSingleton(){}

    //2.
    private static final ThreadLocal<ThreadLocalSingleton> threadLocal = new ThreadLocal<ThreadLocalSingleton>(){
        @Override
        protected ThreadLocalSingleton initialValue() {
            return new ThreadLocalSingleton();
        }
    };

    //3.
    public static ThreadLocalSingleton getInstance() {
        return threadLocal.get() ;
    }

}
