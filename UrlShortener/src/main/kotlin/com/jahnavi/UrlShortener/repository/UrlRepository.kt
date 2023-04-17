package com.jahnavi.UrlShortener.repository

import com.jahnavi.UrlShortener.bean.Url
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UrlRepository : ReactiveMongoRepository<Url,String>{
    fun findByUserIdAndLongUrl(userId: String?, longUrl: String?): Mono<Url>
    fun findByShortUrl(shortUrl: String): Mono<Url>
}