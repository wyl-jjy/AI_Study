package com.example.springai_01;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.springai_01.mapper")
public class SpringAi01Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringAi01Application.class, args);
    }

}
