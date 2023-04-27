package org.mozilla.fenix.gptintegration.usecases

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mozilla.fenix.gptintegration.apis.HTTPIntegrationApiInterface

class GetHTTPPageBodyUseCasesTest {
    private lateinit var repository: HTTPIntegrationApiInterface
    private lateinit var getHTTPPageBodyUseCases: GetHTTPPageBodyUseCases

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        getHTTPPageBodyUseCases = GetHTTPPageBodyUseCases(repository = repository)
    }

    @Test
    fun `verify if body text shrinks based on number of characters`() = runTest {
        val url = "https://www.pumabrowser.com/blog/how-to-web-monetize-your-content-and-support-creators-online-with-micropayments"

        getHTTPPageBodyUseCases.invoke(url)

        coVerify {
            repository.fetch(url)
        }
    }
}
