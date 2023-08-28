package com.bluntsoftware.ludwig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class  ApplicationTest {
  @Autowired
  private ApplicationContext applicationContext;

  @Test
  void contextLoadsAndAppStarts() {
    Application.main(new String[0]);
    Assertions.assertNotNull(applicationContext);
  }

}
