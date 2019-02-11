package org.ilaborie
package org.rbitard

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    // TODO Javalin Hello World
    // Java syntax vs Kotlin comparison (DSL, it)
    Javalin.create()
            .get("/hello") {
                it.json("Hello Toulouse")
            }
            // TODO Configuration
            .disableStartupBanner()
            .enableDebugLogging()
            // TODO routing
            // @see https://javalin.io/documentation
            // routes, path, get, post in DSL
            .routes {
                path("api/emoji") {
                    get("/") {
                        it.json(EmojiRepository.findAll())
                    }
                    get(":name") {
                        val pathParam = it.pathParam("name")
                        it.json(EmojiRepository.findByName(pathParam))
                    }
                    post("/") {
                        val emoji = it.body<Emoji>()
                        it.json(EmojiRepository.insert(emoji))
                    }
                }
            }
            // TODO handle errors
            // ? and ?: null safety, modify emoji implementation to generate NPE
            .exception(NoSuchElementException::class.java) {
                noSuchElementException, context ->
                context.status(404).result(noSuchElementException.message ?: "No such element")
            }
            // TODO Static & WebSocket
            // Executors - Schedulers example, extension property, extension function
            .enableStaticFiles("public")
            .ws("/ws/clock") {
                wsHandler ->
                val executor = Executors.newSingleThreadScheduledExecutor()
                wsHandler.onConnect {
                    executor.setInterval(1.seconds) {
                        it.send(Instant.now().toString())
                    }
                }
            }
            .start(8080)
}


val Int.seconds
    get() = Duration.ofSeconds(this.toLong())

fun ScheduledExecutorService.setInterval(duration: Duration, delay: Duration = Duration.ZERO, function: () -> Unit) =
        this.scheduleAtFixedRate(function, delay.toMillis(), duration.toMillis(), TimeUnit.MILLISECONDS)


