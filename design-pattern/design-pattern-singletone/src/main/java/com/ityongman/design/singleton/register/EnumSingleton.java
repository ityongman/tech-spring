package com.ityongman.design.singleton.register;

/**
 * 枚举式单例
 */
public enum  EnumSingleton {
    INSTANCE ;

    public static EnumSingleton getInstance(){
        return INSTANCE ;
    }


    private Object data ;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
