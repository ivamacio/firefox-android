package org.mozilla.fenix.gptintegration.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.FragmentGptIntegrationBinding
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.ext.showToolbar
import org.mozilla.fenix.ext.toEditable

class GPTIntegrationFragment : Fragment(R.layout.fragment_gpt_integration) {

    private var binding: FragmentGptIntegrationBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGptIntegrationBinding.bind(view)

        binding?.apiKeyField?.text = view.context.settings().gptApiKey.toEditable()
        binding?.apiKeyField?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                view.context.settings().addApiKey(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onResume() {
        super.onResume()
        showToolbar(getString(R.string.preferences_gpt_integration))
    }

}
