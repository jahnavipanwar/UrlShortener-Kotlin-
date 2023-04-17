package com.jahnavi.UrlShortener.repository

import com.jahnavi.UrlShortener.bean.Url
import com.jahnavi.UrlShortener.bean.UrlReport
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Repository
interface UrlReportRepository:ReactiveMongoRepository<UrlReport,String> {
    fun findByShortUrl(shortUrl: String): Mono<UrlReport>
    fun findByShortUrlAndFetchDate(shortUrl: String,fetchDate:LocalDate): Mono<UrlReport>
    fun findByCreateDateAndFetchDate(createDate: LocalDate,fetchDate: LocalDate?):Flux<UrlReport>
    fun findByFetchDate(fetchDate: LocalDate?):Flux<UrlReport>
}