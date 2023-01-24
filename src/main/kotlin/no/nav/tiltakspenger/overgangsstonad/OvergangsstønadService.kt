package no.nav.tiltakspenger.overgangsstonad

import mu.KotlinLogging
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
        // todo
    }

    private fun loggVedInngang(packet: JsonMessage) {
        // todo
    }

    private fun loggVedUtgang(packet: JsonMessage) {
        // todo
    }

    private fun loggVedFeil(ex: Throwable, packet: JsonMessage) {
        // todo
    }
}
