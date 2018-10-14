package org.ilaborie

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Context
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.json.JavalinJackson

fun main(args: Array<String>) {
    println("Hello, Toulouse, Jug")

    // TODO Javalin Hello World
    // @see https://javalin.io/documentation

    JavalinJackson.configure(jacksonObjectMapper())

    Javalin.create()
        // configuration
        .disableStartupBanner()
        .enableCorsForAllOrigins() // 😱 FIXME
        .enableDebugLogging()
        // routing
        .routes {
            path("/api/emoji") {
                get("/") { it.json(EmojiRepository.findAll()) }
                get(":name") { ctx ->
                    val name = ctx.pathParam("name")
                    val result = EmojiRepository.findByName(name)
                    ctx.json(result)
                }
                post("/") { ctx ->
                    val emoji = ctx.body<Emoji>()
                    ctx.json(EmojiRepository.insert(emoji))
                }
            }
        }
        // handle errors
        .exception { e: NoSuchElementException, ctx ->
            ctx.status(404).result(e.message ?: "Oops!")
        }
        .start(8080)
}


// Kotlin Magic
inline fun <reified T : Exception> Javalin.exception(noinline block: (T, Context) -> Unit): Javalin =
    this.exception(T::class.java, block)
