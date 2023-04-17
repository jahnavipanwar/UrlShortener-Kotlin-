package com.jahnavi.UrlShortener.controller

import com.jahnavi.UrlShortener.bean.UrlDto
import com.jahnavi.UrlShortener.bean.UrlReport
import com.jahnavi.UrlShortener.service.UrlService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayInputStream
import java.net.URI
import java.time.LocalDate


@RestController
class UrlController {
    @Autowired
    lateinit var service: UrlService


    @PostMapping("/create")
    public fun generate(@RequestBody urldto: UrlDto): Mono<String> {
        return service.generate(urldto)
    }

    @GetMapping("/miniurl.com/{shorturl}")
    public fun redirect(@PathVariable shorturl: String, response: ServerHttpResponse): Mono<Void> {
        return service.redirect(shorturl)
            .flatMap { longurl->
                response.setStatusCode(HttpStatus.TEMPORARY_REDIRECT)
                response.getHeaders().setLocation(URI.create(longurl))
                return@flatMap response.setComplete();
                          }
    }

    @GetMapping("/urlByCreateDate")
    public fun getcreated(@RequestParam (value="date",required=false)date:LocalDate?):Flux<UrlReport>{
        if(date==null)
            return service.getallreport()
        return service.getcreated(date)
    }

    @GetMapping("/urlByHit")
    public fun urlbyhit(@RequestParam (value="date",required=false)date:LocalDate?):Flux<UrlReport>{
        if(date==null)
            return service.getallreport()
        return service.urlbyhit(date)
    }

    @GetMapping("/getreport")
    public fun getallreport():Flux<UrlReport>{
        return service.getallreport()
    }
}