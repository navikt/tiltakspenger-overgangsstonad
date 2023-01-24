package no.nav.tiltakspenger.overgangsstonad

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import mu.withLoggingContext
import net.logstash.logback.argument.StructuredArguments
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.tiltakspenger.overgangsstonad.efsak.EfSakClient

class OvergangsstønadService(rapidsConnection: RapidsConnection, private val efSakClient: EfSakClient) : River.PacketListener {
    private val log = KotlinLogging.logger {}
    private val secureLog = KotlinLogging.logger("tjenestekall")

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAllOrAny("@behov", listOf("overgangsstønad"))
                it.forbid("@løsning")
                it.requireKey("@id", "@behovId")
                it.requireKey("ident")
                it.requireKey("fom")
                it.requireKey("tom")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        runCatching {
            loggVedInngang(packet)
            withLoggingContext(
                "id" to packet["@id"].asText(),
                "behovId" to packet["@behovId"].asText()
            ) {
                val behovId = packet["@behovId"].asText()
                val ident = packet["ident"].asText()
                secureLog.debug { "mottok ident $ident" }
                val fom = packet["fom"].asText()
                val tom = packet["tom"].asText()
                val response = runBlocking(MDCContext()) {
                    efSakClient.hentOvergangsstønad(
                        ident = ident,
                        fom = fom,
                        tom = tom,
                        behovId = behovId
                    )
                }
                log.info { "Fikk svar fra Efsak. Sjekk securelog for detaljer" }
                secureLog.info { response }
                packet["@løsning"] = mapOf(
                    "perioder" to response.data.perioder,
                    "ident" to ident,
                )
                loggVedUtgang(packet)
                context.publish(ident, packet.toJson())
            }
        }.onFailure {
            loggVedFeil(it, packet)
        }.getOrThrow()
    }

    private fun loggVedInngang(packet: JsonMessage) {
        log.info(
            "løser overgangsstønad-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        secureLog.info(
            "løser overgangsstønad-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        secureLog.debug { "mottok melding: ${packet.toJson()}" }
    }

    private fun loggVedUtgang(packet: JsonMessage) {
        log.info(
            "har løst overgangsstønad-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        secureLog.info(
            "har løst overgangsstønad-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("behovId", packet["@behovId"].asText())
        )
        secureLog.debug { "publiserer melding: ${packet.toJson()}" }
    }

    private fun loggVedFeil(ex: Throwable, packet: JsonMessage) {
        log.error(
            "feil ved behandling av overgangsstønad-behov med {}, se securelogs for detaljer",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
        )
        secureLog.error(
            "feil \"${ex.message}\" ved behandling av overgangsstønad-behov med {} og {}",
            StructuredArguments.keyValue("id", packet["@id"].asText()),
            StructuredArguments.keyValue("packet", packet.toJson()),
            ex
        )
    }
}
