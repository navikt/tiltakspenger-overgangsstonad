package no.nav.tiltakspenger.overgangsstonad.efsak

internal class EfSakClientTest {
//    @Language("JSON")
//    val personHarOvergangsstønad = """{
//        "data": {
//          "perioder": [
//            {"personIdent":"123","fomDato":"2025-01-01","tomDato":"2025-01-10","datakilde":"kilde"}
//          ],
//          "status":"test",
//          "melding":"test",
//          "frontendFeilmelding":"test",
//          "stacktrace":"test"
//        }
//    }
//    """.trimMargin()
//
//    @Language("JSON")
//    val personHarIkkeOvergangsstønad = """{
//        "data": {
//          "perioder": [],
//          "status":"test",
//          "melding":"test",
//          "frontendFeilmelding":"test",
//          "stacktrace":"test"
//        }
//      }
//    """.trimMargin()
//
//    @Test
//    fun `EF Sak svarer OK og personen har overgangsstønad`() {
//        val mockEngine = MockEngine {
//            respond(
//                content = personHarOvergangsstønad,
//                status = HttpStatusCode.OK,
//                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
//            )
//        }
//        val client = httpClientGeneric(mockEngine)
//        val efSakClient = EfSakClient(client) { "token for testing" }
//        runTest {
//            val response = efSakClient.hentOvergangsstønad("ident", "fom", "tom", "behovId")
//            assert(response.data.perioder.size == 1)
//            assert(response.data.perioder.get(0).fomDato == "2025-01-01")
//            assert(response.data.perioder.get(0).tomDato == "2025-01-10")
//        }
//    }
//
//    @Test
//    fun `EF Sak svarer 200 og personen har ikke overgangsstønad`() {
//        val mockEngine = MockEngine {
//            respond(
//                content = personHarIkkeOvergangsstønad,
//                status = HttpStatusCode.OK,
//                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
//            )
//        }
//        val client = httpClientGeneric(mockEngine)
//        val efSakClient = EfSakClient(client) { "token for testing" }
//        runTest {
//            val response = efSakClient.hentOvergangsstønad("ident", "fom", "tom", "behovId")
//            assert(response.data.perioder.size == 0)
//        }
//    }
//
//    @Test
//    fun `EF Sak svarer 400 Bad Request og det kastes exception`() {
//        val mockEngine = MockEngine {
//            respond(
//                content = """400 Bad Request""".trimMargin(),
//                status = HttpStatusCode.BadRequest,
//                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
//            )
//        }
//        assertThrows(ClientRequestException::class.java) {
//            runTest { efSakClient.hentOvergangsstønad("ident", "fom", "tom", "behovId") }
//        }
//    }
}
