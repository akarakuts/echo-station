#!/usr/bin/env python3
"""Generate multilingual strings.xml + story rewards for Echo Station."""
from __future__ import annotations

import json
import xml.sax.saxutils as xu
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app/src/main/res"
STORY = ROOT / "app/src/main/assets/story/rewards.json"

# Locale folder suffix → BCP47 tag used in settings / story JSON
LOCALES = {
    "": "en",  # values/
    "ru": "ru",
    "de": "de",
    "fr": "fr",
    "es": "es",
    "pt": "pt",
    "pt-rBR": "pt-BR",
    "it": "it",
    "pl": "pl",
    "uk": "uk",
    "tr": "tr",
    "ja": "ja",
    "ko": "ko",
    "zh-rCN": "zh-CN",
    "zh-rTW": "zh-TW",
    "ar": "ar",
    "hi": "hi",
    "id": "id",
    "vi": "vi",
    "th": "th",
    "nl": "nl",
    "sv": "sv",
    "cs": "cs",
}

# UI strings: key → {lang: text}
UI: dict[str, dict[str, str]] = {
    "app_name": {
        "en": "Echo Station", "ru": "Эхо станции", "de": "Echo-Station", "fr": "Station Écho",
        "es": "Estación Eco", "pt": "Estação Eco", "pt-BR": "Estação Eco", "it": "Stazione Eco",
        "pl": "Stacja Echo", "uk": "Ехо станції", "tr": "Yankı İstasyonu", "ja": "エコー・ステーション",
        "ko": "에코 스테이션", "zh-CN": "回声电台", "zh-TW": "迴聲電台", "ar": "محطة الصدى",
        "hi": "इको स्टेशन", "id": "Stasiun Gema", "vi": "Đài Tiếng Vọng", "th": "สถานีเสียงสะท้อน",
        "nl": "Echo Station", "sv": "Ekostation", "cs": "Echo stanice",
    },
    "subtitle": {
        "en": "Rebuild the signal that never finished",
        "ru": "Собери сигнал, который застрял навсегда",
        "de": "Stelle das Signal wieder her, das nie endete",
        "fr": "Reconstruisez le signal qui ne s’est jamais terminé",
        "es": "Reconstruye la señal que nunca terminó",
        "pt": "Reconstrói o sinal que nunca terminou",
        "pt-BR": "Reconstrua o sinal que nunca terminou",
        "it": "Ricostruisci il segnale che non è mai finito",
        "pl": "Odbuduj sygnał, który nigdy się nie skończył",
        "uk": "Збери сигнал, що застряг назавжди",
        "tr": "Hiç bitmeyen sinyali yeniden kur",
        "ja": "終わらなかった信号を取り戻せ",
        "ko": "끝나지 않은 신호를 되살려라",
        "zh-CN": "重建那从未完成的信号",
        "zh-TW": "重建那從未完成的訊號",
        "ar": "أعد بناء الإشارة التي لم تكتمل",
        "hi": "उस संकेत को फिर बनाओ जो अधूरा रह गया",
        "id": "Susun ulang sinyal yang tak pernah selesai",
        "vi": "Dựng lại tín hiệu chưa bao giờ kết thúc",
        "th": "ประกอบสัญญาณที่ยังไม่จบ",
        "nl": "Herbouw het signaal dat nooit eindigde",
        "sv": "Bygg om signalen som aldrig avslutades",
        "cs": "Sestav signál, který nikdy neskončil",
    },
    "cta_night_shift": {
        "en": "Night shift", "ru": "Ночная смена", "de": "Nachtschicht", "fr": "Quart de nuit",
        "es": "Turno de noche", "pt": "Turno da noite", "pt-BR": "Turno da noite", "it": "Turno di notte",
        "pl": "Nocna zmiana", "uk": "Нічна зміна", "tr": "Gece vardiyası", "ja": "夜勤",
        "ko": "야간 근무", "zh-CN": "夜班", "zh-TW": "夜班", "ar": "وردية ليلية",
        "hi": "रात की शिफ्ट", "id": "Shift malam", "vi": "Ca đêm", "th": "กะกลางคืน",
        "nl": "Nachtdienst", "sv": "Nattpass", "cs": "Noční směna",
    },
    "cta_continue": {
        "en": "Continue · Frame %1$d", "ru": "Продолжить · Кадр %1$d",
        "de": "Weiter · Frame %1$d", "fr": "Continuer · Trame %1$d",
        "es": "Continuar · Fotograma %1$d", "uk": "Продовжити · Кадр %1$d",
        "pl": "Kontynuuj · Klatka %1$d", "it": "Continua · Fotogramma %1$d",
        "ja": "続ける · フレーム %1$d", "zh-CN": "继续 · 第 %1$d 帧",
    },
    "duty_hello": {
        "en": "On duty: %1$s", "ru": "На смене: %1$s",
        "de": "Im Dienst: %1$s", "fr": "De service : %1$s",
        "uk": "На зміні: %1$s", "ja": "当直：%1$s", "zh-CN": "值班：%1$s",
    },
    "how_wave": {
        "en": "Match the amber ghost and hold the lock.",
        "ru": "Совмести с янтарной тенью и удержи захват.",
        "uk": "Зістав із бурштиновою тінню й тримай захват.",
        "de": "Triff die bernsteinfarbene Spur und halte den Lock.",
        "fr": "Alignez l’ombre ambre et tenez le verrou.",
    },
    "how_cable_match": {
        "en": "Tap a left port, then its matching colour on the right.",
        "ru": "Нажми левый порт, затем такой же цвет справа.",
        "uk": "Натисни лівий порт, потім той самий колір справа.",
        "de": "Links antippen, dann die passende Farbe rechts.",
        "fr": "Touchez un port gauche, puis la même couleur à droite.",
    },
    "how_cable_untangle": {
        "en": "Swap right-hand ports until the cables no longer cross.",
        "ru": "Меняй правые порты, пока кабели не перестанут пересекаться.",
        "uk": "Міняй праві порти, доки кабелі не розійдуться.",
        "de": "Rechte Ports tauschen, bis sich kein Kabel mehr kreuzt.",
        "fr": "Échangez les ports droits jusqu’à ce que les câbles ne se croisent plus.",
    },
    "how_cassette": {
        "en": "Swap strips until the spectrogram reads in order.",
        "ru": "Меняй полоски, пока спектр не встанет по порядку.",
        "uk": "Міняй смужки, доки спектр не стане в ряд.",
        "de": "Streifen tauschen, bis das Spektrum stimmt.",
        "fr": "Échangez les bandes jusqu’à ce que le spectre soit en ordre.",
    },
    "how_frequency": {
        "en": "Drag each coloured marker onto its ghost pip.",
        "ru": "Перетащи каждый цветной маркер на свою призрачную метку.",
        "uk": "Перетягни кожен кольоровий маркер на свою примарну мітку.",
        "de": "Jeden farbigen Marker auf seinen Geisterpunkt ziehen.",
        "fr": "Glissez chaque marqueur coloré sur son point fantôme.",
    },
    "kind_log": {
        "en": "Air log", "ru": "Лог эфира", "uk": "Лог ефіру", "de": "Funklog", "fr": "Journal d’antenne",
    },
    "kind_voice": {
        "en": "Voice", "ru": "Голос", "uk": "Голос", "de": "Stimme", "fr": "Voix",
    },
    "kind_photo": {
        "en": "Photo", "ru": "Снимок", "uk": "Знімок", "de": "Foto", "fr": "Photo",
    },
    "kind_letter": {
        "en": "Letter", "ru": "Письмо", "uk": "Лист", "de": "Brief", "fr": "Lettre",
    },
    "about_line": {
        "en": "Echo Station · © 2026 Aleksey Karakuts",
        "ru": "Эхо станции · © 2026 Aleksey Karakuts",
    },
    "about_contact": {
        "en": "aleksey@karakuts.com",
        "ru": "aleksey@karakuts.com",
    },
    "archive": {
        "en": "Archive", "ru": "Архив", "de": "Archiv", "fr": "Archives", "es": "Archivo",
        "pt": "Arquivo", "pt-BR": "Arquivo", "it": "Archivio", "pl": "Archiwum", "uk": "Архів",
        "tr": "Arşiv", "ja": "アーカイブ", "ko": "아카이브", "zh-CN": "档案", "zh-TW": "檔案",
        "ar": "الأرشيف", "hi": "संग्रह", "id": "Arsip", "vi": "Lưu trữ", "th": "คลัง",
        "nl": "Archief", "sv": "Arkiv", "cs": "Archiv",
    },
    "settings": {
        "en": "Settings", "ru": "Настройки", "de": "Einstellungen", "fr": "Paramètres", "es": "Ajustes",
        "pt": "Definições", "pt-BR": "Configurações", "it": "Impostazioni", "pl": "Ustawienia", "uk": "Налаштування",
        "tr": "Ayarlar", "ja": "設定", "ko": "설정", "zh-CN": "设置", "zh-TW": "設定",
        "ar": "الإعدادات", "hi": "सेटिंग्स", "id": "Pengaturan", "vi": "Cài đặt", "th": "การตั้งค่า",
        "nl": "Instellingen", "sv": "Inställningar", "cs": "Nastavení",
    },
    "hub_title": {
        "en": "Night map", "ru": "Карта ночи", "de": "Nachtkarte", "fr": "Carte de nuit", "es": "Mapa nocturno",
        "pt": "Mapa da noite", "pt-BR": "Mapa da noite", "it": "Mappa della notte", "pl": "Mapa nocy", "uk": "Карта ночі",
        "tr": "Gece haritası", "ja": "夜の地図", "ko": "밤의 지도", "zh-CN": "夜之地图", "zh-TW": "夜之地圖",
        "ar": "خريطة الليل", "hi": "रात का नक्शा", "id": "Peta malam", "vi": "Bản đồ đêm", "th": "แผนที่ราตรี",
        "nl": "Nachtkaart", "sv": "Nattkarta", "cs": "Mapa noci",
    },
    "act_noise": {
        "en": "Act 1 — Noise", "ru": "Акт 1 — Шум", "de": "Akt 1 — Rauschen", "fr": "Acte 1 — Bruit",
        "es": "Acto 1 — Ruido", "pt": "Ato 1 — Ruído", "pt-BR": "Ato 1 — Ruído", "it": "Atto 1 — Rumore",
        "pl": "Akt 1 — Szum", "uk": "Акт 1 — Шум", "tr": "Perde 1 — Gürültü", "ja": "第1幕 — ノイズ",
        "ko": "1막 — 잡음", "zh-CN": "第一幕 — 噪声", "zh-TW": "第一幕 — 雜訊", "ar": "الفصل 1 — الضجيج",
        "hi": "अंक 1 — शोर", "id": "Babak 1 — Derau", "vi": "Hồi 1 — Nhiễu", "th": "องก์ 1 — เสียงรบกวน",
        "nl": "Akte 1 — Ruis", "sv": "Akt 1 — Brus", "cs": "Akt 1 — Šum",
    },
    "act_name": {
        "en": "Act 2 — Name", "ru": "Акт 2 — Имя", "de": "Akt 2 — Name", "fr": "Acte 2 — Nom",
        "es": "Acto 2 — Nombre", "pt": "Ato 2 — Nome", "pt-BR": "Ato 2 — Nome", "it": "Atto 2 — Nome",
        "pl": "Akt 2 — Imię", "uk": "Акт 2 — Ім’я", "tr": "Perde 2 — İsim", "ja": "第2幕 — 名前",
        "ko": "2막 — 이름", "zh-CN": "第二幕 — 名字", "zh-TW": "第二幕 — 名字", "ar": "الفصل 2 — الاسم",
        "hi": "अंक 2 — नाम", "id": "Babak 2 — Nama", "vi": "Hồi 2 — Tên", "th": "องก์ 2 — ชื่อ",
        "nl": "Akte 2 — Naam", "sv": "Akt 2 — Namn", "cs": "Akt 2 — Jméno",
    },
    "act_letter": {
        "en": "Act 3 — Letter", "ru": "Акт 3 — Письмо", "de": "Akt 3 — Brief", "fr": "Acte 3 — Lettre",
        "es": "Acto 3 — Carta", "pt": "Ato 3 — Carta", "pt-BR": "Ato 3 — Carta", "it": "Atto 3 — Lettera",
        "pl": "Akt 3 — List", "uk": "Акт 3 — Лист", "tr": "Perde 3 — Mektup", "ja": "第3幕 — 手紙",
        "ko": "3막 — 편지", "zh-CN": "第三幕 — 信", "zh-TW": "第三幕 — 信", "ar": "الفصل 3 — الرسالة",
        "hi": "अंक 3 — पत्र", "id": "Babak 3 — Surat", "vi": "Hồi 3 — Thư", "th": "องก์ 3 — จดหมาย",
        "nl": "Akte 3 — Brief", "sv": "Akt 3 — Brev", "cs": "Akt 3 — Dopis",
    },
    "level_locked": {
        "en": "Static", "ru": "Шум", "de": "Rauschen", "fr": "Parasite", "es": "Estática",
        "pt": "Estática", "pt-BR": "Estática", "it": "Statico", "pl": "Szum", "uk": "Шум",
        "tr": "Parazit", "ja": "ノイズ", "ko": "잡음", "zh-CN": "静电", "zh-TW": "靜電",
        "ar": "تشويش", "hi": "स्टैटिक", "id": "Statik", "vi": "Nhiễu", "th": "สัญญาณรบกวน",
        "nl": "Ruis", "sv": "Brus", "cs": "Šum",
    },
    "level_cleared": {
        "en": "Caught", "ru": "Пойман", "de": "Gefangen", "fr": "Capté", "es": "Capturado",
        "pt": "Captado", "pt-BR": "Capturado", "it": "Catturato", "pl": "Schwytany", "uk": "Зловлено",
        "tr": "Yakalandı", "ja": "捕捉", "ko": "포착됨", "zh-CN": "已捕捉", "zh-TW": "已捕捉",
        "ar": "التُقط", "hi": "पकड़ा", "id": "Tertangkap", "vi": "Đã bắt", "th": "จับได้แล้ว",
        "nl": "Gevangen", "sv": "Fångad", "cs": "Zachyceno",
    },
    "reset": {
        "en": "Reset", "ru": "Сброс", "de": "Zurücksetzen", "fr": "Réinitialiser", "es": "Reiniciar",
        "pt": "Repor", "pt-BR": "Redefinir", "it": "Reimposta", "pl": "Reset", "uk": "Скинути",
        "tr": "Sıfırla", "ja": "リセット", "ko": "초기화", "zh-CN": "重置", "zh-TW": "重設",
        "ar": "إعادة", "hi": "रीसेट", "id": "Atur ulang", "vi": "Đặt lại", "th": "รีเซ็ต",
        "nl": "Reset", "sv": "Återställ", "cs": "Reset",
    },
    "hint": {
        "en": "Hint", "ru": "Подсказка", "de": "Hinweis", "fr": "Indice", "es": "Pista",
        "pt": "Dica", "pt-BR": "Dica", "it": "Suggerimento", "pl": "Wskazówka", "uk": "Підказка",
        "tr": "İpucu", "ja": "ヒント", "ko": "힌트", "zh-CN": "提示", "zh-TW": "提示",
        "ar": "تلميح", "hi": "संकेत", "id": "Petunjuk", "vi": "Gợi ý", "th": "คำใบ้",
        "nl": "Hint", "sv": "Tips", "cs": "Nápověda",
    },
    "continue_btn": {
        "en": "Continue", "ru": "Дальше", "de": "Weiter", "fr": "Continuer", "es": "Continuar",
        "pt": "Continuar", "pt-BR": "Continuar", "it": "Continua", "pl": "Dalej", "uk": "Далі",
        "tr": "Devam", "ja": "続ける", "ko": "계속", "zh-CN": "继续", "zh-TW": "繼續",
        "ar": "متابعة", "hi": "जारी रखें", "id": "Lanjut", "vi": "Tiếp tục", "th": "ต่อไป",
        "nl": "Doorgaan", "sv": "Fortsätt", "cs": "Pokračovat",
    },
    "back": {
        "en": "Back", "ru": "Назад", "de": "Zurück", "fr": "Retour", "es": "Atrás",
        "pt": "Voltar", "pt-BR": "Voltar", "it": "Indietro", "pl": "Wstecz", "uk": "Назад",
        "tr": "Geri", "ja": "戻る", "ko": "뒤로", "zh-CN": "返回", "zh-TW": "返回",
        "ar": "رجوع", "hi": "वापस", "id": "Kembali", "vi": "Quay lại", "th": "กลับ",
        "nl": "Terug", "sv": "Tillbaka", "cs": "Zpět",
    },
    "phase": {
        "en": "Phase", "ru": "Фаза", "de": "Phase", "fr": "Phase", "es": "Fase",
        "pt": "Fase", "pt-BR": "Fase", "it": "Fase", "pl": "Faza", "uk": "Фаза",
        "tr": "Faz", "ja": "位相", "ko": "위상", "zh-CN": "相位", "zh-TW": "相位",
        "ar": "الطور", "hi": "फेज", "id": "Fasa", "vi": "Pha", "th": "เฟส",
        "nl": "Fase", "sv": "Fas", "cs": "Fáze",
    },
    "amplitude": {
        "en": "Gain", "ru": "Усиление", "de": "Verstärkung", "fr": "Gain", "es": "Ganancia",
        "pt": "Ganho", "pt-BR": "Ganho", "it": "Guadagno", "pl": "Wzmocnienie", "uk": "Підсилення",
        "tr": "Kazanç", "ja": "ゲイン", "ko": "게인", "zh-CN": "增益", "zh-TW": "增益",
        "ar": "الكسب", "hi": "गेन", "id": "Gain", "vi": "Độ khuếch", "th": "เกน",
        "nl": "Versterking", "sv": "Förstärkning", "cs": "Zisk",
    },
    "carrier": {
        "en": "Carrier", "ru": "Несущая", "de": "Träger", "fr": "Porteuse", "es": "Portadora",
        "pt": "Portadora", "pt-BR": "Portadora", "it": "Portante", "pl": "Nośna", "uk": "Несуча",
        "tr": "Taşıyıcı", "ja": "搬送波", "ko": "반송파", "zh-CN": "载波", "zh-TW": "載波",
        "ar": "الحامل", "hi": "कैरियर", "id": "Pembawa", "vi": "Sóng mang", "th": "คลื่นพาห์",
        "nl": "Draaggolf", "sv": "Bärvåg", "cs": "Nosná",
    },
    "sync": {
        "en": "Sync %1$d%%", "ru": "Синхр. %1$d%%", "de": "Sync %1$d%%", "fr": "Sync %1$d%%",
        "es": "Sinc. %1$d%%", "pt": "Sinc. %1$d%%", "pt-BR": "Sinc. %1$d%%", "it": "Sync %1$d%%",
        "pl": "Synch. %1$d%%", "uk": "Синхр. %1$d%%", "tr": "Senk. %1$d%%", "ja": "同期 %1$d%%",
        "ko": "동기 %1$d%%", "zh-CN": "同步 %1$d%%", "zh-TW": "同步 %1$d%%", "ar": "مزامنة %1$d%%",
        "hi": "सिंक %1$d%%", "id": "Sinkron %1$d%%", "vi": "Đồng bộ %1$d%%", "th": "ซิงค์ %1$d%%",
        "nl": "Sync %1$d%%", "sv": "Synk %1$d%%", "cs": "Synch. %1$d%%",
    },
    "sound": {
        "en": "Sound", "ru": "Звук", "de": "Ton", "fr": "Son", "es": "Sonido",
        "pt": "Som", "pt-BR": "Som", "it": "Suono", "pl": "Dźwięk", "uk": "Звук",
        "tr": "Ses", "ja": "音", "ko": "소리", "zh-CN": "声音", "zh-TW": "聲音",
        "ar": "الصوت", "hi": "ध्वनि", "id": "Suara", "vi": "Âm thanh", "th": "เสียง",
        "nl": "Geluid", "sv": "Ljud", "cs": "Zvuk",
    },
    "haptics": {
        "en": "Vibration", "ru": "Вибрация", "de": "Vibration", "fr": "Vibration", "es": "Vibración",
        "pt": "Vibração", "pt-BR": "Vibração", "it": "Vibrazione", "pl": "Wibracje", "uk": "Вібрація",
        "tr": "Titreşim", "ja": "振動", "ko": "진동", "zh-CN": "振动", "zh-TW": "震動",
        "ar": "اهتزاز", "hi": "कंपन", "id": "Getaran", "vi": "Rung", "th": "สั่น",
        "nl": "Trilling", "sv": "Vibration", "cs": "Vibrace",
    },
    "reduce_motion": {
        "en": "Reduce motion", "ru": "Меньше анимации", "de": "Weniger Bewegung", "fr": "Moins d’animations",
        "es": "Menos movimiento", "pt": "Menos movimento", "pt-BR": "Menos movimento", "it": "Meno animazioni",
        "pl": "Mniej animacji", "uk": "Менше анімації", "tr": "Hareketi azalt", "ja": "動きを減らす",
        "ko": "움직임 줄이기", "zh-CN": "减少动态效果", "zh-TW": "減少動態效果", "ar": "تقليل الحركة",
        "hi": "कम एनिमेशन", "id": "Kurangi gerakan", "vi": "Giảm chuyển động", "th": "ลดแอนิเมชัน",
        "nl": "Minder beweging", "sv": "Mindre rörelse", "cs": "Méně animací",
    },
    "display_name": {
        "en": "Duty name", "ru": "Имя дежурного", "de": "Dienstname", "fr": "Nom de service",
        "es": "Nombre de turno", "pt": "Nome de serviço", "pt-BR": "Nome do plantão", "it": "Nome di turno",
        "pl": "Imię dyżurnego", "uk": "Ім’я чергового", "tr": "Nöbet adı", "ja": "当直名",
        "ko": "당직 이름", "zh-CN": "值班名", "zh-TW": "值班名", "ar": "اسم المناوبة",
        "hi": "ड्यूटी नाम", "id": "Nama dinas", "vi": "Tên trực", "th": "ชื่อเวร",
        "nl": "Dienstnaam", "sv": "Journamn", "cs": "Jméno služby",
    },
    "language": {
        "en": "Language", "ru": "Язык", "de": "Sprache", "fr": "Langue", "es": "Idioma",
        "pt": "Idioma", "pt-BR": "Idioma", "it": "Lingua", "pl": "Język", "uk": "Мова",
        "tr": "Dil", "ja": "言語", "ko": "언어", "zh-CN": "语言", "zh-TW": "語言",
        "ar": "اللغة", "hi": "भाषा", "id": "Bahasa", "vi": "Ngôn ngữ", "th": "ภาษา",
        "nl": "Taal", "sv": "Språk", "cs": "Jazyk",
    },
    "language_system": {
        "en": "System", "ru": "Системный", "de": "System", "fr": "Système", "es": "Sistema",
        "pt": "Sistema", "pt-BR": "Sistema", "it": "Sistema", "pl": "Systemowy", "uk": "Системна",
        "tr": "Sistem", "ja": "システム", "ko": "시스템", "zh-CN": "跟随系统", "zh-TW": "跟隨系統",
        "ar": "النظام", "hi": "सिस्टम", "id": "Sistem", "vi": "Hệ thống", "th": "ตามระบบ",
        "nl": "Systeem", "sv": "System", "cs": "Systém",
    },
    "reset_progress": {
        "en": "Reset progress", "ru": "Сбросить прогресс", "de": "Fortschritt zurücksetzen", "fr": "Réinitialiser la progression",
        "es": "Borrar progreso", "pt": "Repor progresso", "pt-BR": "Redefinir progresso", "it": "Azzera progressi",
        "pl": "Resetuj postęp", "uk": "Скинути прогрес", "tr": "İlerlemeyi sıfırla", "ja": "進行状況をリセット",
        "ko": "진행 초기화", "zh-CN": "重置进度", "zh-TW": "重設進度", "ar": "إعادة التقدم",
        "hi": "प्रगति रीसेट", "id": "Atur ulang progres", "vi": "Xóa tiến trình", "th": "รีเซ็ตความคืบหน้า",
        "nl": "Voortgang wissen", "sv": "Nollställ framsteg", "cs": "Resetovat postup",
    },
    "reset_progress_confirm": {
        "en": "Clear all caught frames?", "ru": "Очистить все пойманные кадры?",
        "de": "Alle gefangenen Frames löschen?", "fr": "Effacer toutes les trames captées ?",
        "es": "¿Borrar todos los fotogramas capturados?", "pt": "Limpar todos os fotogramas captados?",
        "pt-BR": "Limpar todos os quadros capturados?", "it": "Cancellare tutti i fotogrammi catturati?",
        "pl": "Wyczyścić wszystkie schwytane klatki?", "uk": "Очистити всі зловлених кадри?",
        "tr": "Yakalanan tüm kareler silinsin mi?", "ja": "捕捉したフレームをすべて消去しますか？",
        "ko": "포착한 프레임을 모두 지울까요?", "zh-CN": "清除所有已捕捉的帧？", "zh-TW": "清除所有已捕捉的幀？",
        "ar": "هل تريد مسح كل الإطارات الملتقطة؟", "hi": "सभी पकड़े गए फ़्रेम साफ़ करें?",
        "id": "Hapus semua bingkai yang tertangkap?", "vi": "Xóa tất cả khung đã bắt?",
        "th": "ล้างเฟรมที่จับได้ทั้งหมด?", "nl": "Alle gevangen frames wissen?",
        "sv": "Rensa alla fångade rutor?", "cs": "Vymazat všechny zachycené snímky?",
    },
    "confirm": {
        "en": "Confirm", "ru": "Подтвердить", "de": "Bestätigen", "fr": "Confirmer", "es": "Confirmar",
        "pt": "Confirmar", "pt-BR": "Confirmar", "it": "Conferma", "pl": "Potwierdź", "uk": "Підтвердити",
        "tr": "Onayla", "ja": "確認", "ko": "확인", "zh-CN": "确认", "zh-TW": "確認",
        "ar": "تأكيد", "hi": "पुष्टि", "id": "Konfirmasi", "vi": "Xác nhận", "th": "ยืนยัน",
        "nl": "Bevestigen", "sv": "Bekräfta", "cs": "Potvrdit",
    },
    "cancel": {
        "en": "Cancel", "ru": "Отмена", "de": "Abbrechen", "fr": "Annuler", "es": "Cancelar",
        "pt": "Cancelar", "pt-BR": "Cancelar", "it": "Annulla", "pl": "Anuluj", "uk": "Скасувати",
        "tr": "İptal", "ja": "キャンセル", "ko": "취소", "zh-CN": "取消", "zh-TW": "取消",
        "ar": "إلغاء", "hi": "रद्द", "id": "Batal", "vi": "Hủy", "th": "ยกเลิก",
        "nl": "Annuleren", "sv": "Avbryt", "cs": "Zrušit",
    },
    "epilogue_title": {
        "en": "What do we do with the letter?", "ru": "Что сделать с письмом?",
        "de": "Was tun wir mit dem Brief?", "fr": "Que faire de la lettre ?",
        "es": "¿Qué hacemos con la carta?", "pt": "O que fazemos com a carta?",
        "pt-BR": "O que fazemos com a carta?", "it": "Cosa facciamo della lettera?",
        "pl": "Co zrobimy z listem?", "uk": "Що зробити з листом?",
        "tr": "Mektupla ne yapalım?", "ja": "手紙をどうする？",
        "ko": "편지를 어떻게 할까요?", "zh-CN": "我们如何处置这封信？", "zh-TW": "我們要如何處理這封信？",
        "ar": "ماذا نفعل بالرسالة؟", "hi": "पत्र का क्या करें?",
        "id": "Apa yang kita lakukan dengan surat ini?", "vi": "Ta làm gì với bức thư?",
        "th": "เราจะทำอย่างไรกับจดหมาย?", "nl": "Wat doen we met de brief?",
        "sv": "Vad gör vi med brevet?", "cs": "Co uděláme s dopisem?",
    },
    "epilogue_broadcast": {
        "en": "Send into the air", "ru": "Отправить в эфир", "de": "In den Äther senden", "fr": "Envoyer dans les ondes",
        "es": "Enviar al aire", "pt": "Enviar para o ar", "pt-BR": "Enviar ao ar", "it": "Mandare in onda",
        "pl": "Wyślij w eter", "uk": "Надіслати в ефір", "tr": "Yayına gönder", "ja": "電波に送る",
        "ko": "전파로 보내기", "zh-CN": "送入电波", "zh-TW": "送入電波", "ar": "أرسل إلى الأثير",
        "hi": "प्रसारण में भेजें", "id": "Kirim ke udara", "vi": "Phát lên sóng", "th": "ส่งขึ้นอากาศ",
        "nl": "De ether in sturen", "sv": "Sänd ut i etern", "cs": "Odeslat do éteru",
    },
    "epilogue_archive": {
        "en": "Keep in the archive", "ru": "Сохранить в архив", "de": "Im Archiv behalten", "fr": "Garder aux archives",
        "es": "Guardar en el archivo", "pt": "Guardar no arquivo", "pt-BR": "Guardar no arquivo", "it": "Conserva in archivio",
        "pl": "Zachowaj w archiwum", "uk": "Зберегти в архіві", "tr": "Arşivde tut", "ja": "アーカイブに残す",
        "ko": "아카이브에 보관", "zh-CN": "存入档案", "zh-TW": "存入檔案", "ar": "احفظ في الأرشيف",
        "hi": "संग्रह में रखें", "id": "Simpan di arsip", "vi": "Lưu vào kho", "th": "เก็บในคลัง",
        "nl": "In het archief bewaren", "sv": "Spara i arkivet", "cs": "Uložit do archivu",
    },
    "epilogue_leave": {
        "en": "Leave on the frequency", "ru": "Оставить на частоте", "de": "Auf der Frequenz lassen", "fr": "Laisser sur la fréquence",
        "es": "Dejar en la frecuencia", "pt": "Deixar na frequência", "pt-BR": "Deixar na frequência", "it": "Lascia sulla frequenza",
        "pl": "Zostaw na częstotliwości", "uk": "Залишити на частоті", "tr": "Frekansta bırak", "ja": "周波数に残す",
        "ko": "주파수에 남겨두기", "zh-CN": "留在频率上", "zh-TW": "留在頻率上", "ar": "اتركها على التردد",
        "hi": "आवृत्ति पर छोड़ें", "id": "Tinggalkan di frekuensi", "vi": "Để lại trên tần số", "th": "ทิ้งไว้บนความถี่",
        "nl": "Op de frequentie laten", "sv": "Lämna på frekvensen", "cs": "Nechat na frekvenci",
    },
    "epilogue_done": {
        "en": "Morning", "ru": "Утро", "de": "Morgen", "fr": "Matin", "es": "Mañana",
        "pt": "Manhã", "pt-BR": "Manhã", "it": "Mattina", "pl": "Poranek", "uk": "Ранок",
        "tr": "Sabah", "ja": "朝", "ko": "아침", "zh-CN": "清晨", "zh-TW": "清晨",
        "ar": "الصباح", "hi": "सुबह", "id": "Pagi", "vi": "Buổi sáng", "th": "เช้า",
        "nl": "Ochtend", "sv": "Morgon", "cs": "Ráno",
    },
    "to_menu": {
        "en": "To menu", "ru": "В меню", "de": "Zum Menü", "fr": "Au menu", "es": "Al menú",
        "pt": "Ao menu", "pt-BR": "Ao menu", "it": "Al menu", "pl": "Do menu", "uk": "До меню",
        "tr": "Menüye", "ja": "メニューへ", "ko": "메뉴로", "zh-CN": "返回菜单", "zh-TW": "返回選單",
        "ar": "إلى القائمة", "hi": "मेनू पर", "id": "Ke menu", "vi": "Về menu", "th": "ไปเมนู",
        "nl": "Naar menu", "sv": "Till menyn", "cs": "Do menu",
    },
    "archive_empty": {
        "en": "No fragments yet. Catch a frame.", "ru": "Пока пусто. Поймай кадр сигнала.",
        "de": "Noch keine Fragmente. Fange einen Frame.", "fr": "Pas encore de fragments. Capturez une trame.",
        "es": "Aún no hay fragmentos. Captura un fotograma.", "pt": "Ainda sem fragmentos. Captura um fotograma.",
        "pt-BR": "Ainda sem fragmentos. Capture um quadro.", "it": "Nessun frammento. Cattura un fotogramma.",
        "pl": "Brak fragmentów. Schwytaj klatkę.", "uk": "Поки порожньо. Злови кадр.",
        "tr": "Henüz parça yok. Bir kare yakala.", "ja": "まだ断片はありません。フレームを捕捉して。",
        "ko": "아직 조각이 없습니다. 프레임을 포착하세요.", "zh-CN": "尚无碎片。去捕捉一帧吧。", "zh-TW": "尚無碎片。去捕捉一幀吧。",
        "ar": "لا أجزاء بعد. التقط إطارًا.", "hi": "अभी कोई टुकड़ा नहीं। एक फ़्रेम पकड़ें।",
        "id": "Belum ada fragmen. Tangkap sebuah bingkai.", "vi": "Chưa có mảnh nào. Hãy bắt một khung.",
        "th": "ยังไม่มีชิ้นส่วน จับเฟรมหนึ่ง", "nl": "Nog geen fragmenten. Vang een frame.",
        "sv": "Inga fragment än. Fånga en ruta.", "cs": "Zatím žádné fragmenty. Zachyť snímek.",
    },
    "got_it": {
        "en": "Got it", "ru": "Понятно", "uk": "Зрозуміло", "de": "Verstanden", "fr": "Compris",
    },
    "leave_puzzle": {
        "en": "Leave frame?", "ru": "Сменить кадр?", "uk": "Змінити кадр?", "de": "Frame verlassen?", "fr": "Quitter la trame ?",
    },
    "leave_puzzle_confirm": {
        "en": "The lock will reset.", "ru": "Захват сбросится.", "uk": "Захват скинеться.",
        "de": "Der Lock geht verloren.", "fr": "Le verrou sera perdu.",
    },
    "stay": {"en": "Stay", "ru": "Остаться", "uk": "Залишитися", "de": "Bleiben", "fr": "Rester"},
    "ambiance": {
        "en": "Room tone", "ru": "Атмосфера", "uk": "Атмосфера", "de": "Raumton", "fr": "Ambiance",
    },
    "zone_fish": {"en": "Fishing", "ru": "Рыбаки", "uk": "Рибалки", "de": "Fischerei", "fr": "Pêche"},
    "zone_closed": {"en": "Closed", "ru": "Закрытая", "uk": "Закрита", "de": "Geschlossen", "fr": "Fermée"},
    "zone_official": {"en": "Official", "ru": "Служебная", "uk": "Службова", "de": "Dienst", "fr": "Service"},
    "legend_gap": {"en": "Marker 1 — the gap", "ru": "Маркер 1 — щель", "uk": "Маркер 1 — щілина"},
    "legend_call": {"en": "Marker 2 — callsign", "ru": "Маркер 2 — позывной", "uk": "Маркер 2 — позивний"},
    "act_break_1": {
        "en": "End of noise. Next — a name.",
        "ru": "Конец шума. Дальше — имя.",
        "uk": "Кінець шуму. Далі — ім’я.",
        "de": "Ende des Rauschens. Als Nächstes — ein Name.",
        "fr": "Fin du bruit. Ensuite — un nom.",
    },
    "act_break_2": {
        "en": "The name is assembled. Next — the letter.",
        "ru": "Имя собрано. Дальше — письмо.",
        "uk": "Ім’я зібрано. Далі — лист.",
        "de": "Der Name steht. Als Nächstes — der Brief.",
        "fr": "Le nom est réuni. Ensuite — la lettre.",
    },
    "archive_tab_log": {"en": "Journal", "ru": "Журнал", "uk": "Журнал", "de": "Logbuch", "fr": "Journal"},
    "archive_tab_voice": {"en": "Voice", "ru": "Голос", "uk": "Голос", "de": "Stimme", "fr": "Voix"},
    "archive_tab_photo": {"en": "Photos", "ru": "Снимки", "uk": "Знімки", "de": "Fotos", "fr": "Photos"},
    "archive_tab_letter": {"en": "Letter", "ru": "Письмо", "uk": "Лист", "de": "Brief", "fr": "Lettre"},
    "story_on_english": {
        "en": "Story cards are in English.",
        "ru": "Сюжет на английском.",
        "uk": "Сюжет англійською.",
        "de": "Die Geschichte ist auf Englisch.",
        "fr": "L’histoire est en anglais.",
    },
    "reset_levels": {
        "en": "Reset frames", "ru": "Сбросить кадры", "uk": "Скинути кадри", "de": "Frames zurücksetzen", "fr": "Réinit. les trames",
    },
    "reset_levels_confirm": {
        "en": "Caught frames and the epilogue reset. Archive stays.",
        "ru": "Пойманные кадры и эпилог сбросятся. Архив останется.",
        "uk": "Зловлені кадри й епілог скинуться. Архів лишиться.",
    },
    "reset_all": {
        "en": "Reset everything", "ru": "Сбросить всё", "uk": "Скинути все",
    },
    "marks_title": {"en": "Shift marks", "ru": "Отметки смены", "uk": "Відмітки зміни"},
    "mark_first_word": {"en": "First word", "ru": "Первое слово", "uk": "Перше слово"},
    "mark_name_found": {"en": "Name assembled", "ru": "Имя собрано", "uk": "Ім’я зібрано"},
    "mark_letter_done": {"en": "Letter finished", "ru": "Письмо дочитано", "uk": "Лист дочитано"},
    "mark_three_tones": {"en": "Three mornings", "ru": "Три утра", "uk": "Три ранку"},
    "leave_signed": {
        "en": "Note for %1$s, if you take the next shift.",
        "ru": "Записка для %1$s, если заступишь следующей сменой.",
        "uk": "Записка для %1$s, якщо заступиш наступною зміною.",
    },
    "privacy": {"en": "Privacy", "ru": "Конфиденциальность", "uk": "Приватність", "de": "Datenschutz", "fr": "Confidentialité"},
    "about_version": {"en": "Version %1$s", "ru": "Версия %1$s", "uk": "Версія %1$s"},
    "cd_frame_chip": {
        "en": "Frame %1$d, %2$s, %3$s",
        "ru": "Кадр %1$d, %2$s, %3$s",
    },
    "status_open": {"en": "open", "ru": "открыт", "uk": "відкритий"},
    "status_caught": {"en": "caught", "ru": "пойман", "uk": "зловлен"},
    "status_noise": {"en": "noise", "ru": "шум", "uk": "шум"},
    "letter_parts": {
        "en": "Paragraphs gathered: %1$d",
        "ru": "Собрано абзацев: %1$d",
        "uk": "Зібрано абзаців: %1$d",
    },
    "privacy_body": {
        "en": "Echo Station works offline. Progress stays in on-device DataStore. No network, ads, or analytics.",
        "ru": "Эхо станции работает офлайн. Прогресс хранится в DataStore на устройстве. Нет сети, рекламы и аналитики.",
    },
    "puzzle_wave": {
        "en": "Tune the wave", "ru": "Настрой волну", "de": "Welle abstimmen", "fr": "Régler l’onde",
        "es": "Sintoniza la onda", "pt": "Sintoniza a onda", "pt-BR": "Sintonize a onda", "it": "Sintonizza l’onda",
        "pl": "Dostroj falę", "uk": "Налаштуй хвилю", "tr": "Dalgayı ayarla", "ja": "波を合わせる",
        "ko": "파형을 맞추세요", "zh-CN": "调谐波形", "zh-TW": "調諧波形", "ar": "اضبط الموجة",
        "hi": "तरंग मिलाएँ", "id": "Selaraskan gelombang", "vi": "Chỉnh sóng", "th": "จูนคลื่น",
        "nl": "Stem de golf af", "sv": "Stäm vågen", "cs": "Sladit vlnu",
    },
    "puzzle_cable": {
        "en": "Patch the relays", "ru": "Соедини реле", "de": "Relais verbinden", "fr": "Relier les relais",
        "es": "Conecta los relés", "pt": "Liga os relés", "pt-BR": "Conecte os relés", "it": "Collega i relè",
        "pl": "Połącz przekaźniki", "uk": "З’єднай реле", "tr": "Röleleri bağla", "ja": "リレーをつなぐ",
        "ko": "릴레이를 연결하세요", "zh-CN": "连接继电器", "zh-TW": "連接繼電器", "ar": "وصّل المرحلات",
        "hi": "रिले जोड़ें", "id": "Sambungkan relay", "vi": "Nối rơ-le", "th": "ต่อรีเลย์",
        "nl": "Verbind de relais", "sv": "Koppla reläerna", "cs": "Propoj relé",
    },
    "puzzle_cassette": {
        "en": "Restore the tape", "ru": "Собери кассету", "de": "Band wiederherstellen", "fr": "Restaurer la bande",
        "es": "Restaura la cinta", "pt": "Restaura a fita", "pt-BR": "Restaure a fita", "it": "Ripristina il nastro",
        "pl": "Odtwórz taśmę", "uk": "Збери касету", "tr": "Kaseti onar", "ja": "テープを復元",
        "ko": "테이프를 복원하세요", "zh-CN": "还原磁带", "zh-TW": "還原磁帶", "ar": "استعد الشريط",
        "hi": "टेप बहाल करें", "id": "Pulihkan kaset", "vi": "Khôi phục băng", "th": "กู้เทป",
        "nl": "Herstel de band", "sv": "Återställ bandet", "cs": "Obnov pásku",
    },
    "puzzle_frequency": {
        "en": "Place the callsign", "ru": "Расставь позывной", "de": "Rufzeichen setzen", "fr": "Placer l’indicatif",
        "es": "Coloca el indicativo", "pt": "Coloca o indicativo", "pt-BR": "Coloque o indicativo", "it": "Posiziona il nominativo",
        "pl": "Ustaw znak wywoławczy", "uk": "Розстав позивний", "tr": "Çağrı işaretini yerleştir", "ja": "コールサインを配置",
        "ko": "호출부호를 배치하세요", "zh-CN": "放置呼号", "zh-TW": "放置呼號", "ar": "ضع رمز النداء",
        "hi": "कॉलसाइन रखें", "id": "Tempatkan tanda panggil", "vi": "Đặt tín hiệu gọi", "th": "วางสัญญาณเรียก",
        "nl": "Plaats het roepteken", "sv": "Placera anropssignalen", "cs": "Umísti volací znak",
    },
    "puzzle_multi": {
        "en": "Assemble the letter", "ru": "Собери письмо", "de": "Brief zusammensetzen", "fr": "Assembler la lettre",
        "es": "Arma la carta", "pt": "Monta a carta", "pt-BR": "Monte a carta", "it": "Assembla la lettera",
        "pl": "Złóż list", "uk": "Збери лист", "tr": "Mektubu birleştir", "ja": "手紙を組み立てる",
        "ko": "편지를 조립하세요", "zh-CN": "拼写信件", "zh-TW": "拼寫信件", "ar": "ركّب الرسالة",
        "hi": "पत्र जोड़ें", "id": "Susun surat", "vi": "Ghép bức thư", "th": "ประกอบจดหมาย",
        "nl": "Stel de brief samen", "sv": "Sätt ihop brevet", "cs": "Sestav dopis",
    },
    "frame_n": {
        "en": "Frame %1$d", "ru": "Кадр %1$d", "de": "Frame %1$d", "fr": "Trame %1$d", "es": "Fotograma %1$d",
        "pt": "Fotograma %1$d", "pt-BR": "Quadro %1$d", "it": "Fotogramma %1$d", "pl": "Klatka %1$d", "uk": "Кадр %1$d",
        "tr": "Kare %1$d", "ja": "フレーム %1$d", "ko": "프레임 %1$d", "zh-CN": "第 %1$d 帧", "zh-TW": "第 %1$d 幀",
        "ar": "إطار %1$d", "hi": "फ़्रेम %1$d", "id": "Bingkai %1$d", "vi": "Khung %1$d", "th": "เฟรม %1$d",
        "nl": "Frame %1$d", "sv": "Ruta %1$d", "cs": "Snímek %1$d",
    },
    "multi_step": {
        "en": "Step %1$d / %2$d", "ru": "Шаг %1$d / %2$d", "de": "Schritt %1$d / %2$d", "fr": "Étape %1$d / %2$d",
        "es": "Paso %1$d / %2$d", "pt": "Passo %1$d / %2$d", "pt-BR": "Etapa %1$d / %2$d", "it": "Passo %1$d / %2$d",
        "pl": "Krok %1$d / %2$d", "uk": "Крок %1$d / %2$d", "tr": "Adım %1$d / %2$d", "ja": "ステップ %1$d / %2$d",
        "ko": "단계 %1$d / %2$d", "zh-CN": "步骤 %1$d / %2$d", "zh-TW": "步驟 %1$d / %2$d", "ar": "الخطوة %1$d / %2$d",
        "hi": "चरण %1$d / %2$d", "id": "Langkah %1$d / %2$d", "vi": "Bước %1$d / %2$d", "th": "ขั้น %1$d / %2$d",
        "nl": "Stap %1$d / %2$d", "sv": "Steg %1$d / %2$d", "cs": "Krok %1$d / %2$d",
    },
    "cd_home_cta": {
        "en": "Start night shift", "ru": "Начать ночную смену", "de": "Nachtschicht starten", "fr": "Commencer le quart de nuit",
        "es": "Empezar turno de noche", "pt": "Começar turno da noite", "pt-BR": "Começar turno da noite", "it": "Inizia il turno di notte",
        "pl": "Rozpocznij nocną zmianę", "uk": "Почати нічну зміну", "tr": "Gece vardiyasını başlat", "ja": "夜勤を始める",
        "ko": "야간 근무 시작", "zh-CN": "开始夜班", "zh-TW": "開始夜班", "ar": "ابدأ الوردية الليلية",
        "hi": "रात की शिफ्ट शुरू करें", "id": "Mulai shift malam", "vi": "Bắt đầu ca đêm", "th": "เริ่มกะกลางคืน",
        "nl": "Start nachtdienst", "sv": "Starta nattpass", "cs": "Začít noční směnu",
    },
    "cd_level": {
        "en": "Open frame %1$d", "ru": "Открыть кадр %1$d", "de": "Frame %1$d öffnen", "fr": "Ouvrir la trame %1$d",
        "es": "Abrir fotograma %1$d", "pt": "Abrir fotograma %1$d", "pt-BR": "Abrir quadro %1$d", "it": "Apri fotogramma %1$d",
        "pl": "Otwórz klatkę %1$d", "uk": "Відкрити кадр %1$d", "tr": "Kare %1$d aç", "ja": "フレーム %1$d を開く",
        "ko": "프레임 %1$d 열기", "zh-CN": "打开第 %1$d 帧", "zh-TW": "開啟第 %1$d 幀", "ar": "افتح الإطار %1$d",
        "hi": "फ़्रेम %1$d खोलें", "id": "Buka bingkai %1$d", "vi": "Mở khung %1$d", "th": "เปิดเฟรม %1$d",
        "nl": "Open frame %1$d", "sv": "Öppna ruta %1$d", "cs": "Otevřít snímek %1$d",
    },
    "cd_hint": {
        "en": "Show hint", "ru": "Показать подсказку", "de": "Hinweis zeigen", "fr": "Afficher l’indice",
        "es": "Mostrar pista", "pt": "Mostrar dica", "pt-BR": "Mostrar dica", "it": "Mostra suggerimento",
        "pl": "Pokaż wskazówkę", "uk": "Показати підказку", "tr": "İpucu göster", "ja": "ヒントを表示",
        "ko": "힌트 보기", "zh-CN": "显示提示", "zh-TW": "顯示提示", "ar": "إظهار تلميح",
        "hi": "संकेत दिखाएँ", "id": "Tampilkan petunjuk", "vi": "Hiện gợi ý", "th": "แสดงคำใบ้",
        "nl": "Toon hint", "sv": "Visa tips", "cs": "Zobrazit nápovědu",
    },
    "cd_reset": {
        "en": "Reset puzzle", "ru": "Сбросить пазл", "de": "Rätsel zurücksetzen", "fr": "Réinitialiser l’énigme",
        "es": "Reiniciar el puzzle", "pt": "Repor o puzzle", "pt-BR": "Redefinir o puzzle", "it": "Reimposta il puzzle",
        "pl": "Resetuj puzzle", "uk": "Скинути пазл", "tr": "Bulmacayı sıfırla", "ja": "パズルをリセット",
        "ko": "퍼즐 초기화", "zh-CN": "重置谜题", "zh-TW": "重設謎題", "ar": "إعادة اللغز",
        "hi": "पज़ल रीसेट", "id": "Atur ulang teka-teki", "vi": "Đặt lại câu đố", "th": "รีเซ็ตปริศนา",
        "nl": "Puzzel resetten", "sv": "Återställ pussel", "cs": "Resetovat hádanku",
    },
    "progress_label": {
        "en": "Signal progress", "ru": "Прогресс сигнала", "de": "Signalfortschritt", "fr": "Progression du signal",
        "es": "Progreso de la señal", "pt": "Progresso do sinal", "pt-BR": "Progresso do sinal", "it": "Progresso del segnale",
        "pl": "Postęp sygnału", "uk": "Прогрес сигналу", "tr": "Sinyal ilerlemesi", "ja": "信号の進行",
        "ko": "신호 진행도", "zh-CN": "信号进度", "zh-TW": "訊號進度", "ar": "تقدم الإشارة",
        "hi": "संकेत प्रगति", "id": "Progres sinyal", "vi": "Tiến độ tín hiệu", "th": "ความคืบหน้าสัญญาณ",
        "nl": "Signaalvoortgang", "sv": "Signalframsteg", "cs": "Postup signálu",
    },
    "solved_flash": {
        "en": "Frame locked", "ru": "Кадр пойман", "de": "Frame erfasst", "fr": "Trame captée",
        "es": "Fotograma fijado", "pt": "Fotograma captado", "pt-BR": "Quadro capturado", "it": "Fotogramma bloccato",
        "pl": "Klatka schwytana", "uk": "Кадр зловлено", "tr": "Kare kilitlendi", "ja": "フレーム捕捉",
        "ko": "프레임 고정", "zh-CN": "帧已锁定", "zh-TW": "幀已鎖定", "ar": "تم قفل الإطار",
        "hi": "फ़्रेम लॉक", "id": "Bingkai terkunci", "vi": "Đã khóa khung", "th": "ล็อกเฟรมแล้ว",
        "nl": "Frame vastgelegd", "sv": "Ruta låst", "cs": "Snímek zachycen",
    },
}

# Native language names for picker
LANG_LABELS = {
    "en": "English", "ru": "Русский", "de": "Deutsch", "fr": "Français", "es": "Español",
    "pt": "Português", "pt-BR": "Português (Brasil)", "it": "Italiano", "pl": "Polski", "uk": "Українська",
    "tr": "Türkçe", "ja": "日本語", "ko": "한국어", "zh-CN": "简体中文", "zh-TW": "繁體中文",
    "ar": "العربية", "hi": "हिन्दी", "id": "Bahasa Indonesia", "vi": "Tiếng Việt", "th": "ไทย",
    "nl": "Nederlands", "sv": "Svenska", "cs": "Čeština",
}


def escape(s: str) -> str:
    return xu.escape(s).replace("'", r"\'")


def write_strings() -> None:
    keys = list(UI.keys())
    for folder_suffix, lang in LOCALES.items():
        folder = RES / ("values" if folder_suffix == "" else f"values-{folder_suffix}")
        folder.mkdir(parents=True, exist_ok=True)
        lines = ['<?xml version="1.0" encoding="utf-8"?>', "<resources>"]
        for key in keys:
            text = UI[key].get(lang) or UI[key]["en"]
            lines.append(f'    <string name="{key}">{escape(text)}</string>')
        # language_* labels for picker (native names)
        for code, label in LANG_LABELS.items():
            safe = code.replace("-", "_")
            lines.append(f'    <string name="language_{safe}">{escape(label)}</string>')
        lines.append("</resources>")
        (folder / "strings.xml").write_text("\n".join(lines) + "\n", encoding="utf-8")
        print("wrote", folder)


def localize_story() -> None:
    """Expand existing rewards to multilingual title/body maps."""
    data = json.loads(STORY.read_text(encoding="utf-8"))
    langs = sorted({v for v in LOCALES.values()})

    def pack(en: str, ru: str) -> dict[str, str]:
        # For non en/ru: use English as base; key story lines get ru when available
        out = {lang: en for lang in langs}
        out["en"] = en
        out["ru"] = ru
        # Light localized variants for major EU/Asia (readable, not machine-garbage for short UI-like titles)
        return out

    # Additional title/body dictionaries for common patterns — keep ru/en authentic, others from en
    new_rewards = []
    for r in data["rewards"]:
        title_en = r.get("titleEn") or r.get("titles", {}).get("en", "Log")
        title_ru = r.get("titleRu") or r.get("titles", {}).get("ru", title_en)
        body_en = r.get("bodyEn") or r.get("bodies", {}).get("en", "")
        body_ru = r.get("bodyRu") or r.get("bodies", {}).get("ru", body_en)
        # If already new format, preserve extra langs
        titles = r.get("titles") or pack(title_en, title_ru)
        bodies = r.get("bodies") or pack(body_en, body_ru)
        # Ensure all langs present
        for lang in langs:
            titles.setdefault(lang, titles.get("en", title_en))
            bodies.setdefault(lang, bodies.get("en", body_en))
        titles["ru"] = title_ru
        titles["en"] = title_en
        bodies["ru"] = body_ru
        bodies["en"] = body_en

        # Human translations for epilogues & key titles in major languages
        rid = r["id"]
        if rid.startswith("epilogue_") or r.get("kind") in ("VOICE", "LETTER"):
            titles, bodies = enrich_key_reward(rid, r.get("kind"), titles, bodies)

        new_rewards.append({
            "id": r["id"],
            "kind": r["kind"],
            "titles": titles,
            "bodies": bodies,
            "imageAsset": r.get("imageAsset"),
            "archiveKey": r.get("archiveKey"),
        })

    STORY.write_text(json.dumps({"rewards": new_rewards}, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("story rewards", len(new_rewards), "langs", len(langs))


def enrich_key_reward(rid: str, kind: str | None, titles: dict, bodies: dict) -> tuple[dict, dict]:
    """Add hand-crafted translations for epilogues and letter."""
    table = {
        "epilogue_broadcast": {
            "titles": {
                "de": "Äther", "fr": "Ondes", "es": "Éter", "pt": "Éter", "pt-BR": "Éter",
                "it": "Etere", "pl": "Eter", "uk": "Ефір", "tr": "Yayın", "ja": "電波",
                "ko": "전파", "zh-CN": "电波", "zh-TW": "電波", "ar": "الأثير",
            },
            "bodies": {
                "de": "Morgen. Die Zentrale meldet einen sauberen Träger auf dem alten geschlossenen Kanal — und einmal, darüber, ein fremdes Rufzeichen vierhundert Kilometer entfernt: „Orion-sieben, ich höre Sie.“ Die Schleife ist offen. Der Brief ist weiter gekommen als die Post.",
                "fr": "Matin. La régulation note une porteuse propre sur l’ancien canal fermé — et une fois, par-dessus, un indicatif inconnu à quatre cents kilomètres : « Orion-sept, je vous entends. » La boucle est ouverte. La lettre a dépassé le courrier.",
                "es": "Mañana. Control anota una portadora limpia en el viejo canal cerrado — y una vez, encima, un indicativo extraño a cuatrocientos kilómetros: «Orión-siete, le oigo.» El bucle está abierto. La carta llegó más lejos que el correo.",
                "uk": "Ранок. Диспетчер фіксує чистий несучий на старому закритому каналі — і поверх нього, один раз, чужий позивний за чотириста кілометрів: «Оріон-сім, вас чую». Петлю розімкнуто. Лист пішов далі за пошту.",
                "ja": "朝。管制は古い閉鎖チャンネルにきれいな搬送波を記録する。その上に一度だけ、四百キロ先の見知らぬコールサイン。「オリオン・セブン、聞こえます」。ループは開いた。手紙は郵便より先へ行った。",
                "zh-CN": "清晨。调度记下旧封闭信道上干净的载波——其上一次，四百公里外陌生呼号：「猎户座七号，听到你。」回路已断开。信比邮路走得更远。",
            },
        },
        "epilogue_archive": {
            "titles": {
                "de": "Archiv", "fr": "Archives", "es": "Archivo", "uk": "Архів", "ja": "アーカイブ",
                "zh-CN": "档案", "ko": "아카이브", "tr": "Arşiv", "pl": "Archiwum", "it": "Archivio",
            },
            "bodies": {
                "de": "Der Ordner „Morozova L., 1994“ füllt die Lücke im Regal. Die Turmzeichnung kommt hinein. Der Saal ist still und warm: keine Museumsstille — Hausstille. Der Brief muss nicht mehr im Eisen schreien.",
                "fr": "Le classeur « Morozova L., 1994 » comble le vide sur l’étagère. Le dessin de la tour y entre. La salle est calme et chaude : pas un silence de musée — un silence de maison. La lettre n’a plus à crier dans le fer.",
                "uk": "Папка «Морозова Л., 1994» стає на порожнє місце полиці. Малюнок вежі кладеш усередину. У залі тихо й тепло: не музейна тиша, а домашня. Листу більше не треба кричати в залізі.",
                "ja": "「モロゾワ・L、1994」のファイルが棚の空きを埋める。塔の絵を中へ。ホールは静かで温かい。博物館の静けさではなく、家の静けさ。手紙はもう鉄の中で叫ばなくていい。",
                "zh-CN": "文件夹「莫罗佐娃·L，1994」填上架上的空位。塔的画放进去。大厅安静而温暖：不是博物馆的静，是家里的静。信不必再在铁里喊。",
            },
        },
        "epilogue_leave": {
            "titles": {
                "de": "Auf Frequenz", "fr": "Sur la fréquence", "es": "En frecuencia", "uk": "На частоті",
                "ja": "周波数に", "zh-CN": "留在频率", "ko": "주파수에", "tr": "Frekansta", "pl": "Na częstotliwości",
            },
            "bodies": {
                "de": "Die nächste Schicht findet einen Zettel und einen warmen Abstimmknopf: „Für alle, die zuhören. Ich bin hier, wenn. Mach weiter, wenn du willst.“ Die Frequenz ist nicht geschlossen. Sie wartet nur höflich.",
                "fr": "Le prochain quart trouve un mot et un bouton encore tiède : « Pour qui écoute. Je suis ici si. Termine, si tu veux. » La fréquence n’est pas fermée. Elle attend poliment.",
                "uk": "Наступна зміна знайде на пульті записку й теплу ручку налаштування: «Для тих, хто слухає. Я тут якщо. Добери, якщо захочеш.» Частота не закрита. Вона просто чемно чекає.",
                "ja": "次の当直はメモと温かいチューニングつまみを見つける。「聴く人へ。わたしはここに、もし。続けたければ続けて。」周波数は閉じていない。ただ丁寧に待っている。",
                "zh-CN": "下一班会在台上发现字条和还温的旋钮：「写给聆听的人。我在这里，如果。想续就续。」频率并未关闭。它只是礼貌地等着。",
            },
        },
    }
    if rid in table:
        for k, v in table[rid]["titles"].items():
            titles[k] = v
        for k, v in table[rid]["bodies"].items():
            bodies[k] = v.strip()
    if kind == "LETTER":
        titles.update({
            "de": "Brief an ihren Sohn", "fr": "Lettre à son fils", "es": "Carta a su hijo",
            "uk": "Лист синові", "ja": "息子への手紙", "zh-CN": "写给儿子的信", "ko": "아들에게 보내는 편지",
            "tr": "Oğluna mektup", "pl": "List do syna", "it": "Lettera al figlio",
        })
        bodies.update({
            "uk": (
                "Колю. Я на «Оріоні-7», нічна зміна. Якщо радіо дійде далі за пошту — знай: я крутила ручки й думала про тебе. "
                "Термос охолов, малюнок вежі дивиться з реле, наказ уже підписано. Станцію закриють, а я все одно говорю. "
                "Не бійся тихих частот. Тиша — не порожнеча. Три слова і четверте, які я берегла: я тут, якщо слухаєш. "
                "Якщо ні — я тут усе одно. Добери тишу за мене. Цілую. Мама."
            ),
            "de": (
                "Kolya. Ich bin auf Orion-7, Nachtschicht. Wenn Funk weiter reicht als die Post — wisse: ich habe an den Knöpfen gedreht und an dich gedacht. "
                "Die Thermoskanne ist kalt, die Turmzeichnung wacht über den Relais, der Befehl ist schon unterschrieben. Sie werden die Station schließen, und ich spreche trotzdem. "
                "Fürchte keine stillen Frequenzen. Stille ist keine Leere. Drei Wörter und ein viertes, die ich aufhob: ich bin hier, wenn du zuhörst. "
                "Wenn nicht — ich bin trotzdem hier. Beende die Stille für mich. Kuss. Mama."
            ),
            "fr": (
                "Kolya. Je suis à Orion-7, quart de nuit. Si la radio va plus loin que le courrier — sache que je tournais les boutons en pensant à toi. "
                "Le thermos a refroidi, le dessin de la tour veille sur les relais, l’ordre est déjà signé. Ils fermeront le poste, et je parle encore. "
                "N’aie pas peur des fréquences calmes. Le silence n’est pas le vide. Trois mots et un quatrième que je gardais : je suis ici, si tu écoutes. "
                "Si tu n’écoutes pas — je suis ici quand même. Achève le silence pour moi. Je t’embrasse. Maman."
            ),
        })
    if kind == "VOICE":
        titles.update({
            "de": "Linas Stimme", "fr": "Voix de Lina", "es": "Voz de Lina", "uk": "Голос Ліни",
            "ja": "リナの声", "zh-CN": "莉娜的声音", "ko": "리나의 목소리", "tr": "Lina’nın sesi",
            "pl": "Głos Liny", "it": "Voce di Lina",
        })
    return titles, bodies


def write_locales_config() -> None:
    path = RES / "xml" / "locales_config.xml"
    lines = [
        '<?xml version="1.0" encoding="utf-8"?>',
        '<locale-config xmlns:android="http://schemas.android.com/apk/res/android">',
    ]
    for tag in [
        "en", "ru", "de", "fr", "es", "pt", "pt-BR", "it", "pl", "uk", "tr",
        "ja", "ko", "zh-CN", "zh-TW", "ar", "hi", "id", "vi", "th", "nl", "sv", "cs",
    ]:
        lines.append(f'    <locale android:name="{tag}" />')
    lines.append("</locale-config>")
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print("locales_config ok")


def main() -> None:
    write_strings()
    write_locales_config()
    localize_story()


if __name__ == "__main__":
    main()
