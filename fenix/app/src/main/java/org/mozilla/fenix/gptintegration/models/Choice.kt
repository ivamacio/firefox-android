package org.mozilla.fenix.gptintegration.models

data class Choice(
    val message: Message,
    val finish_reason: String,
    val index: Int
)
