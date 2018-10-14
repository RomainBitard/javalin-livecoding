package org.ilaborie

import java.util.concurrent.atomic.AtomicReference


data class Emoji(val name: String, val value: String)


object EmojiRepository {

    private val all = AtomicReference<Set<Emoji>>(
        setOf(
            Emoji("cat", "😸"),
            Emoji("dog", "🐕"),
            Emoji("monkey", "🐒")
        )
    )

    fun findAll(): Set<Emoji> =
        all.get()

    fun findByName(name: String) =
        all.get()
            .find { it.name == name }
            ?: throw NoSuchElementException("No emoji for '$name'")

    fun insert(emoji: Emoji): Set<Emoji> =
        all.updateAndGet { it + emoji }
}