package org.mozilla.fenix.gptintegration.usecases

import org.json.JSONObject
import org.mozilla.fenix.gptintegration.apis.GPTIntegrationApiInterface
import org.mozilla.fenix.gptintegration.models.ChatCompletion
import org.mozilla.fenix.gptintegration.models.toChatCompletion
import kotlin.IllegalArgumentException

const val MAX_CHARACTERS_DUE_TO_TOKEN_LIMITATION = 7943

class GetPageSummaryWithGPTUseCases(private val repository: GPTIntegrationApiInterface) {

    suspend fun invoke(body: String?): GPTIntegrationStates {
        val response = repository.fetch(shrinkBody(body))
        return when (response.status) {
            200 -> {
                val responseBody = response.body.string(Charsets.UTF_8)
                try {
                    val chatCompletion =
                        convertJSONObjectTOChatCompletion(json = JSONObject(responseBody))

                    GPTIntegrationStates.Loaded(chatCompletion = chatCompletion)
                } catch (e: Exception) {
                    GPTIntegrationStates.Error(message = e.message.toString())
                }
            }
            else -> {
                GPTIntegrationStates.Error(message = "An error just happened, please check your API Key and integration with ChatGPT.")
            }
        }
    }

    private fun shrinkBody(body: String?): String {
        return when {
            body == null -> ""
            body.length <= MAX_CHARACTERS_DUE_TO_TOKEN_LIMITATION -> body
            else -> body.substring(0, MAX_CHARACTERS_DUE_TO_TOKEN_LIMITATION)
        }
    }

    private fun convertJSONObjectTOChatCompletion(json: JSONObject): ChatCompletion {
        try {
            return json.toChatCompletion()
        } catch (e: Exception) {
            throw IllegalArgumentException("An error just happened while parsing the GPT response.")
        }
    }
}

sealed interface GPTIntegrationStates {
    class Loaded(val chatCompletion: ChatCompletion): GPTIntegrationStates
    class Error(val message: String): GPTIntegrationStates
}
