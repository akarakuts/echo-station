/** EngineFactory — сборка PuzzleEngine из LevelDef.paramsJson. */
package ru.akarakuts.echostation.puzzle

import org.json.JSONArray
import org.json.JSONObject
import ru.akarakuts.echostation.story.LevelDef

object EngineFactory {
    fun create(level: LevelDef): PuzzleEngine = when (level.puzzleType) {
        PuzzleType.WAVE -> wave(JSONObject(level.paramsJson))
        PuzzleType.CABLE -> cable(JSONObject(level.paramsJson))
        PuzzleType.CASSETTE -> cassette(JSONObject(level.paramsJson))
        PuzzleType.FREQUENCY -> frequency(JSONObject(level.paramsJson))
        PuzzleType.MULTI -> multi(JSONObject(level.paramsJson))
    }

    fun wave(o: JSONObject): WaveEngine = WaveEngine(
        WaveParams(
            targetPhase = o.getDouble("targetPhase").toFloat(),
            targetAmplitude = o.getDouble("targetAmplitude").toFloat(),
            noiseSeed = o.getInt("noiseSeed"),
            tolerance = o.getDouble("tolerance").toFloat(),
            holdFrames = o.getInt("holdFrames"),
            targetFrequency = o.optDouble("targetFrequency", 4.0).toFloat(),
            useFrequency = o.optBoolean("useFrequency", false),
            drift = o.optDouble("drift", 0.0).toFloat(),
            hideAmplitude = o.optBoolean("hideAmplitude", false),
            glitchEvery = o.optInt("glitchEvery", 0),
            glitchFrames = o.optInt("glitchFrames", 0),
            envelope = o.optString("envelope", "sine"),
        )
    )

    fun cable(o: JSONObject): CableEngine = CableEngine(
        CableParams(
            pairCount = o.getInt("pairCount"),
            allowCrossing = o.optBoolean("allowCrossing", false),
            seed = o.getInt("seed"),
            mode = o.optString("mode", "match"),
            lockCorrect = o.optBoolean("lockCorrect", false),
            hideDigits = o.optBoolean("hideDigits", false),
            highlightPairId = o.optInt("highlightPairId", -1),
        )
    )

    fun cassette(o: JSONObject): CassetteEngine = CassetteEngine(
        CassetteParams(
            cols = o.getInt("cols"),
            rows = o.getInt("rows"),
            seed = o.getInt("seed"),
            adjacentOnly = o.optBoolean("adjacentOnly", true),
            lockCorrect = o.optBoolean("lockCorrect", false),
        )
    )

    fun frequency(o: JSONObject): FrequencyEngine {
        val targetsArr = o.getJSONArray("targets")
        val targets = (0 until targetsArr.length()).map { targetsArr.getDouble(it).toFloat() }
        return FrequencyEngine(
            FrequencyParams(
                markerCount = o.getInt("markerCount"),
                slotCount = o.getInt("slotCount"),
                tolerance = o.getDouble("tolerance").toFloat(),
                seed = o.getInt("seed"),
                targets = targets,
                assigned = o.optBoolean("assigned", true),
                legend = o.optBoolean("legend", false),
                zones = o.optBoolean("zones", true),
            )
        )
    }

    fun multi(o: JSONObject): MultiEngine {
        val steps: JSONArray = o.getJSONArray("steps")
        val engines = ArrayList<PuzzleEngine>(steps.length())
        for (i in 0 until steps.length()) {
            val step = steps.getJSONObject(i)
            val type = PuzzleType.valueOf(step.getString("type"))
            val params = step.getJSONObject("params")
            engines += when (type) {
                PuzzleType.WAVE -> wave(params)
                PuzzleType.CABLE -> cable(params)
                PuzzleType.CASSETTE -> cassette(params)
                PuzzleType.FREQUENCY -> frequency(params)
                PuzzleType.MULTI -> error("nested MULTI not supported")
            }
        }
        return MultiEngine(engines)
    }
}
