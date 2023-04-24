package no.nav.tiltakspenger.overgangsstonad

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import no.nav.tiltakspenger.overgangsstonad.auth.AzureTokenProvider
import no.nav.tiltakspenger.overgangsstonad.efsak.EfSakClient

object Configuration {
    val rapidsAndRivers = mapOf(
        "RAPID_APP_NAME" to "tiltakspenger-overgangsstonad",
        "KAFKA_BROKERS" to System.getenv("KAFKA_BROKERS"),
        "KAFKA_CREDSTORE_PASSWORD" to System.getenv("KAFKA_CREDSTORE_PASSWORD"),
        "KAFKA_TRUSTSTORE_PATH" to System.getenv("KAFKA_TRUSTSTORE_PATH"),
        "KAFKA_KEYSTORE_PATH" to System.getenv("KAFKA_KEYSTORE_PATH"),
        "KAFKA_RAPID_TOPIC" to "tpts.rapid.v1",
        "KAFKA_RESET_POLICY" to "latest",
        "KAFKA_CONSUMER_GROUP_ID" to "tiltakspenger-overgangsstonad-v1",
    )
    private val otherDefaultProperties = mapOf(
        "AZURE_APP_CLIENT_ID" to System.getenv("AZURE_APP_CLIENT_ID"),
        "AZURE_APP_CLIENT_SECRET" to System.getenv("AZURE_APP_CLIENT_SECRET"),
        "AZURE_APP_WELL_KNOWN_URL" to System.getenv("AZURE_APP_WELL_KNOWN_URL"),
        "HTTP_PROXY" to System.getenv("HTTP_PROXY"),
    )
    private val defaultProps = ConfigurationMap(rapidsAndRivers + otherDefaultProperties)
    private val localProps = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.LOCAL.toString(),
            "EF_SAK_URL" to "https://familie-ef-sak.intern.nav.no",
            "EF_SAK_SCOPE" to "api://dev-gcp.teamfamilie.familie-ef-sak/.default",
        ),
    )
    private val devProps = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.DEV.toString(),
            "EF_SAK_URL" to "https://familie-ef-sak.intern.dev.nav.no",
            "EF_SAK_SCOPE" to "api://dev-gcp.teamfamilie.familie-ef-sak/.default",
        ),
    )
    private val prodProps = ConfigurationMap(
        mapOf(
            "application.profile" to Profile.PROD.toString(),
            "EF_SAK_URL" to "https://familie-ef-sak.intern.nav.no",
            "EF_SAK_SCOPE" to "api://prod-gcp.teamfamilie.familie-ef-sak/.default",
        ),
    )

    private fun config() = when (System.getenv("NAIS_CLUSTER_NAME") ?: System.getProperty("NAIS_CLUSTER_NAME")) {
        "dev-gcp" -> systemProperties() overriding EnvironmentVariables overriding devProps overriding defaultProps
        "prod-gcp" -> systemProperties() overriding EnvironmentVariables overriding prodProps overriding defaultProps
        else -> systemProperties() overriding EnvironmentVariables overriding localProps overriding defaultProps
    }

//    data class OauthConfig(
//        val scope: String = config()[Key("EF_SAK_SCOPE", stringType)],
//        val clientId: String = config()[Key("AZURE_APP_CLIENT_ID", stringType)],
//        val clientSecret: String = config()[Key("AZURE_APP_CLIENT_SECRET", stringType)],
//        val wellknownUrl: String = config()[Key("AZURE_APP_WELL_KNOWN_URL", stringType)],
//    )

    fun oauthConfig(
        scope: String = config()[Key("EF_SAK_SCOPE", stringType)],
        clientId: String = config()[Key("AZURE_APP_CLIENT_ID", stringType)],
        clientSecret: String = config()[Key("AZURE_APP_CLIENT_SECRET", stringType)],
        wellknownUrl: String = config()[Key("AZURE_APP_WELL_KNOWN_URL", stringType)],
    ) = AzureTokenProvider.OauthConfig(
        scope = scope,
        clientId = clientId,
        clientSecret = clientSecret,
        wellknownUrl = wellknownUrl,
    )

    fun efClientConfig(baseUrl: String = config()[Key("EF_SAK_URL", stringType)]) =
        EfSakClient.EFClientConfig(baseUrl = baseUrl)

    @JvmInline
    value class EfsakConfig(val efsakUrl: String = config()[Key("EF_SAK_URL", stringType)])

    fun httpProxy(): String? = config().getOrNull(Key("HTTP_PROXY", stringType))
}

enum class Profile {
    LOCAL, DEV, PROD
}
