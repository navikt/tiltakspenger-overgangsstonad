package no.nav.tiltakspenger.overgangsstonad.efsak

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import no.nav.tiltakspenger.overgangsstonad.httpClientGeneric
import org.junit.jupiter.api.Test

internal class EfSakClientTest {
    @Test
    fun `EF Sak svarer OK og personen har overgangsstønad`() {
        val mockEngine = MockEngine {
            respond(
                content = """{"data":{"perioder":[{"personIdent":"123","fomDato":"2025-01-01","tomDato":"2025-01-10","datakilde":"kilde"}],"status":"test","melding":"test","frontendFeilmelding":"test","stacktrace":"test"}}""".trimMargin(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = httpClientGeneric(mockEngine)
        val efSakClient = EfSakClient(client) { "token for testing" }
        runTest {
            val response = efSakClient.hentOvergangsstønad("ident", "fom", "tom", "behovId")
            assert(response.data.perioder.size == 1)
            assert(response.data.perioder.get(0).fomDato == "2025-01-01")
            assert(response.data.perioder.get(0).tomDato == "2025-01-10")
        }
    }

    @Test
    fun `EF Sak svarer 200 og personen har ikke overgangsstønad`() {
        val mockEngine = MockEngine {
            respond(
                content = ""
            )
        }
    }

    @Test
    fun `EF Sak svarer 400 Bad Request og det kastes exception`() {

    }
}