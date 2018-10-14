package org.ilaborie

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import io.javalin.apibuilder.ApiBuilder.post
import io.javalin.json.JavalinJackson
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

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
        .exception(NoSuchElementException::class.java) { e, ctx ->
            ctx.status(404).result(e.message ?: "Oops!")
        }
        // Static & WebSocket
        .enableStaticFiles("public")
        .ws("/ws/clock") { wsHandler ->
            val ec = Executors.newSingleThreadScheduledExecutor()
            wsHandler.onConnect { session ->
                ec.setInterval(1.second) {
                    session.send(Instant.now().toString())
                }
            }
        }
        .start(8080)
}


// Kotlin Magic
val Int.second
    get() = Duration.ofSeconds(this.toLong())

fun ScheduledExecutorService.setInterval(duration: Duration, delay: Duration = Duration.ZERO, function: () -> Unit) =
    this.scheduleAtFixedRate(function, delay.toMillis(), duration.toMillis(), TimeUnit.MILLISECONDS)

