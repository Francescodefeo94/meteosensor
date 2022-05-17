package com.meteo.cloud.meteosensor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MeteosensorApplication {

    public static void main(String[] args) {

        SpringApplication.run(MeteosensorApplication.class, args);
    }

}
