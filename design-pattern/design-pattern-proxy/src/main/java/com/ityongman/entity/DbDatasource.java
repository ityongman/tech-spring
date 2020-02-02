package com.ityongman.entity;

public class DbDatasource {
    public static final String DEFAULT_DB = null ;

    //ThreadLocal 单例模式
    //1.
    private DbDatasource (){};

    //2.
    private final static ThreadLocal<String> dbs = new ThreadLocal<>();

    //3.
    public static String getDB() {
        return dbs.get();
    }
    //
    public static void restore(){
        dbs.set(DEFAULT_DB);
    }

    public static void setDB(String db){
        dbs.set(db);
    }

}
