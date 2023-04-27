package org.mozilla.fenix.gptintegration.models

data class ChatCompletion(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val usage: Usage,
    val choices: List<Choice>
)


