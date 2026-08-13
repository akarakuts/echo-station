"""Story beats 1–80 + epilogues — unique RU/EN copy for Echo Station."""

# kind, titleRu, titleEn, bodyRu, bodyEn, archiveKey, imageAsset
BEATS: list[tuple] = [
    (
        "LOG", "Первая ночь", "First night",
        "Шип не похож на грозу. Он дышит — ровно, как человек у микрофона, который ещё не решился заговорить.",
        "The hiss is not a storm. It breathes — even, like someone at a mic who has not yet dared to speak.",
        None, None,
    ),
    (
        "LOG", "Слово", "A word",
        "Из статики выныривает одно: «я». Не крик. Просто присутствие, упрямое и тихое.",
        "One word surfaces: “I.” Not a cry. Just presence — stubborn and quiet.",
        None, None,
    ),
    (
        "LOG", "Период", "Period",
        "Каждые сорок семь секунд кадр повторяется. Это не эхо зала. Это петля в железе.",
        "Every forty-seven seconds the frame repeats. Not hall echo. A loop in the iron.",
        None, None,
    ),
    (
        "LOG", "Не погода", "Not weather",
        "Метеосводка чистая. Значит, голос не с неба. Он сидит в самой станции — как чай в остывшем термосе.",
        "The weather sheet is clean. So the voice is not from the sky. It lives in the post — like tea in a cold thermos.",
        None, None,
    ),
    (
        "LOG", "Второе слово", "Second word",
        "«Здесь». Вместе с первым получается обещание: я здесь. Кто-то держит это обещание десятилетиями.",
        "“Here.” With the first word it becomes a promise: I am here. Someone has kept it for decades.",
        None, None,
    ),
    (
        "LOG", "Журнал 1994", "Log 1994",
        "В ящике — журнал ночных смен. Почерк мелкий, женский. На полях звёздочки, как в тетради школьника.",
        "A night-shift journal in the drawer. Small, woman’s hand. Stars in the margins, like a school notebook.",
        None, None,
    ),
    (
        "LOG", "Третье", "The third",
        "Третье слово не «слышу». Оно оборвано: «если…». Фраза не закончена. Петля жуёт недоговорённость.",
        "The third word is not “hear.” It is cut: “if…” The sentence never finished. The loop chews on the unfinished.",
        None, None,
    ),
    (
        "LOG", "Три такта", "Three beats",
        "Я. Здесь. Если. Как стук в дверь, на который никто не открыл — и стук всё равно продолжается.",
        "I. Here. If. A knock no one answered — and the knock continues anyway.",
        None, None,
    ),
    (
        "PHOTO", "Пустой стул", "Empty chair",
        "На снимке стул у пульта, плед сполз на пол. Кто-то ушёл быстро — или думал вернуться через минуту.",
        "A chair by the console, a blanket slipped to the floor. Someone left in a hurry — or meant to be back in a minute.",
        "photo_chair", "photo_console",
    ),
    (
        "LOG", "Намерение", "Intent",
        "Автоподстройка крутит ту же частоту. Это не сбой. Кто-то оставил ручку «на память».",
        "Auto-tune keeps the same frequency. Not a fault. Someone left the knob “as a reminder.”",
        None, None,
    ),
    (
        "LOG", "Красная черта", "Red line",
        "На карте ночи частота зачёркнута красным: ЗАКРЫТА. 12.11.1994. В этот день станцию должны были выключить.",
        "On the night map a frequency is crossed in red: CLOSED. 12 Nov 1994. That was the day they were to shut the post.",
        None, None,
    ),
    (
        "VOICE", "Голос Лины", "Lina’s voice",
        "«Если радио дойдёт дальше почты… Коля, ты только не пугайся шипа. Это я учусь говорить в пустоту.»",
        "“If radio reaches farther than mail… Kolya, don’t be afraid of the hiss. That’s me learning to speak into the empty.”",
        "voice_lina_01", None,
    ),
    (
        "LOG", "Семён", "Semyon",
        "Запись диспетчера Семёна: «Лина опять сидит после смены. Говорит, письмо важное. Я сделал вид, что не слышал.»",
        "Dispatcher Semyon’s note: “Lina stayed after shift again. Said the letter mattered. I pretended not to hear.”",
        None, None,
    ),
    (
        "LOG", "Иней", "Frost",
        "На стекле иней рисует мачту. В журнале: «Коля прислал рисунок башни. Повесила над реле. Пусть смотрит.»",
        "Frost on the glass draws a mast. In the journal: “Kolya sent a drawing of the tower. Hung it over the relays. Let it watch.”",
        None, None,
    ),
    (
        "LOG", "Не страх", "Not fear",
        "Голос усталый, не злой. Он не зовёт на помощь. Он ждёт, как ждут у окна, когда уже поздно для поезда.",
        "The voice is tired, not cruel. It does not call for rescue. It waits the way one waits at a window after the last train.",
        None, None,
    ),
    (
        "LOG", "Обрывок позывного", "Callsign scrap",
        "Между словами — морзянка. Три тире, две точки: набросок «Орион». Она называла станцию по имени, как дом.",
        "Between the words — Morse. A sketch of “Orion.” She named the post the way you name a house.",
        None, None,
    ),
    (
        "LOG", "Озон", "Ozone",
        "В кабельном отсеке пахнет озоном и яблоком. В кружке — засохшая кожура. Домашняя война с казённой ночью.",
        "The cable bay smells of ozone and apple. A dried peel in the mug. A private war against the official night.",
        None, None,
    ),
    (
        "LOG", "Петля", "The loop",
        "Понятно: автомат донастройки гоняет недописанный эфир. Голос не пришелец. Голос — человек, которого выключили на полуслове.",
        "Clear now: auto-tune is replaying an unfinished broadcast. Not an alien. A person switched off mid-sentence.",
        None, None,
    ),
    (
        "PHOTO", "Схема реле", "Relay diagram",
        "На схеме карандашом: «не трогать 7-ю пару — там письмо». Кто-то защищал частоту как дверь в детскую.",
        "Pencil on the diagram: “don’t touch pair 7 — the letter lives there.” Someone guarded a frequency like a child’s door.",
        "photo_relays", "photo_console",
    ),
    (
        "LOG", "Буква Л", "The letter L",
        "На крышке тюнера выцарапано «Л.М.» Ногтем или булавкой. Имя ещё не полное — только зацепка для следующей ночи.",
        "Scratched into the tuner lid: “L.M.” Nail or pin. Not a full name yet — a hook for the next night.",
        None, None,
    ),
    # 21
    (
        "LOG", "Шкафчик", "Locker",
        "Шкафчик с той же царапиной. Внутри шарф, сухая ромашка и конверт без марки. Адрес не дописан.",
        "A locker with the same scratch. Inside: a scarf, a dry chamomile, an envelope with no stamp. The address trails off.",
        None, None,
    ),
    (
        "LOG", "Морозова", "Morozova",
        "Бирка: Лина Морозова, техник ночной смены. 1991–1994. Уволена в связи с консервацией объекта.",
        "The tag: Lina Morozova, night technician. 1991–1994. Released when the site was mothballed.",
        None, None,
    ),
    (
        "LOG", "Не уволена — оборвана", "Not fired — cut",
        "В приказе дата совпадает с красной чертой. Её не выгнали. Ей выключили микрофон вместе со станцией.",
        "The order’s date matches the red line. She was not dismissed. They switched off her microphone with the post.",
        None, None,
    ),
    (
        "LOG", "Приёмник", "The receiver",
        "В дневнике: «Купила Коле маленький приёмник. Написала частоту на бумажке. Почта в том краю хромает.»",
        "Diary: “Bought Kolya a small receiver. Wrote the frequency on a slip. Mail limps in that region.”",
        None, None,
    ),
    (
        "VOICE", "Голос Лины", "Lina’s voice",
        "«Я здесь. Повторяю: я здесь. Если бумажка потеряется — пусть хотя бы эфир знает дорогу.»",
        "“I am here. Repeating: I am here. If the slip is lost — let the air at least know the way.”",
        "voice_lina_02", None,
    ),
    (
        "LOG", "Девять лет", "Nine years",
        "Коле было девять. Он жил у тётки за сотни километров. Лина слала деньги и голос — что успевала.",
        "Kolya was nine. He lived with an aunt hundreds of kilometres away. Lina sent money and a voice — whatever she could catch.",
        None, None,
    ),
    (
        "PHOTO", "Рисунок башни", "Tower drawing",
        "Детский рисунок: мачта, луна, подпись «МАМЕ НА РАБОТУ». Скотч пожелтел, но башня всё ещё стоит над реле.",
        "A child’s drawing: mast, moon, “FOR MUM AT WORK.” The tape yellowed. The tower still stands over the relays.",
        "photo_drawing", "photo_console",
    ),
    (
        "LOG", "Потерянное письмо", "Lost letter",
        "Семён позже дописал: «Её конверт вернули через месяц. Частота так и не дошла. Она уже молчала к тому дню.»",
        "Semyon added later: “Her envelope came back a month on. The frequency never arrived. By then she had gone quiet.”",
        None, None,
    ),
    (
        "LOG", "Поэтому петля", "Why the loop",
        "Она не знала, что бумажка не дошла. Автомат крутил «я здесь если» — на случай, если приёмник всё-таки включат.",
        "She never knew the slip failed. The machine kept “I am here if” — in case the receiver was switched on after all.",
        None, None,
    ),
    (
        "LOG", "Ночь после приказа", "The night after the order",
        "12 ноября она осталась одна. В журнале последняя строка: «Ещё одно предложение. Успею.» Не успела.",
        "On 12 November she stayed alone. Last journal line: “One more sentence. I’ll make it.” She did not.",
        None, None,
    ),
    (
        "LOG", "Кассета «Коле»", "Tape “For Kolya”",
        "В ящике кассета без крышки, фломастером: КОЛЕ. Плёнка склеилась от холода. Её можно собрать по кускам.",
        "A cassette without a lid, markered FOR KOLYA. The tape fused in the cold. It can be rebuilt in pieces.",
        None, None,
    ),
    (
        "LOG", "Шум кухни", "Kitchen noise",
        "На плёнке за голосом — чайник и радиоточка. Она записывала не речь, а дом, которого у него не было рядом.",
        "Behind her voice: a kettle, a wired radio. She was recording a home he did not have beside him.",
        None, None,
    ),
    (
        "VOICE", "Голос Лины", "Lina’s voice",
        "«Позывной Орион-семь, ночь. Сынок, если поймаешь — моргни лампочкой. Я буду смотреть на стекло.»",
        "“Callsign Orion-seven, night. Son, if you catch this — blink a lamp. I’ll be watching the glass.”",
        "voice_lina_03", None,
    ),
    (
        "LOG", "Стекло", "The glass",
        "На окне до сих пор пятно от ладони — низко, как будто ждала сигнала с той стороны леса.",
        "A palm-print still ghosts the window — low, as if she waited for a signal from the far side of the woods.",
        None, None,
    ),
    (
        "LOG", "Не сказка", "Not a fairy tale",
        "Она не верила в чудеса. Верила в короткие волны и в то, что мальчик крутит ручку так же упрямо, как она.",
        "She did not believe in miracles. She believed in shortwave — and that a boy would turn a knob as stubbornly as she did.",
        None, None,
    ),
    (
        "LOG", "Зарплата", "Pay",
        "В конверте расчётный лист и записка: «На ботинки Коле. Остальное — на марки, если снова будут ходить.»",
        "A pay slip and a note: “For Kolya’s boots. The rest for stamps, if they start walking again.”",
        None, None,
    ),
    (
        "PHOTO", "Термос", "Thermos",
        "Термос с вмятиной. На дне сахарный камень. В дневнике: «Сладкий, как ты просил. Я пью за двоих.»",
        "A dented thermos. Sugar crust at the bottom. Diary: “Sweet, the way you asked. I drink for two.”",
        "photo_thermos", "photo_console",
    ),
    (
        "LOG", "Страх тишины", "Fear of quiet",
        "«Боюсь не ночи, — пишет она. — Боюсь, что тишина значит: ты вырос и тебе уже не нужно.»",
        "“I don’t fear the night,” she writes. “I fear quiet meaning you’ve grown and no longer need this.”",
        None, None,
    ),
    (
        "LOG", "Ответ, которого не было", "The answer that wasn’t",
        "Ни одного входящего на закрытой частоте. Петля — это не диалог. Это верность без подтверждения.",
        "No inbound traffic on the closed channel. The loop is not a dialogue. It is loyalty without a receipt.",
        None, None,
    ),
    (
        "VOICE", "Голос Лины", "Lina’s voice",
        "«Сынок, термос остыл, а я всё кручу ручки. Если спишь — ничего. Я побуду здесь ещё немного.»",
        "“Son, the thermos went cold and I keep turning knobs. If you’re asleep — that’s fine. I’ll stay a little longer.”",
        "voice_lina_04", None,
    ),
    (
        "LOG", "Карта ночи", "Night map",
        "Закрытая частота лежит между рыбацким каналом и служебным. Она выбрала щель, куда взрослые не смотрят.",
        "The closed frequency sits between a fishing channel and official traffic. She chose a gap grown-ups don’t watch.",
        None, None,
    ),
    (
        "LOG", "Позывной дома", "House callsign",
        "Для неё «Орион-7» было не кодом. «Семь» — номер общежития, где Коля родился. Позывной — адрес сердца.",
        "“Orion-7” was not a code to her. Seven was the hostel number where Kolya was born. A callsign as a heart’s address.",
        None, None,
    ),
    (
        "LOG", "Консервация", "Mothball",
        "Приказ сухой: оборудование законсервировать, эфир прекратить. Подпись чужая. Её фамилии в листе уже нет.",
        "A dry order: mothball the gear, cease broadcast. Someone else’s signature. Her name already gone from the sheet.",
        None, None,
    ),
    (
        "LOG", "Обход", "Bypass",
        "Она оставила автомат на закрытой паре. Формально эфир мёртв. По факту — одно письмо ещё идёт.",
        "She left the auto-tune on the closed pair. Officially the air is dead. In fact one letter is still travelling.",
        None, None,
    ),
    (
        "LOG", "Не призрак", "Not a ghost",
        "Страшно стало меньше. Осталась печаль и уважение: ты чинишь не станцию. Ты доносишь чужое «если».",
        "The scare thins. What remains is sadness and respect: you are not fixing a post. You are carrying someone else’s “if.”",
        None, None,
    ),
    (
        "LOG", "Тётка", "The aunt",
        "Открытка от тётки, 1995-й, уже после закрытия: «Коля приёмник сломал, чинить некому. Не пишет — стесняется.»",
        "A postcard from the aunt, 1995, after the closure: “Kolya broke the receiver, no one to fix it. He doesn’t write — he’s shy.”",
        None, None,
    ),
    (
        "LOG", "Стеснение", "Shyness",
        "Он не бросил её. Он разбил единственный мост и подумал, что виноват. Два молчания встретились и стали петлёй.",
        "He did not abandon her. He broke the only bridge and thought it was his fault. Two silences met and became a loop.",
        None, None,
    ),
    (
        "VOICE", "Голос Лины", "Lina’s voice",
        "«Станцию законсервируют. Письмо должно остаться в эфире. Пусть хоть железо помнит, если люди забудут.»",
        "“They will mothball the post. The letter must stay on the air. Let the iron remember if people forget.”",
        "voice_lina_05", None,
    ),
    (
        "LOG", "Твоя смена", "Your shift",
        "Музейный пост открыли спустя годы. Ночной волонтёр — это ты. Петля ждала не героя. Ждала дежурного.",
        "The museum post opened years later. The night volunteer is you. The loop did not wait for a hero. It waited for a duty officer.",
        None, None,
    ),
    (
        "LOG", "Имя собрано", "Name assembled",
        "Лина Морозова. Коля. Орион-7. Закрытая частота. Недописанное «если». Теперь можно собирать письмо, а не миф.",
        "Lina Morozova. Kolya. Orion-7. A closed frequency. An unfinished “if.” Now you assemble a letter, not a myth.",
        None, None,
    ),
    # 51 Act 3
    (
        "LOG", "Черновик", "Draft",
        "Первый абзац на обороте схемы: «Сынок. Я на работе, но это тоже про нас. Слушай медленно.»",
        "First paragraph on the back of a diagram: “Son. I am at work, but this is still about us. Listen slowly.”",
        None, None,
    ),
    (
        "LOG", "Про ночь", "About night",
        "«Ночь длинная, зато звёзды близко к мачте. Я рассказываю им, как ты свистел чайник — они не против.»",
        "“The night is long, but the stars sit close to the mast. I tell them how you whistled the kettle — they don’t mind.”",
        None, None,
    ),
    (
        "LOG", "Про ботинки", "About boots",
        "«Деньги на ботинки ушли. Если малы — подложи газету, как дед. Главное, чтобы не хлюпало.»",
        "“The boot money is gone. If they’re small, stuff newspaper like grandad did. Just don’t let them slosh.”",
        None, None,
    ),
    (
        "LOG", "Про стыд", "About shame",
        "«Если сломал приёмник — не стыдись. Сломанные вещи чинят. Молчание чинить труднее, но мы попробуем.»",
        "“If you broke the receiver — don’t be ashamed. Broken things get mended. Silence is harder, but we can try.”",
        None, None,
    ),
    (
        "VOICE", "Голос Лины", "Lina’s voice",
        "«Три слова, которые я твержу сквозь шум: я здесь если… Дальше должно быть “слушаешь”. Я берегла его на конец.»",
        "“Three words I keep saying through the noise: I am here if… Next should be “you’re listening.” I was saving it for the end.”",
        "voice_lina_06", None,
    ),
    (
        "LOG", "Слушаешь", "You’re listening",
        "Слово найдено на обороте кассеты: СЛУШАЕШЬ. Петля наконец может стать предложением, а не обрубком.",
        "The word is on the cassette sleeve: LISTENING. The loop can become a sentence instead of a stump.",
        None, None,
    ),
    (
        "LOG", "Про тишину", "About quiet",
        "«Не бойся тихих частот. Тишина — не пустота. Иногда это просто кто-то дышит на той стороне.»",
        "“Don’t fear quiet frequencies. Quiet is not emptiness. Sometimes it is only someone breathing on the other side.”",
        None, None,
    ),
    (
        "PHOTO", "Ладонь на стекле", "Palm on glass",
        "Фото окна: отпечаток и луна. Подпись фломастером: «моргай, если слышишь». Лампочка над пультом давно перегорела.",
        "A photo of the window: a print and the moon. Marker: “blink if you hear me.” The lamp above the console burned out long ago.",
        "photo_window", "photo_console",
    ),
    (
        "LOG", "Про тётку", "About the aunt",
        "«Тётку слушайся, но своё сердце не отдавай в чужие правила. У нас с тобой своя частота.»",
        "“Mind your aunt, but don’t give your heart to other people’s rules. You and I have our own frequency.”",
        None, None,
    ),
    (
        "LOG", "Про работу", "About the work",
        "«Работа скучная и важная. Как мыть кружку. Как ждать. Я горжусь, что умею ждать красиво.»",
        "“The work is dull and important. Like washing a mug. Like waiting. I am proud I can wait with some grace.”",
        None, None,
    ),
    (
        "LOG", "Про имя", "About the name",
        "«Орион — не бог. Это просто красивое слово над нашей крышей. Можешь звать его домом, если хочешь.»",
        "“Orion is not a god. Just a handsome word above our roof. You may call it home if you like.”",
        None, None,
    ),
    (
        "VOICE", "Голос Лины", "Lina’s voice",
        "«Частота закрытая — но любовь не бывает закрытой. Даже если приказ уже подписан чужой рукой.»",
        "“The frequency is closed — love never is. Even if the order is already signed in someone else’s hand.”",
        "voice_lina_07", None,
    ),
    (
        "LOG", "Про возврат", "About coming back",
        "«Когда вырастешь, не обязан искать станцию. Но если найдёшь шип — это не призрак. Это я оставила свет в прихожей.»",
        "“When you grow up you need not hunt for the post. But if you find the hiss — it is not a ghost. I left a hall light on.”",
        None, None,
    ),
    (
        "LOG", "Чужой дежурный", "A stranger on duty",
        "Она не знала, что письмо дочитает не Коля. Она всё равно писала во второе лицо. Так теплее железу.",
        "She did not know a stranger would finish the letter. She wrote in second person anyway. Warmer for the iron.",
        None, None,
    ),
    (
        "LOG", "Ты не вор", "You are not a thief",
        "Читать чужое письмо — не кража, если петля сама тебя позвала. Ты — почтальон, которого не хватило в девяносто четвёртом.",
        "Reading it is not theft if the loop invited you. You are the postman 1994 never had.",
        None, None,
    ),
    (
        "LOG", "Предпоследнее", "Penultimate",
        "На клочке: «Добери…» Дальше дыра от кнопки. Слово «тишину» или «меня» — не разобрать. Оба подходят.",
        "On a scrap: “Finish…” Then a thumbtack hole. “the quiet” or “me” — both fit.",
        None, None,
    ),
    (
        "LOG", "Коля взрослый", "Kolya grown",
        "Если он жив, ему за сорок. Он мог забыть частоту. Письмо всё равно имеет право быть законченным.",
        "If he lives he is past forty. He may have forgotten the frequency. The letter still has a right to be finished.",
        None, None,
    ),
    (
        "PHOTO", "Полка архива", "Archive shelf",
        "Пустое место на полке шириной с папку. Кто-то когда-то вынул «Л.М.» и не вернул. Сегодня можно вернуть.",
        "A gap on the shelf the width of a binder. Someone once took “L.M.” and never brought it back. Today you can.",
        "photo_shelf", "photo_console",
    ),
    (
        "LOG", "Не спасение мира", "Not saving the world",
        "Никакой катастрофы. Только одно человеческое дело: не оставить мать на полуслове.",
        "No catastrophe. Only one human job: do not leave a mother mid-sentence.",
        None, None,
    ),
    (
        "VOICE", "Голос Лины", "Lina’s voice",
        "«Добери последнее предложение за меня. Не красиво — честно. Честность лучше эха.»",
        "“Finish the last sentence for me. Not prettily — honestly. Honesty is better than echo.”",
        "voice_lina_08", None,
    ),
    (
        "LOG", "Черновик конца", "Draft of the end",
        "Варианты на полях: «я здесь если слушаешь» / «я здесь даже если нет». Она не выбрала. Придётся выбрать тебе — тоном, не сюжетом.",
        "Margin drafts: “I am here if you’re listening” / “I am here even if not.” She didn’t choose. You will — in tone, not plot.",
        None, None,
    ),
    (
        "LOG", "Сборка", "Assembly",
        "Абзацы ложатся по порядку. Голос перестаёт быть обрывком. Становится письмом, которое можно отдать утру.",
        "Paragraphs lock in order. The voice stops being scrap. It becomes a letter you can hand to morning.",
        None, None,
    ),
    (
        "LOG", "Почти утро", "Almost morning",
        "За окном синеет. Петля слабее: автомат впервые не знает, что крутить дальше. Хороший знак.",
        "Blue gathers at the window. The loop weakens: auto-tune no longer knows what to spin. A good sign.",
        None, None,
    ),
    (
        "LOG", "Последний кадр перед письмом", "Last frame before the letter",
        "Осталось склеить финал. Три слова и четвёртое. Я. Здесь. Если. Слушаешь.",
        "Only the ending left to splice. Three words and a fourth. I. Here. If. You’re listening.",
        None, None,
    ),
    (
        "LOG", "Шаг волны", "Wave step",
        "Волна легла. В наушниках — её дыхание без шипа. Как будто кто-то сел рядом и замолчал уже спокойно.",
        "The wave settles. In the phones — her breath without static. As if someone sat down and went quiet at last, at peace.",
        None, None,
    ),
    (
        "LOG", "Шаг реле", "Relay step",
        "Седьмая пара щёлкнула. Та самая, «не трогать». Теперь её можно трогать: письмо выходит из укрытия.",
        "Pair seven clicks. The one marked “don’t touch.” It may be touched now: the letter is leaving cover.",
        None, None,
    ),
    (
        "LOG", "Шаг ленты", "Tape step",
        "Кассета собрана. На хвосте плёнки фломастером: «конец не записывала — запишешь ты».",
        "The tape is whole. On the leader, in marker: “I didn’t record the end — you will.”",
        None, None,
    ),
    (
        "LOG", "Перед выбором", "Before the choice",
        "Письмо готово. Его можно отдать эфиру, полке или следующей смене. Сюжет один. Разный способ сказать «я здесь».",
        "The letter is ready. Air, shelf, or the next shift. One story. Different ways to say “I am here.”",
        None, None,
    ),
    (
        "LOG", "Коля, если это ты", "Kolya, if it’s you",
        "Если дежурный — он, выросший: письмо всё равно не требует ответа. Только чтобы его дочитали вслух хоть раз.",
        "If the volunteer is him, grown: the letter still asks no reply. Only to be read aloud once.",
        None, None,
    ),
    (
        "LETTER", "Письмо сыну", "Letter to her son",
        (
            "Коля. Я на «Орионе-7», ночная смена. Если радио дойдёт дальше почты — знай: я крутила ручки и думала о тебе. "
            "Термос остыл, рисунок башни смотрит с реле, приказ уже подписан. Станцию закроют, а я всё равно говорю. "
            "Не бойся тихих частот. Тишина — не пустота. Три слова и четвёртое, которые я берегла: я здесь, если слушаешь. "
            "Если нет — я здесь всё равно. Добери тишину за меня. Целую. Мама."
        ),
        (
            "Kolya. I am at Orion-7 on the night shift. If radio reaches farther than mail — know I kept turning knobs and thinking of you. "
            "The thermos went cold, the tower drawing watches from the relays, the order is already signed. They will close the post, and I am still speaking. "
            "Don’t fear quiet frequencies. Quiet is not emptiness. Three words and a fourth I was saving: I am here if you’re listening. "
            "If you’re not — I am here anyway. Finish the quiet for me. Love. Mum."
        ),
        "letter_final", None,
    ),
]

assert len(BEATS) == 80, len(BEATS)

EPILOGUES = [
    {
        "id": "epilogue_broadcast",
        "kind": "LOG",
        "titleRu": "Эфир",
        "titleEn": "Broadcast",
        "bodyRu": (
            "Утро. Диспетчер отмечает чистый несущий на старой закрытой частоте — и поверх него, один раз, "
            "чужой позывной за четыреста километров: «Орион-семь, вас слышу». Петля разомкнута. Письмо пошло дальше почты."
        ),
        "bodyEn": (
            "Morning. Dispatch notes a clean carrier on the old closed channel — and once, over it, "
            "a strange callsign four hundred kilometres out: “Orion-seven, I hear you.” The loop is open. The letter outran the mail."
        ),
        "imageAsset": None,
        "archiveKey": "epilogue_broadcast",
    },
    {
        "id": "epilogue_archive",
        "kind": "LOG",
        "titleRu": "Архив",
        "titleEn": "Archive",
        "bodyRu": (
            "Папка «Морозова Л., 1994» встаёт на пустое место полки. Рисунок башни кладёшь внутрь. "
            "В зале тихо и тепло: не музейная тишина, а домашняя. Письмо больше не должно кричать в железе."
        ),
        "bodyEn": (
            "The binder “Morozova L., 1994” fills the gap on the shelf. The tower drawing goes inside. "
            "The hall is quiet and warm: not museum quiet — household quiet. The letter no longer has to shout in the iron."
        ),
        "imageAsset": None,
        "archiveKey": "epilogue_archive",
    },
    {
        "id": "epilogue_leave",
        "kind": "LOG",
        "titleRu": "На частоте",
        "titleEn": "On frequency",
        "bodyRu": (
            "Следующая смена найдёт на пульте записку и тёплую ручку настройки: «Для тех, кто слушает. "
            "Я здесь если. Добери, если захочешь.» Частота не закрыта. Она просто ждёт вежливо."
        ),
        "bodyEn": (
            "The next shift will find a note and a warm tuning knob: “For whoever listens. "
            "I am here if. Finish it if you want.” The frequency is not closed. It is only waiting politely."
        ),
        "imageAsset": None,
        "archiveKey": "epilogue_leave",
    },
]
