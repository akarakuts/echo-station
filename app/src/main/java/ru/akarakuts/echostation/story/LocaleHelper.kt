/** LocaleHelper — язык сюжета по системной локали (без override в приложении). */
package ru.akarakuts.echostation.story

import java.util.Locale

object AppLocales {
    /** Теги в story JSON / values-* */
    val supported: List<String> = listOf(
        "en", "ru", "de", "fr", "es", "pt", "pt-BR", "it", "pl", "uk", "tr",
        "ja", "ko", "zh-CN", "zh-TW", "ar", "hi", "id", "vi", "th", "nl", "sv", "cs",
    )

    fun storyLang(system: Locale): String {
        val full = system.toLanguageTag()
        val tag = when {
            full.startsWith("pt-BR", ignoreCase = true) ||
                (system.language == "pt" && system.country.equals("BR", true)) -> "pt-BR"
            full.startsWith("zh-TW", ignoreCase = true) ||
                system.country.equals("TW", true) -> "zh-TW"
            full.startsWith("zh", ignoreCase = true) -> "zh-CN"
            else -> system.language
        }
        return when {
            tag in supported -> tag
            tag.startsWith("zh") && "zh-CN" in supported -> "zh-CN"
            tag.startsWith("pt") && "pt" in supported -> "pt"
            else -> "en"
        }
    }
}

fun StoryReward.titleFor(lang: String): String =
    titles[lang] ?: titles["en"] ?: titles.values.firstOrNull().orEmpty()

fun StoryReward.bodyFor(lang: String): String =
    bodies[lang] ?: bodies["en"] ?: bodies.values.firstOrNull().orEmpty()
