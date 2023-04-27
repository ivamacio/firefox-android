package org.mozilla.fenix.home.sessioncontrol.viewholders.onboarding

import android.view.LayoutInflater
import io.mockk.every
import io.mockk.mockk
import mozilla.components.support.test.robolectric.testContext
import mozilla.telemetry.glean.testing.GleanTestRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mozilla.fenix.GleanMetrics.Onboarding
import org.mozilla.fenix.databinding.OnboardingGptIntegrationBinding
import org.mozilla.fenix.ext.components
import org.mozilla.fenix.helpers.FenixRobolectricTestRunner
import org.mozilla.fenix.utils.Settings


@RunWith(FenixRobolectricTestRunner::class)
class OnboardingGPTIntegrationViewHolderTest {

    @get:Rule
    val gleanTestRule = GleanTestRule(testContext)

    private lateinit var binding: OnboardingGptIntegrationBinding
    private lateinit var mockSettings: Settings

    @Before
    fun setup() {
        mockSettings = mockk(relaxed = true)
        binding = OnboardingGptIntegrationBinding.inflate(LayoutInflater.from(testContext))
    }

    @Test
    fun `verify key when user enters a new value`() {
        val apiKey = "API_KEY"
        OnboardingGPTIntegrationViewHolder(binding.root, mockSettings)
        every { testContext.components.settings } returns mockk(relaxed = true)
        every { mockSettings.gptApiKey } returns apiKey

        binding.apiKeyField.setText(apiKey)
        Assert.assertNotNull(Onboarding.gptIntegration.testGetValue())
        Assert.assertEquals(1, Onboarding.gptIntegration.testGetValue()!!.size)
        Assert.assertNotNull(Onboarding.gptIntegration.testGetValue()!!.single().extra)
        Assert.assertEquals(apiKey, Onboarding.gptIntegration.testGetValue()!!.single().extra!!.values.first())
    }
}
