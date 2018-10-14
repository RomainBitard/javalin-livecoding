package org.ilaborie

import io.javalin.Javalin

fun main(args: Array<String>) {
    println("Hello, Toulouse, Jug")

    // TODO Javalin Hello World
    // @see https://javalin.io/documentation

    Javalin.create()
        // configuration
        .disableStartupBanner()
        .enableCorsForAllOrigins() // 😱 FIXME
        .enableDebugLogging()
        .get("/") { it.result("Hello, Toulouse JUG")}
        .start(8080)
}
