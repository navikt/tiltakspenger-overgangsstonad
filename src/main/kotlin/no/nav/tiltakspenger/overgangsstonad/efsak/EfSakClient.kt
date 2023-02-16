package no.nav.tiltakspenger.overgangsstonad.efsak

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import no.nav.tiltakspenger.overgangsstonad.Configuration
import no.nav.tiltakspenger.overgangsstonad.defaultHttpClient
import no.nav.tiltakspenger.overgangsstonad.defaultObjectMapper
import java.time.LocalDate

internal class OvergangsstønadRequestBody(
    val personIdent: String,
    val fom: LocalDate,
    val tom: LocalDate,
)

class EfSakClient(
    private val config: EFClientConfig = Configuration.efClientConfig(),
    private val objectMapper: ObjectMapper = defaultObjectMapper(),
    private val getToken: suspend () -> String,
    engine: HttpClientEngine? = null,
    private val httpClient: HttpClient = defaultHttpClient(
        objectMapper = objectMapper,
        engine = engine,
    ),
) {
    companion object {
        const val navCallIdHeader = "Nav-Call-Id"
    }

    suspend fun hentOvergangsstønad(
        ident: String,
        fom: LocalDate,
        tom: LocalDate,
        behovId: String,
    ): OvergangsstønadResponse {
        val httpResponse =
            httpClient.preparePost("${config.baseUrl}/api/ekstern/perioder") {
                header(navCallIdHeader, behovId)
                bearerAuth(getToken())
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                setBody(
                    OvergangsstønadRequestBody(
                        personIdent = ident,
                        fom = fom,
                        tom = tom,
                    ),
                )
            }.execute()
        return when (httpResponse.status) {
            HttpStatusCode.OK -> httpResponse.call.response.body()
            HttpStatusCode.NotFound -> OvergangsstønadResponse(
                data = OvergangsstønadResponseData(
                    perioder = emptyList(),
                ),
                status = "",
                melding = "",
                frontendFeilmelding = null,
                stacktrace = null,
            )
            else -> throw RuntimeException("error (responseCode=${httpResponse.status.value}) from Ef")
        }
    }

    data class EFClientConfig(
        val baseUrl: String,
    )
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
    var frontendFeilmelding: String?,
    val stacktrace: String?,
)
