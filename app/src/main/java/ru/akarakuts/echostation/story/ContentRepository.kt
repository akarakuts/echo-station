/** ContentRepository — загрузка levels/rewards из assets с кэшем. */
package ru.akarakuts.echostation.story

import android.content.Context

class ContentRepository(private val context: Context) {
    @Volatile private var levels: List<LevelDef>? = null
    @Volatile private var rewards: Map<String, StoryReward>? = null

    fun levels(): List<LevelDef> {
        levels?.let { return it }
        val json = context.assets.open("levels/levels.json").bufferedReader().use { it.readText() }
        return ContentParser.parseLevels(json).also { levels = it }
    }

    fun level(id: Int): LevelDef? = levels().firstOrNull { it.id == id }

    fun rewards(): Map<String, StoryReward> {
        rewards?.let { return it }
        val json = context.assets.open("story/rewards.json").bufferedReader().use { it.readText() }
        return ContentParser.parseRewards(json).associateBy { it.id }.also { rewards = it }
    }

    fun reward(id: String): StoryReward? = rewards()[id]
}
