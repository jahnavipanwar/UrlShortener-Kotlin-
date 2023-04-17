package com.jahnavi.UrlShortener

import com.jahnavi.UrlShortener.bean.Url
import com.jahnavi.UrlShortener.bean.UrlDto
import com.jahnavi.UrlShortener.bean.UrlReport
import com.jahnavi.UrlShortener.exception.ArgumentNotValidException
import com.jahnavi.UrlShortener.exception.UrlLengthException
import com.jahnavi.UrlShortener.repository.UrlReportRepository
import com.jahnavi.UrlShortener.repository.UrlRepository
import com.jahnavi.UrlShortener.service.UrlService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class PostTest : UrlShortenerApplicationTests() {
    @Mock
    lateinit var repo:UrlRepository

    @Mock
    lateinit var reportrepo:UrlReportRepository

    @InjectMocks
    lateinit var service:UrlService

    private lateinit var mockUserId:String
    @BeforeEach
    fun init(){
        mockUserId="jahnavi"
    }

    @Test
    fun blankTest(){
        var mockLongUrl:String=""
        var urlDto:UrlDto=UrlDto(mockLongUrl,mockUserId)

        var response:Mono<String> = service.generate(urlDto)

        StepVerifier.create(response)
            .verifyError(ArgumentNotValidException::class.java)
    }

    @Test
    fun containsMiniurlTest(){
        var mockLongUrl:String="miniurl.com"
        var urlDto:UrlDto=UrlDto(mockLongUrl,mockUserId)

        var response:Mono<String> = service.generate(urlDto)
        StepVerifier.create(response)
            .verifyError(ArgumentNotValidException::class.java)
    }

    @Test
    fun urlLengthTest(){
        var mockLongUrl:String="google.com"
        var urlDto:UrlDto=UrlDto(mockLongUrl,mockUserId)

        var response:Mono<String> = service.generate(urlDto)
        print(mockUserId)
        StepVerifier.create(response)
            .verifyError(UrlLengthException::class.java)
    }

    @Test
    fun notindbTest(){
        var mockLongUrl:String="https://medium.com/@sandeep4.verma/system-design-scalable-url-shortener-service-like-tinyurl-106f30f23a82"
        var urlDto:UrlDto= UrlDto(mockLongUrl,mockUserId)
        var mockShortUrl="28h90f79"
        var mockUrl:Url= Url("1",mockUserId,mockLongUrl,mockShortUrl, LocalDateTime.now(), LocalDateTime.now().plusDays(1))
        var urlReport:UrlReport=UrlReport("1",mockShortUrl, LocalDate.now(), LocalDate.now(),0)

        Mockito.`when`(repo.findByUserIdAndLongUrl(mockUserId,mockLongUrl)).thenReturn(Mono.empty())
        Mockito.`when`(repo.save(Mockito.any(Url::class.java))).thenReturn(Mono.just(mockUrl))
        Mockito.`when`(reportrepo.findByShortUrl(mockShortUrl)).thenReturn(Mono.empty())
        Mockito.`when`(reportrepo.save(Mockito.any(UrlReport::class.java))).thenReturn(Mono.just(urlReport))

        var response : Mono<String> = service.generate(urlDto)
        StepVerifier.create(response)
            .expectNext("""
                UserId: jahnavi
                ShortUrl: http://localhost:8080/miniurl.com/28h90f79
            """.trimIndent())
            .verifyComplete()
    }

    @Test
    fun indbTest(){
        var mockLongUrl:String="https://medium.com/@sandeep4.verma/system-design-scalable-url-shortener-service-like-tinyurl-106f30f23a82"
        var urlDto:UrlDto= UrlDto(mockLongUrl,mockUserId)
        var mockShortUrl="28h90f79"
        var mockUrl:Url= Url("1",mockUserId,mockLongUrl,mockShortUrl, LocalDateTime.now(), LocalDateTime.now().plusDays(1))
        var urlReport:UrlReport=UrlReport("1",mockShortUrl, LocalDate.now(), LocalDate.now(),0)

        Mockito.`when`(repo.findByUserIdAndLongUrl(mockUserId,mockLongUrl)).thenReturn(Mono.just(mockUrl))
        Mockito.`when`(reportrepo.findByShortUrl(mockShortUrl)).thenReturn(Mono.just(urlReport))

        var response : Mono<String> = service.generate(urlDto)
        StepVerifier.create(response)
            .expectNext("""
                UserId: jahnavi
                ShortUrl: http://localhost:8080/miniurl.com/28h90f79
            """.trimIndent())
            .verifyComplete()
    }
}