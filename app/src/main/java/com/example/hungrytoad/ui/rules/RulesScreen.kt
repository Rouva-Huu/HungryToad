package com.example.hungrytoad.ui.rules

import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun RulesScreen() {
    val htmlContent = """
        <html>
            <head>
                <style>
                    body {
                        color: #2E7D32;
                        line-height: 1.6;
                        padding: 16px;
                        background-color: #F2F2EF;
                    }
                    h1 { color: #2E7D32; text-align: center; }
                    h2 { color: #4CAF50; border-bottom: 2px solid #4CAF50; padding-bottom: 5px; }
                    .icon { width: 24px; height: 24px; vertical-align: middle; margin-right: 8px; }
                    .points { font-weight: bold; color: #4CAF50; }
                    .bonus-item { margin: 10px 0; }
                    table { width: 100%; border-collapse: collapse; margin: 15px 0; }
                    td { padding: 8px; border: 1px solid #4CAF50; }
                </style>
            </head>
            <body>
                <h1>🐸 Правила игры 🐸 <br>"Голодная жаба"</h1>
                
                <h2>🎯 Цель игры</h2>
                <p>Управляйте жабой и ловите падающую еду, избегая бомб. Набирайте как можно больше очков!</p>
                
                <h2>🎮 Управление</h2>
                <div class="bonus-item">
                <strong>Касание экрана</strong> - движение жабы в указанную точку
                </div>
                <div class="bonus-item">
                    <strong>Свайп влево/вправо</strong> - быстрое перемещение
                </div>
                
                <h2>⭐ Система очков</h2>
                <div class="bonus-item">🐞 Божья коровка: +30 очков</div>
                <div class="bonus-item">🐛 Гусеница: +25 очков</div>
                <div class="bonus-item">🐜 Муравей: +15 очков</div>
                <div class="bonus-item">🦋 Бабочка: +30 очков</div>
                <div class="bonus-item">🦟 Комар: +10 очков</div>
                <div class="bonus-item">🪲 Жук: +20 очков</div>
                <div class="bonus-item">💣 Бомба: -30 очков</div>
                
                <h2>🎁 Бонусы</h2>
                <div class="bonus-item">
                    🛡️ <strong>Щит</strong> - защита от бомб на 10 секунд
                </div>
                <div class="bonus-item">
                    ⚡️ <strong>Ускорение</strong> - двойные очки на 15 секунд
                </div>
                
                <h2>📈 Уровни сложности</h2>
                <div class="bonus-item"><strong>Очень легко:</strong> Очень медленная скорость, бомб нет</div>
                <div class="bonus-item"><strong>Легко:</strong> Медленная скорость, бомб мало</div>
                <div class="bonus-item"><strong>Нормально:</strong> Средняя скорость, нормальное количество бомб</div>
                <div class="bonus-item"><strong>Сложно:</strong> Высокая скорость, много бомб</div>
                <div class="bonus-item"><strong>Очень сложно:</strong> Максимальная скорость, бомбы падают чаще еды</div>

                <h2>🏆 Достижения</h2>
                <p>Собирайте достижения за различные игровые моменты:</p>
                <div class="bonus-item"><strong>Новичок</strong> - 100 очков</div>
                <div class="bonus-item"><strong>Опытный игрок</strong> - 500 очков</div>
                <div class="bonus-item"><strong>Мастер жабы</strong> - 1000 очков</div>
                <div class="bonus-item"><strong>Неуязвимый</strong> - пройти уровень без попадания бомб</div>
                <div class="bonus-item"><strong>Обжора</strong> - поймать 50 единиц еды подряд</div>
                
                <p style="text-align: center; margin-top: 30px; font-style: italic;">
                    <strong>Удачи в игре!</strong> 🍀
                </p>
            </body>
        </html>
    """.trimIndent()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    loadDataWithBaseURL(
                        "file:///android_res/drawable/",
                        htmlContent,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        )
    }
}