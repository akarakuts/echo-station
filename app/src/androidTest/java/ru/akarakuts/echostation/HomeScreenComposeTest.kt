package ru.akarakuts.echostation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenComposeTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun str(id: Int, vararg args: Any) = rule.activity.getString(id, *args)

    @Test
    fun homeShowsBrandAndCta() {
        rule.onNodeWithContentDescription("night shift").assertIsDisplayed()
    }

    @Test
    fun smokeHomeSettingsArchiveHubWave() {
        rule.onNodeWithContentDescription("night shift").assertIsDisplayed()

        rule.onNodeWithContentDescription("settings").performClick()
        rule.onNodeWithText(str(R.string.sound)).assertIsDisplayed()
        rule.onNodeWithText(str(R.string.ambiance)).assertIsDisplayed()
        rule.onNodeWithText(str(R.string.reduce_motion)).assertIsDisplayed()
        rule.onNodeWithText(str(R.string.about_version, BuildConfig.VERSION_NAME))
            .performScrollTo()
            .assertIsDisplayed()
        rule.onNodeWithText(str(R.string.back)).performClick()

        rule.onNodeWithContentDescription("archive").performClick()
        rule.onNodeWithText(str(R.string.archive_tab_log)).assertIsDisplayed()
        rule.onNodeWithText(str(R.string.archive_tab_voice)).performClick()
        rule.onNodeWithText(str(R.string.archive_tab_photo)).performClick()
        rule.onNodeWithText(str(R.string.archive_tab_letter)).performClick()
        rule.onNodeWithText(str(R.string.back)).performClick()

        rule.onNodeWithContentDescription("night shift").performClick()
        rule.onNodeWithText(str(R.string.hub_title)).assertIsDisplayed()
        rule.onNodeWithTag("frame-1").assertIsDisplayed().performClick()
        dismissHowToIfShown()
        rule.onNodeWithContentDescription("phase").assertIsDisplayed()
        rule.onNodeWithContentDescription("hint").performClick()
        rule.onNodeWithContentDescription("reset").performClick()
        leavePuzzle()
        rule.onNodeWithText(str(R.string.hub_title)).assertIsDisplayed()
        rule.onNodeWithTag("frame-7").performScrollTo().assertIsDisplayed()
    }

    private fun dismissHowToIfShown() {
        rule.waitUntil(5_000) {
            rule.onAllNodesWithText(str(R.string.got_it)).fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithContentDescription("hint").fetchSemanticsNodes().isNotEmpty()
        }
        if (rule.onAllNodesWithText(str(R.string.got_it)).fetchSemanticsNodes().isNotEmpty()) {
            rule.onNodeWithText(str(R.string.got_it)).performClick()
            rule.waitForIdle()
        }
    }

    private fun leavePuzzle() {
        rule.onNodeWithText(str(R.string.back)).performClick()
        rule.waitForIdle()
        if (rule.onAllNodesWithText(str(R.string.confirm)).fetchSemanticsNodes().isNotEmpty()) {
            rule.onNodeWithText(str(R.string.confirm)).performClick()
            rule.waitForIdle()
        }
    }
}
