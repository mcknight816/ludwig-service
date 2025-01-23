package com.bluntsoftware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LudwigApplication {
  public static void main(String[] args) {
    SpringApplication.run(LudwigApplication.class, args);
  }
}
