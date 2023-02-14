package no.nav.tiltakspenger.overgangsstonad

import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.tiltakspenger.overgangsstonad.auth.AzureTokenProvider
import no.nav.tiltakspenger.overgangsstonad.efsak.EfSakClient

fun main() {
    System.setProperty("logback.configurationFile", "egenLogback.xml")
    val log = KotlinLogging.logger {}
    val securelog = KotlinLogging.logger("tjenestekall")
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        log.error { "Uncaught exception logget i securelog" }
        securelog.error(e) { e.message }
    }
    val tokenProvider = AzureTokenProvider(httpClientMedProxy())
    RapidApplication.create(Configuration.rapidsAndRivers).apply {
        OvergangsstønadService(
            rapidsConnection = this,
            efSakClient = EfSakClient(httpClientCIO(), tokenProvider::getToken)
        )
        register(object : RapidsConnection.StatusListener {
            override fun onStartup(rapidsConnection: RapidsConnection) {
                log.info { "Starting tiltakspenger-overgangsstonad" }
            }

            override fun onShutdown(rapidsConnection: RapidsConnection) {
                log.info { "Stopping tiltakspenger-overgangsstonad" }
                super.onShutdown(rapidsConnection)
            }
        })
    }.start()
}
