package com.iba;

import com.iba.config.CoreUtilCfg;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Application {

    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(CoreUtilCfg.class);

    }
}
