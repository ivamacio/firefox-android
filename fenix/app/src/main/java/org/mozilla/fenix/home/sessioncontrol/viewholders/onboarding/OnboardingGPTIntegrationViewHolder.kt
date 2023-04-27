/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.sessioncontrol.viewholders.onboarding

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import mozilla.components.service.glean.private.NoExtras
import mozilla.components.support.base.log.Log
import org.mozilla.fenix.GleanMetrics.Onboarding
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.OnboardingFinishBinding
import org.mozilla.fenix.databinding.OnboardingGptIntegrationBinding
import org.mozilla.fenix.ext.settings
import org.mozilla.fenix.ext.toEditable
import org.mozilla.fenix.utils.Settings

class OnboardingGPTIntegrationViewHolder(
    view: View,
    settings: Settings
) : RecyclerView.ViewHolder(view) {

    init {
        val binding = OnboardingGptIntegrationBinding.bind(view)
        binding.apiKeyField.text = settings.gptApiKey.toEditable()

        binding.apiKeyField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                view.context.settings().addApiKey(s.toString())
                Onboarding.gptIntegration.record(Onboarding.GptIntegrationExtra(s.toString()))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    companion object {
        const val LAYOUT_ID = R.layout.onboarding_gpt_integration
    }
}
