package org.mozilla.fenix.gptintegration.usecases

import org.mozilla.fenix.gptintegration.apis.HTTPIntegrationApiInterface

class GetHTTPPageBodyUseCases(private val repository: HTTPIntegrationApiInterface) {

    suspend fun invoke(url: String) = repository.fetch(url)

}
