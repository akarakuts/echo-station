/** ContentParser — разбор JSON уровней/наград; в RAM оставляем en/ru/uk. */
package ru.akarakuts.echostation.story

import org.json.JSONObject
import ru.akarakuts.echostation.puzzle.PuzzleType

object ContentParser {
    fun parseLevels(json: String): List<LevelDef> {
        val root = JSONObject(json)
        val arr = root.getJSONArray("levels")
        val out = ArrayList<LevelDef>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val prev = if (o.isNull("unlockPrevId")) null else o.getInt("unlockPrevId")
            out += LevelDef(
                id = o.getInt("id"),
                act = o.getInt("act"),
                puzzleType = PuzzleType.valueOf(o.getString("puzzleType")),
                difficulty = o.getInt("difficulty"),
                paramsJson = o.getJSONObject("params").toString(),
                storyRewardId = o.getString("storyRewardId"),
                unlockPrevId = prev,
            )
        }
        return out.sortedBy { it.id }
    }

    fun parseRewards(json: String): List<StoryReward> {
        val root = JSONObject(json)
        val arr = root.getJSONArray("rewards")
        val out = ArrayList<StoryReward>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val titles: Map<String, String>
            val bodies: Map<String, String>
            if (o.has("titles")) {
                titles = stringMap(o.getJSONObject("titles"))
                bodies = stringMap(o.getJSONObject("bodies"))
            } else {
                // Legacy en/ru fields
                titles = mapOf(
                    "en" to o.optString("titleEn"),
                    "ru" to o.optString("titleRu"),
                )
                bodies = mapOf(
                    "en" to o.optString("bodyEn"),
                    "ru" to o.optString("bodyRu"),
                )
            }
            out += StoryReward(
                id = o.getString("id"),
                kind = RewardKind.valueOf(o.getString("kind")),
                titles = titles,
                bodies = bodies,
                imageAsset = if (o.isNull("imageAsset")) null else o.optString("imageAsset").ifBlank { null },
                archiveKey = if (o.isNull("archiveKey")) null else o.optString("archiveKey").ifBlank { null },
            )
        }
        return out
    }

    private fun stringMap(o: JSONObject): Map<String, String> {
        val out = LinkedHashMap<String, String>(4)
        for (k in arrayOf("en", "ru", "uk")) {
            if (o.has(k)) out[k] = o.getString(k)
        }
        if (out.isEmpty()) {
            val keys = o.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                out[k] = o.getString(k)
            }
        }
        return out
    }
}
