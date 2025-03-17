package com.iba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
//        var ctx = SpringApplication.run(Application.class);
//        Arrays.stream(ctx.getBeanDefinitionNames())
//                .sorted()
//                .forEach(System.out::println);
        SpringApplication.run(Application.class);
    }
}
