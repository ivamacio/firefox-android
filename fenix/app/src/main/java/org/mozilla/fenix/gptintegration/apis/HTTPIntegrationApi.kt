package org.mozilla.fenix.gptintegration.apis

import mozilla.components.concept.fetch.Client
import mozilla.components.concept.fetch.Request
import mozilla.components.concept.fetch.Response
import mozilla.components.service.digitalassetlinks.ext.parseJsonBody

class HTTPIntegrationApi(
    private val client: Client,
): HTTPIntegrationApiInterface {

    override suspend fun fetch(url: String): String {
       return client.fetch(Request(url)).parseJsonBody { it } ?: ""
    }
}

interface HTTPIntegrationApiInterface {
    suspend fun fetch(url: String): String
}
