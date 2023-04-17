package com.jahnavi.UrlShortener

import com.jahnavi.UrlShortener.bean.Url
import com.jahnavi.UrlShortener.bean.UrlReport
import com.jahnavi.UrlShortener.controller.UrlController
import com.jahnavi.UrlShortener.exception.UrlLengthException
import com.jahnavi.UrlShortener.exception.UrlNotFoundException
import com.jahnavi.UrlShortener.exception.UrlTimeoutException
import com.jahnavi.UrlShortener.repository.UrlReportRepository
import com.jahnavi.UrlShortener.repository.UrlRepository
import com.jahnavi.UrlShortener.service.UrlService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class GetTest : UrlShortenerApplicationTests() {
    @Mock
    lateinit var repo: UrlRepository

    @Mock
    lateinit var reportrepo:UrlReportRepository

    @InjectMocks
    lateinit var service:UrlService

    @Test
    fun lengthTest(){
        var mockShortUrl:String="82g"

        var response:Mono<String> = service.redirect(mockShortUrl)


        StepVerifier.create(response)
            .verifyError(UrlLengthException::class.java)
    }

    @Test
    fun notfoundTest(){
        var mockShortUrl:String="82g78h98"

        Mockito.`when`(repo.findByShortUrl(mockShortUrl)).thenReturn(Mono.empty())

        var response:Mono<String> = service.redirect(mockShortUrl)

        StepVerifier.create(response)
            .verifyError(UrlNotFoundException::class.java)
    }

    @Test
    fun urlExpiredTest(){
        var mockShortUrl:String="82g78h98"
        var mockLongUrl:String="https://medium.com/@sandeep4.verma/system-design-scalable-url-shortener-service-like-tinyurl-106f30f23a82"
        var mockUserId:String="jahnavi"
        var mockUrl: Url = Url("1",mockUserId,mockLongUrl,mockShortUrl, LocalDateTime.now(), LocalDateTime.now().minusDays(1))

        Mockito.`when`(repo.findByShortUrl(mockShortUrl)).thenReturn(Mono.just(mockUrl))
        Mockito.`when`(repo.delete(Mockito.any(Url::class.java))).thenReturn(Mono.empty())

        var response:Mono<String> =service.redirect(mockShortUrl)

        StepVerifier.create(response)
            .verifyError(UrlTimeoutException::class.java)
    }

    @Test
    fun urlNotExpiredTest(){
        var mockShortUrl:String="82g78h98"
        var mockLongUrl:String="https://medium.com/@sandeep4.verma/system-design-scalable-url-shortener-service-like-tinyurl-106f30f23a82"
        var mockUserId:String="jahnavi"
        var mockUrl: Url = Url("1",mockUserId,mockLongUrl,mockShortUrl, LocalDateTime.now(), LocalDateTime.now().plusDays(1))
        var urlReport: UrlReport = UrlReport("1",mockShortUrl, LocalDate.now(), LocalDate.now(),0)

        Mockito.`when`(repo.findByShortUrl(mockShortUrl)).thenReturn(Mono.just(mockUrl))
        Mockito.`when`(reportrepo.findByShortUrlAndDate(mockShortUrl, LocalDate.now())).thenReturn(Mono.just(urlReport))
        Mockito.`when`(reportrepo.save(Mockito.any(UrlReport::class.java))).thenReturn(Mono.just(urlReport))

        var response:Mono<String> =service.redirect(mockShortUrl)

        StepVerifier.create(response)
            .expectNext(mockLongUrl)
            .verifyComplete()
    }

    @Test
    fun getCreatedTest(){
        var date:LocalDate= LocalDate.now()
        var mockShortUrl:String="82g78h98"
        var urlReport: UrlReport = UrlReport("1",mockShortUrl, LocalDate.now(), LocalDate.now(),0)

        Mockito.`when`(reportrepo.findByCreateDateAndDate(date,date)).thenReturn(Flux.just(urlReport))

        var response:Flux<UrlReport> =service.getcreated(date)

        StepVerifier.create(response)
            .expectNext(urlReport)
            .verifyComplete()
    }

    @Test
    fun hitTest(){
        var date:LocalDate= LocalDate.now()
        var mockShortUrl:String="82g78h98"
        var urlReport: UrlReport = UrlReport("1",mockShortUrl, LocalDate.now(), LocalDate.now(),1)

        Mockito.`when`(reportrepo.findByDate(date)).thenReturn(Flux.just(urlReport))

        var response:Flux<UrlReport> =service.urlbyhit(date)

        StepVerifier.create(response)
            .expectNext(urlReport)
            .verifyComplete()
    }

    @Test
    fun reportTest(){
        var date:LocalDate= LocalDate.now()
        var mockShortUrl:String="82g78h98"
        var urlReport: UrlReport = UrlReport("1",mockShortUrl, LocalDate.now(), LocalDate.now(),1)

        Mockito.`when`(reportrepo.findAll()).thenReturn(Flux.just(urlReport))

        var response:Flux<UrlReport> =service.getallreport()

        StepVerifier.create(response)
            .expectNext(urlReport)
            .verifyComplete()
    }
}