package ru.itmo.shashulovskiy.mysterysanta

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MysterySantaApplication

fun main(args: Array<String>) {
    runApplication<MysterySantaApplication>(*args)
}
