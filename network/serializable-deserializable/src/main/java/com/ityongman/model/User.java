package com.ityongman.model;

import java.io.*;

/**
 * @Author shedunze
 * @Date 2020-01-30 11:23
 * @Description
 */
public class User implements Serializable /** 解决网络传输序列化错误 java.io.NotSerializableException*/{

    private static final long serialVersionUID = 1L ;

//    transient private String userName ;
    private String userName ;

    private Integer age ;

    /***
     * writeObject and readObject 访问修饰标识必须为 private ,
     * 不然不能正确的解决 transient修饰字段的序列化问题
     * @param s
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        s.writeObject(userName);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        userName = (String) s.readObject();
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                '}';
    }
}
