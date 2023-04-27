package org.mozilla.fenix.gptintegration.models

import org.json.JSONObject

fun JSONObject.toChatCompletion(): ChatCompletion {
    val json = this

    val id = json.getString("id")
    val obj = json.getString("object")
    val created = json.getLong("created")
    val model = json.getString("model")

    val usageJson = json.getJSONObject("usage")
    val promptTokens = usageJson.getInt("prompt_tokens")
    val completionTokens = usageJson.getInt("completion_tokens")
    val totalTokens = usageJson.getInt("total_tokens")

    val usage = Usage(promptTokens, completionTokens, totalTokens)

    val choicesJson = json.getJSONArray("choices")
    val choices = mutableListOf<Choice>()

    for (i in 0 until choicesJson.length()) {
        val choiceJson = choicesJson.getJSONObject(i)

        val messageJson = choiceJson.getJSONObject("message")
        val role = messageJson.getString("role")
        val content = messageJson.getString("content")
        val message = Message(role, content)

        val finishReason = choiceJson.getString("finish_reason")
        val index = choiceJson.getInt("index")

        val choice = Choice(message, finishReason, index)
        choices.add(choice)
    }

    return ChatCompletion(id, obj, created, model, usage, choices)
}
