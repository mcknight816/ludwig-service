package com.bluntsoftware.ludwig.model;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Scope;

@Scope("test")
class FlowTest {

  @Test
  void shouldCreateFlow(){
    EasyRandom generator = new EasyRandom();
    Assertions.assertNotNull(generator.nextObject(Flow.class));
  }

  @Test
  void shouldBuildFlow(){
    Assertions.assertNotNull(Flow.builder().build());
  }
}
