package com.jahnavi.UrlShortener.bean


data class ErrorDetails(var errorDetails:String,var message:String?) {
    constructor() : this("","")

}