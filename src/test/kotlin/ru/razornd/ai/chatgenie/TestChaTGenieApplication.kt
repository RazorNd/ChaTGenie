package ru.razornd.ai.chatgenie

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<ChaTGenieApplication>().with(TestcontainersConfiguration::class).run(*args)
}
