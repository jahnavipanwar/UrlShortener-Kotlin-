package com.jahnavi.UrlShortener.bean

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "UrlReport")
data class UrlReport(@Id var id:String?,var shortUrl:String,var fetchDate:LocalDate?,var createDate:LocalDate,var hits:Long) {
}