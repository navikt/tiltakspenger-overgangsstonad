package no.nav.tiltakspenger.overgangsstonad

import io.mockk.coEvery
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.overgangsstonad.efsak.EfSakClient
import no.nav.tiltakspenger.overgangsstonad.efsak.OvergangsstønadPeriode
import no.nav.tiltakspenger.overgangsstonad.efsak.OvergangsstønadResponse
import no.nav.tiltakspenger.overgangsstonad.efsak.OvergangsstønadResponseData
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

internal class OvergangsstønadServiceTest {
    private val ident = "123"

    @Language("JSON")
    private val behov = """
            {
              "@behov": [
                "overgangsstønad"
              ],
              "@id": "test",
              "@behovId": "behovId",
              "ident": "$ident",
              "fom": "2025-01-01",
              "tom": "2025-01-10",
              "@opprettet": "2025-01-01T00:00:00",
              "system_read_count": 0,
              "system_participating_services": [
                {
                  "id": "test",
                  "time": "2025-01-01T00:00:00",
                  "service": "tiltakspenger-overgangsstønad",
                  "instance": "tiltakspenger-overgangsstonad",
                  "image": "ghcr.io/navikt/tiltakspenger-overgangsstonad"
                }
              ]
            }
        """

    @Test
    fun happy() {
        val testRapid = TestRapid()
        val efsakClient = mockk<EfSakClient>()
        coEvery { efsakClient.hentOvergangsstønad(ident, any(), any(), any()) }.returns(
            OvergangsstønadResponse(
                data = OvergangsstønadResponseData(
                    perioder = listOf(
                        OvergangsstønadPeriode(
                            personIdent = ident,
                            fomDato = "2025-01-01",
                            tomDato = "2025-01-10",
                            datakilde = "test",
                        ),
                    ),
                ),
                melding = "test",
                frontendFeilmelding = "test",
                status = "SUKSESS",
                stacktrace = "test",
            ),
        )
        OvergangsstønadService(testRapid, efsakClient)
        testRapid.sendTestMessage(behov)
        with(testRapid.inspektør) {
            println(message(0).toPrettyString())
            Assertions.assertEquals(1, size)
            JSONAssert.assertEquals(
                svar,
                message(0).toPrettyString(),
                JSONCompareMode.LENIENT,
            )
        }
    }

    private val svar = """
            {
              "@løsning": {
                "overgangsstønad" : {
                  "overgangsstønader": [
                    {
                      "fom": "2025-01-01",
                      "tom": "2025-01-10",
                      "datakilde": "test"
                    }
                  ],
                  "feil": null
                }
              }
            }
    """.trimIndent()
}
