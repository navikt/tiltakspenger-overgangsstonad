package no.nav.tiltakspenger.overgangsstonad

internal class OvergangsstønadServiceTest {
//    private val ident = "123"

//    @Language("JSON")
//    private val behov = """
//            {
//              "@behov": [
//                "overgangsstønad"
//              ],
//              "@id": "test",
//              "@behovId": "behovId",
//              "ident": "$ident",
//              "fom": "2025-01-01",
//              "tom": "2025-01-10",
//              "testmelding": true,
//              "@opprettet": "2025-01-01T00:00:00",
//              "system_read_count": 0,
//              "system_participating_services": [
//                {
//                  "id": "test",
//                  "time": "2025-01-01T00:00:00",
//                  "service": "tiltakspenger-overgangsstønad",
//                  "instance": "tiltakspenger-overgangsstonad",
//                  "image": "ghcr.io/navikt/tiltakspenger-overgangsstonad"
//                }
//              ]
//            }
//        """
//
//    @Test
//    fun happy() {
//        val testRapid = TestRapid()
//        val efsakClient = mockk<EfSakClient>()
//        coEvery { efsakClient.hentOvergangsstønad(ident, any(), any(), any()) }.returns(
//            OvergangsstønadResponse(
//                data = OvergangsstønadResponseData(
//                    perioder = listOf(
//                        OvergangsstønadPeriode(
//                            fomDato = "2025-01-01",
//                            tomDato = "2025-01-10",
//                            datakilde = "test",
//                        ),
//                    ),
//                    melding = "test",
//                    frontendFeilmelding = "test",
//                    status = "test",
//                    stacktrace = "test",
//                ),
//            ),
//        )
//        OvergangsstønadService(testRapid, efsakClient)
//        testRapid.sendTestMessage(behov)
//        with(testRapid.inspektør) {
//            val løsning = this.message(0)["@løsning.overgangsstønad"]
//            Assertions.assertEquals(1, size)
//            Assertions.assertEquals(1, løsning["perioder"].size())
//            Assertions.assertEquals("\"2025-01-01\"", løsning["perioder"].get(0)["fomDato"].toString())
//            Assertions.assertEquals("\"2025-01-10\"", løsning["perioder"].get(0)["tomDato"].toString())
//            Assertions.assertEquals("\"test\"", løsning["perioder"].get(0)["datakilde"].toString())
//        }
//    }
}
