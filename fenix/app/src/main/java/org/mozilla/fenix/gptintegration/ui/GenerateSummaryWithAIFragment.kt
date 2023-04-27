package org.mozilla.fenix.gptintegration.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.FragmentGenerateSummaryWithAiBinding
import org.mozilla.fenix.ext.requireComponents
import org.mozilla.fenix.ext.showToolbar
import org.mozilla.fenix.gptintegration.models.ChatCompletion
import org.mozilla.fenix.gptintegration.models.Choice
import org.mozilla.fenix.gptintegration.models.Message
import org.mozilla.fenix.gptintegration.models.Usage
import org.mozilla.fenix.gptintegration.models.toChatCompletion
import org.mozilla.fenix.gptintegration.usecases.GPTIntegrationStates
import java.lang.IllegalArgumentException

class GenerateSummaryWithAIFragment : Fragment(R.layout.fragment_generate_summary_with_ai) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val args by navArgs<GenerateSummaryWithAIFragmentArgs>()

    @VisibleForTesting
    internal lateinit var bundleArgs: Bundle

    private var binding: FragmentGenerateSummaryWithAiBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentGenerateSummaryWithAiBinding.bind(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundleArgs = args.toBundle()
    }

    override fun onResume() {
        super.onResume()
        showToolbar(getString(R.string.browser_menu_gpt_integration))

        scope.launch {
            when (val state = requireComponents.useCases.getPageSummaryWithGPTUseCases.invoke(body = args.body)) {
               is GPTIntegrationStates.Loaded -> {
                   scope.launch(Dispatchers.Main) {
                       @OptIn(DelicateCoroutinesApi::class)
                       GlobalScope.launch(Dispatchers.Main) {
                           binding?.progressBar?.visibility = View.GONE

                           state.chatCompletion.choices.firstOrNull()?.let {
                               binding?.contentText?.text = it.message.content
                           }
                       }
                   }
               }
               is GPTIntegrationStates.Error -> {
                   @OptIn(DelicateCoroutinesApi::class)
                   GlobalScope.launch(Dispatchers.Main) {
                       binding?.progressBar?.visibility = View.GONE
                       binding?.contentText?.text = state.message
                   }
               }
            }
        }
    }

    private fun convertJSONObjectTOChatCompletion(json: JSONObject): ChatCompletion {
        try {
          return json.toChatCompletion()
        } catch (e: Exception) {
            val errorMessage = "An error just happened while parsing the GPT response."

            @OptIn(DelicateCoroutinesApi::class)
            GlobalScope.launch(Dispatchers.Main) {
                binding?.contentText?.text = e.message ?: errorMessage
            }
            throw IllegalArgumentException(errorMessage)
        }
    }
}
