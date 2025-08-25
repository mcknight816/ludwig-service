package com.bluntsoftware.ludwig;

import com.bluntsoftware.LudwigApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class  ApplicationTest {

  private final ApplicationContext applicationContext;

  ApplicationTest(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

/*  @Test
  void contextLoadsAndAppStarts() {
      LudwigApplication.main(new String[0]);
      Assertions.assertNotNull(applicationContext);
  }*/

}
