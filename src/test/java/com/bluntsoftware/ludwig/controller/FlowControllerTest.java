package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.model.Flow;
import com.bluntsoftware.ludwig.service.FlowService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import java.util.List;
import org.skyscreamer.jsonassert.JSONAssert;

@WebFluxTest(controllers = FlowController.class)
@Import(FlowService.class)
@ExtendWith(SpringExtension.class)
@Scope("test")
class FlowControllerTest {

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  FlowService service;

  @Autowired
  private FlowController controller;

  Mono<Flow> mono;
  Flux<Flow> flux;

  @BeforeEach
  void before() {
  EasyRandomParameters parameters = new EasyRandomParameters();
  parameters.setCollectionSizeRange(new EasyRandomParameters.Range<>(2,10));
  EasyRandom generator = new EasyRandom(parameters);

  mono = Mono.just(generator.nextObject(Flow.class));
    flux = Flux.just(generator.nextObject(Flow.class), generator.nextObject(Flow.class));
    Mockito.when(this.service.findAll()).thenReturn(flux);
    Mockito.when(this.service.save(any())).thenReturn(mono);
    Mockito.when(this.service.findById(any())).thenReturn(mono);
  }

  @Test
  void shouldFindById() throws Exception {
    String jsonBlob = objectMapper.writeValueAsString(mono.block());
    byte[] data = WebTestClient.bindToController(controller).build()
      .get().uri("/rest/flow/1")
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody().returnResult().getResponseBody();
       JSONAssert.assertEquals(jsonBlob, objectMapper.writeValueAsString(objectMapper.readValue(data,Flow.class)),false);
  }

  @Test
  void shouldFindAll() throws Exception {
    String jsonBlob = objectMapper.writeValueAsString(flux.collectList().block());
    byte[] data = WebTestClient.bindToController(controller).build()
      .get().uri("/rest/flow")
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody().returnResult().getResponseBody();

    assert (flux.collectList().block().size() == objectMapper.readValue(data, List.class).size());
  }

  @Test
  void shouldDeleteById() {
    WebTestClient
      .bindToController(controller)
      .build()
      .delete().uri("/rest/flow/1")
      .exchange()
      .expectStatus().isOk();
  }

  @Test
  void shouldSave() throws Exception {
    String jsonBlob = objectMapper.writeValueAsString(mono.block());
     byte[] data = WebTestClient.bindToController(controller).build()
      .post().uri("/rest/flow")
      .body(mono,Flow.class)
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentType(MediaType.APPLICATION_JSON)
      .expectBody().returnResult().getResponseBody();

     JSONAssert.assertEquals(jsonBlob, objectMapper.writeValueAsString(objectMapper.readValue(data,Flow.class)),false);
  }
}
