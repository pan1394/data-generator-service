package com.linkstec.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DataSetGenerator {

  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(DataSetGenerator.class, args);
  }
}