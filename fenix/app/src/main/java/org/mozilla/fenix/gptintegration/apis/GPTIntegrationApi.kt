package org.mozilla.fenix.gptintegration.apis

import mozilla.components.concept.fetch.Client
import mozilla.components.concept.fetch.MutableHeaders
import mozilla.components.concept.fetch.Request
import mozilla.components.concept.fetch.Response
import org.json.JSONArray
import org.json.JSONObject
import org.mozilla.fenix.utils.Settings

private const val BASE_API_URL = "https://api.openai.com/"

class GPTIntegrationApi(
    settings: Settings,
    private val client: Client,
): GPTIntegrationApiInterface {

    private val apiKey = settings.gptApiKey

    override suspend fun fetch(body: String?): Response {
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        val jsonObjectSubItem = JSONObject()
        jsonObjectSubItem.put("role", "user")
        jsonObjectSubItem.put("content",
         "Summarize the following text: $body"
        )
        jsonArray.put(jsonObjectSubItem)
        jsonObject.put("model", "gpt-3.5-turbo")
        jsonObject.put("max_tokens", 1028)
        jsonObject.put("messages", jsonArray)

        val request = Request(
            url = BASE_API_URL + "v1/chat/completions",
            method = Request.Method.POST,
            headers = MutableHeaders(
                "Content-Type" to "application/json", //; charset=utf-8
                "Authorization" to "Bearer $apiKey",
            ),
            body = Request.Body.fromString(jsonObject.toString())
        )

       return client.fetch(request)
    }
}

interface GPTIntegrationApiInterface {
    suspend fun fetch(body: String?): Response
}
