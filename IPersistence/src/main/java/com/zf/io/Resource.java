package com.zf.io;

import java.io.InputStream;

/**
 * @author admin
 * @date 2020/2/22 19:07
 * @description
 */
public class Resource {

    private Resource(){

    }

    /**
     *  读取配置文件
     * @param path
     * @return
     */
    public static InputStream getResourceAsStream(String path){
        InputStream resourceAsStream = Resource.class.getClassLoader().getResourceAsStream(path);
        return  resourceAsStream;
    }

}
