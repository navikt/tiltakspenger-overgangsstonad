package no.nav.tiltakspenger.overgangsstonad.efsak

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import mu.KotlinLogging

internal class OvergangsstønadRequestBody(val personIdent: String)

class EfSakClient(private val client: HttpClient, private val getToken: suspend () -> String) {
    private val config = no.nav.tiltakspenger.overgangsstonad.Configuration.EfsakConfig()
    private val secureLog = KotlinLogging.logger("tjenestekall")
    private val token = getToken

    suspend fun hentOvergangsstønad(
        ident: String,
        fom: String,
        tom: String,
        behovId: String,
    ): OvergangsstønadResponse =
        try {
            secureLog.info { "Kaller ef : ${config.efsakUrl} $ident $fom $tom $token" }
            val response = client.post(urlString = config.efsakUrl) {
                bearerAuth(getToken())
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                header("Nav-Call-Id", behovId)
                setBody(
                    OvergangsstønadRequestBody(
                        personIdent = ident,
                    ),
                )
            }
            response.body<OvergangsstønadResponse>().also {
                secureLog.info { "Resonse fra ef : $it" }
            }
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) {
                OvergangsstønadResponse(
                    data = OvergangsstønadResponseData(
                        perioder = emptyList(),
                    ),
                    status = "",
                    melding = "",
                    frontendFeilmelding = "",
                    stacktrace = "",
                )
            } else {
                throw (e)
            }
        }
}

data class OvergangsstønadPeriode(
    val personIdent: String,
    val fomDato: String,
    val tomDato: String,
    val datakilde: String,
)

data class OvergangsstønadResponseData(
    val perioder: List<OvergangsstønadPeriode>,

)

data class OvergangsstønadResponse(
    val data: OvergangsstønadResponseData,
    val status: String,
    val melding: String,
    var frontendFeilmelding: String,
    val stacktrace: String,
)
