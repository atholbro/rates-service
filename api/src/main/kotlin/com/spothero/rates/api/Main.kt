package com.spothero.rates.api

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.inputStream
import com.github.ajalt.clikt.parameters.types.int
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapBoth
import com.spothero.rates.api.util.parseRates
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import kotlin.system.exitProcess
import com.spothero.rates.api.RatesApplication.Companion.instance as app

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) = Main().main(args)

class Main : CliktCommand() {
    private val ratesFile by argument("rates.json")
        .help("Path to initial rates file in JSON format.")
        .inputStream()

    private val httpPort by option("-p", "--port")
        .int()
        .help("HTTP Server Port, defaults to 5000.")
        .default(5000)

    override fun run(): Unit = runBlocking {
        // Wait for DB to be ready:
        app.startupService.waitForDb()

        // Init Application with defaults (real RateService):
        // Parse rates file:
        ratesFile.use { stream ->
            parseRates(stream)
                // Set rates:
                .andThen { app.setInitialRates(it) }
                // Handle success/failure:
                .mapBoth(
                    success = {
                        logger.info { "Loaded $it rates!" }
                    },

                    failure = {
                        logger.error { "Failed to set initial rates: $it." }
                        exitProcess(1)
                    }
                )
        }

        // Start HTTP server
        embeddedServer(
            factory = Netty,
            module = Application::module,
            port = httpPort,
        ).start(wait = true)
    }
}
