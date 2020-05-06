package com.xsxy.fluxController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestFluxController {

    @GetMapping("testFlux")
    public Mono<String> testFlux() {
        return Mono.just("hello webFlux");
    }
}
