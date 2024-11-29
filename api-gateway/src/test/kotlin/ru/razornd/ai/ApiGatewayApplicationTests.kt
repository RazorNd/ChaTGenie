package ru.razornd.ai

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@Suppress("SpringBootApplicationProperties")
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = ["chat-genie-url=http://localhost:\${wiremock.server.port}/"])
@AutoConfigureWireMock(port = 0)
class ApiGatewayApplicationTests {

    @Autowired
    lateinit var testClient: WebTestClient

    private val token =
        "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.Ll" +
                "0mYpGz5N8jUqtr8orc_3xLWvZfE95A1vYiQSCzfuD_8iyz-P4RpAKS2lRIJeBJ1XEX9XTo2--jPcT_spQgfMH_pCIV5HatPvNCtf" +
                "m_CyJwBA2fl4FWzhi38MzMCVlcmgpT5Luq4PF_aFPLaIk_HtjKDjoxz_II2ENf8LG4JIa5ENyLM_IIb5iR6KSPSVuzOIucNbh6nB" +
                "Jkxzk76Gc1upzRxwf_ML4VgD8WMOv5shqctulihUKP-9k6zmoy4jTlueqBv51GAfbPkP3inZFS5wNmumjYfMTFyvQ6XQftNyWKAC" +
                "2wFJObcmPBis6POAnauCs6xn6vJ01QimUaza0BQQA7YSCZHv2ofjy5vPNCdvrffbL4m_s_3lWA1M8DkgRBFsbdKPv4bKTWb-XIw9" +
                "UcwUuVPpJ_mNaum_markNYeAlkusaeWbCZtjrfDMyHjXCzQIWskfQXQbGt0gL1u0-yVaXAg8pa5jnK8tIlYmQNbmxALSyeg4XXTS" +
                "EURi-Y0nOwzIlUfi_MWD7yhM2eTkc1or3r9ro8kTtecWfrevh3Le79hPTBAtOIAfoCOksAZDBD5OzBc52m_0NJijRAg3_E0DHOUq" +
                "2M51p8hlFmbj56Uee3lbpeu2HXqAC5HRi_t9CksHAc_dwGpun2wB2PhcyOTMUc64FrWetLdf8RkEyGF7A"

    @Test
    fun proxyApi() {
        val expectedBody = """{"ok":  true}"""
        stubFor(
            post(urlPathEqualTo("/api/generate/suggestions"))
                .willReturn(okJson(expectedBody))
        )

        testClient.post()
            .uri("/api/generate/suggestions")
            .headers { it.setBearerAuth(token) }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"generate": 1}""")
            .exchange()
            .expectAll(
                { it.expectStatus().isOk },
                { it.expectBody().json(expectedBody) }
            )

        verify(postRequestedFor(urlMatching(".*userId=1234567890.*")))
    }
}
