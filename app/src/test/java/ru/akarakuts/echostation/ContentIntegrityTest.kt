package ru.akarakuts.echostation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import ru.akarakuts.echostation.story.ContentParser
import java.io.File

@RunWith(RobolectricTestRunner::class)
class ContentIntegrityTest {
    private fun readAsset(relative: String): String {
        val root = File("src/main/assets")
        val f = File(root, relative)
        check(f.exists()) { "Missing ${f.absolutePath}" }
        return f.readText()
    }

    @Test
    fun levelsAndRewardsAreConsistent() {
        val levels = ContentParser.parseLevels(readAsset("levels/levels.json"))
        val rewards = ContentParser.parseRewards(readAsset("story/rewards.json")).associateBy { it.id }
        assertEquals(80, levels.size)
        assertTrue(rewards.size >= 80)
        levels.forEachIndexed { index, level ->
            assertEquals(index + 1, level.id)
            assertTrue("missing reward ${level.storyRewardId}", rewards.containsKey(level.storyRewardId))
            val reward = rewards.getValue(level.storyRewardId)
            assertTrue(reward.titles.containsKey("en"))
            assertTrue(reward.bodies.containsKey("en"))
            assertTrue(reward.titles.containsKey("ru"))
            if (level.id == 1) {
                assertEquals(null, level.unlockPrevId)
            } else {
                assertEquals(level.id - 1, level.unlockPrevId)
            }
        }
        listOf("epilogue_broadcast", "epilogue_archive", "epilogue_leave").forEach {
            assertTrue(rewards.containsKey(it))
            assertTrue(rewards.getValue(it).titles.size >= 2)
        }
    }
}
