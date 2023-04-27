package org.mozilla.fenix.gptintegration.usecases

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mozilla.components.concept.fetch.MutableHeaders
import mozilla.components.concept.fetch.Response
import mozilla.components.support.test.any
import org.junit.Before
import org.junit.Test
import org.mozilla.fenix.gptintegration.apis.GPTIntegrationApi
import java.io.ByteArrayInputStream
import java.io.InputStream

class GetPageSummaryWithGPTUseCasesTest {
    private lateinit var repository: GPTIntegrationApi
    private lateinit var getPageSummaryWithGPTUseCases: GetPageSummaryWithGPTUseCases

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        getPageSummaryWithGPTUseCases = GetPageSummaryWithGPTUseCases(repository = repository)
    }

    @Test
    fun `verify if body text shrinks based on number of characters`() = runTest {
        val body = generateRandomString(10000)
        val expectedBodyResponse = body.substring(0, MAX_CHARACTERS_DUE_TO_TOKEN_LIMITATION)

        getPageSummaryWithGPTUseCases.invoke(body)

        coVerify {
            repository.fetch(expectedBodyResponse)
        }
    }

    @Test
    fun `verify if body text length remains the same`() = runTest {
        val length = 100
        val body = generateRandomString(length)

        getPageSummaryWithGPTUseCases.invoke(body)

        coVerify {
            repository.fetch(body)
        }
    }

    @Test
    fun `verify if body text is equals to empty string when value is null`() = runTest {
        val body = null
        val expectedBody = ""

        getPageSummaryWithGPTUseCases.invoke(body)

        coVerify {
            repository.fetch(expectedBody)
        }
    }

    @Test
    fun `when response status is 200, app returns Loaded state`() = runTest {
        val response = Response(
            url = "https://www.pumabrowser.com/blog/how-to-web-monetize-your-content-and-support-creators-online-with-micropayments",
            status = 200,
            headers = MutableHeaders(),
            body = Response.Body(jsonToInputStream("{\"id\":\"chatcmpl-79y83fDxJOj6gzDfzFxr1YTT0gZCl\",\"object\":\"chat.completion\",\"created\":1682610443,\"model\":\"gpt-3.5-turbo-0301\",\"usage\":{\"prompt_tokens\":1986,\"completion_tokens\":145,\"total_tokens\":2131},\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"Mothers often tell white lies to their children, and Atlas Obscura has revealed some of the most bizarre fibs parents have used. Over 500 submissions were received, with many lies similar across cultures such as, making funny faces will cause them to stay that way or eating before swimming causes cramps or death. However, other lies were more unique, including the claim a little man lives in children’s eyes who signals mothers when they are telling the truth or the dangers of dragonflies sewing a child’s mouth closed. Lies based around food were also common, including that eating burnt toast would remove curls from hair, or that bread crusts could make a child taller, whiten their teeth or cause their hair to curl.\"},\"finish_reason\":\"stop\",\"index\":0}]}\n")),
        )
        coEvery { repository.fetch(any()) } returns (response)

        val state = getPageSummaryWithGPTUseCases.invoke(any())

        assert(state is GPTIntegrationStates.Loaded)
    }

    @Test
    fun `when there is an error parsing the json response even though status code is 200 then app returns Error state`() = runTest {
        val response = Response(
            url = "https://www.pumabrowser.com/blog/how-to-web-monetize-your-content-and-support-creators-online-with-micropayments",
            status = 200,
            headers = MutableHeaders(),
            body = Response.Body.empty(),
        )
        coEvery { repository.fetch(any()) } returns (response)

        val state = getPageSummaryWithGPTUseCases.invoke(any())

        assert(state is GPTIntegrationStates.Error)
    }

    @Test
    fun `when response status is 401, app returns Error state`() = runTest {
        val response = Response(
            url = "https://www.pumabrowser.com/blog/how-to-web-monetize-your-content-and-support-creators-online-with-micropayments",
            status = 401,
            headers = MutableHeaders(),
            body = Response.Body.empty(),
        )
        coEvery { repository.fetch(mozilla.components.support.test.any()) } returns (response)

        val state = getPageSummaryWithGPTUseCases.invoke(any())

        assert(state is GPTIntegrationStates.Error)
    }

    private fun generateRandomString(length: Int): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun jsonToInputStream(jsonString: String): InputStream {
        val bytes = jsonString.toByteArray()
        return ByteArrayInputStream(bytes)
    }
}
