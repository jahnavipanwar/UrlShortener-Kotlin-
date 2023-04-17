package com.jahnavi.UrlShortener.service

import com.google.common.hash.Hashing
import com.jahnavi.UrlShortener.bean.Url
import com.jahnavi.UrlShortener.bean.UrlDto
import com.jahnavi.UrlShortener.bean.UrlReport
import com.jahnavi.UrlShortener.exception.*
import com.jahnavi.UrlShortener.repository.UrlReportRepository
import com.jahnavi.UrlShortener.repository.UrlRepository
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class UrlService {
    @Autowired
    lateinit var repo:UrlRepository

    @Autowired
    lateinit var reportrepo:UrlReportRepository

    val domain:String="http://localhost:8080/miniurl.com/"

    fun generate(urlDto:UrlDto):Mono<String>{
        return Mono.just(urlDto)
            .flatMap { url->checkLongUrl(url) }

            .flatMap { urldto-> repo.findByUserIdAndLongUrl(urldto.userId,urldto.longUrl)
                .switchIfEmpty{createUrl(urldto)}}

            .flatMap { url->reportrepo.findByShortUrl(url.shortUrl)
                .switchIfEmpty{createReport(url)}
                .then(Mono.just("UserId: "+url.userId+"\nShortUrl: "+domain+url.shortUrl))}
    }

    fun checkLongUrl(urlDto:UrlDto):Mono<UrlDto>{
        return Mono.just(urlDto)
            .filter { urldto-> !(urldto.userId.isNullOrBlank())  }
            .switchIfEmpty { Mono.error(InvalidUserException("Please provide valid user")) }

            .filter { urldto ->UrlValidator.getInstance().isValid(urldto.longUrl) && !urldto.longUrl!!.contains("miniurl") }
            .switchIfEmpty{Mono.error(ArgumentNotValidException("Please Provide Valid Long Url"))}

            .filter{urldto->urldto.longUrl!!.length>50}
            .switchIfEmpty{Mono.error(UrlLengthException("Enter url of length atleast 50 characters"))}
    }

    fun createUrl(urldto:UrlDto):Mono<Url>{
        return repo.save(Url(null,urldto.userId,urldto.longUrl,encodeUrl(urldto.longUrl), LocalDateTime.now(), LocalDateTime.now().plusDays(1)))
    }

    fun encodeUrl(longurl:String?):String{
        var encodeUrl:String=""
        var time:LocalDateTime= LocalDateTime.now()
        encodeUrl=Hashing.murmur3_32_fixed().hashString(longurl.plus(time.toString()),StandardCharsets.UTF_8).toString()
        return encodeUrl
    }

    fun createReport(url: Url): Mono<UrlReport> {
        return reportrepo.save(UrlReport(null,url.shortUrl, LocalDate.now(),url.creationDate.toLocalDate(),0))
    }

    fun geturl(url:String):Mono<Url>{
        return repo.findByShortUrl(url)
    }

    fun deleteurl(url:String):Mono<Void>{
        return repo.findByShortUrl(url)
            .flatMap { url->repo.delete(url) }
    }

    fun updateHits(urlreport:UrlReport):Mono<UrlReport>{
        urlreport.hits=urlreport.hits+1
        return reportrepo.save(urlreport)
    }

    fun createNewUrlReport(url:Url):Mono<UrlReport>{
        return Mono.just(UrlReport(null,url.shortUrl, LocalDate.now(),url.creationDate.toLocalDate(),1))
    }

    fun redirect(shorturl:String):Mono<String>{
        return Mono.just(shorturl)
            .filter{url->url.length==8}
            .switchIfEmpty{Mono.error(UrlLengthException("Please Provide Correct Short Url"))}

            .flatMap { url->geturl(url)
                .switchIfEmpty{Mono.error(UrlNotFoundException("short url does not exists"))}}

            .filter{url->url.expiryDate.isAfter(LocalDateTime.now())}
            .switchIfEmpty{deleteurl(shorturl).then(Mono.error(UrlTimeoutException("Url expired!")))}

            .flatMap { url->reportrepo.findByShortUrlAndFetchDate(shorturl, LocalDate.now())
                .flatMap { url->updateHits(url) }
                .switchIfEmpty{createNewUrlReport(url)}
                .then(Mono.just(url.longUrl!!))}
    }

    fun getcreated(date:LocalDate) :Flux<UrlReport>{
             return reportrepo.findByCreateDateAndFetchDate(date,date)

    }


    fun urlbyhit(date:LocalDate):Flux<UrlReport>{
        return reportrepo.findByFetchDate(date)
            .filter{url->url.hits>0}
    }

    fun getallreport():Flux<UrlReport>{
        return reportrepo.findAll()
    }
}