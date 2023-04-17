package com.jahnavi.UrlShortener

import com.jahnavi.UrlShortener.bean.UrlDto
import com.jahnavi.UrlShortener.bean.UrlReport
import com.jahnavi.UrlShortener.controller.UrlController
import com.jahnavi.UrlShortener.service.UrlService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class ControllerTest : UrlShortenerApplicationTests(){
    @Mock
    lateinit var service: UrlService

    @InjectMocks
    lateinit var controller: UrlController
    lateinit var response:ServerHttpResponse

    @Test
    fun generate(){
        var urlDto:UrlDto=UrlDto("https://medium.com/@sandeep4.verma/system-design-scalable-url-shortener-service-like-tinyurl-106f30f23a82","jahnavi")
        Mockito.`when`(service.generate(urlDto)).thenReturn(Mono.just("""
                UserId: jahnavi
                ShortUrl: http://localhost:8080/miniurl.com/28h90f79
            """.trimIndent()))

        var result:Mono<String> = controller.generate(urlDto)

        StepVerifier.create(result)
            .expectNext("""
                UserId: jahnavi
                ShortUrl: http://localhost:8080/miniurl.com/28h90f79
            """.trimIndent())
            .verifyComplete()
    }

    @Test
    fun redirectTest() {
        val shorturl = "38g2h789"
        val longurl = "https://howtodoinjava.com/spring-boot2/testing/spring-boot-mockmvc-example/"
        Mockito.`when`(service.redirect(shorturl)).thenReturn(Mono.just(longurl))
        val result:Mono<Void> = controller.redirect(shorturl,response)
        StepVerifier.create(result)
            .expectComplete()
    }

    @Test
    fun reportByCreateDateTest(){
        var date:LocalDate= LocalDate.now()
        var urlReport: UrlReport = UrlReport("1","37h29fh7", LocalDate.now(), LocalDate.now(),1)

        Mockito.`when`(service.getcreated(date)).thenReturn(Flux.just(urlReport))
        var result:Flux<UrlReport> =controller.getcreated(date)
        StepVerifier.create(result)
            .expectNext(urlReport)
            .verifyComplete()
    }

    @Test
    fun reportByHitsTest(){
        var date:LocalDate= LocalDate.now()
        var urlReport: UrlReport = UrlReport("1","37h29fh7", LocalDate.now(), LocalDate.now(),1)

        Mockito.`when`(service.urlbyhit(date)).thenReturn(Flux.just(urlReport))
        var result:Flux<UrlReport> =controller.urlbyhit(date)
        StepVerifier.create(result)
            .expectNext(urlReport)
            .verifyComplete()
    }

    @Test
    fun reportTest(){
        var urlReport: UrlReport = UrlReport("1","37h29fh7", LocalDate.now(), LocalDate.now(),1)

        Mockito.`when`(service.getallreport()).thenReturn(Flux.just(urlReport))
        var result:Flux<UrlReport> =controller.getallreport()
        StepVerifier.create(result)
            .expectNext(urlReport)
            .verifyComplete()
    }
}