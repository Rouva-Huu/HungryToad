package com.example.hungrytoad.ui.game

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.hungrytoad.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hungrytoad.R
import com.example.hungrytoad.model.FallingObject
import com.example.hungrytoad.model.FallingObjectInstance
import com.example.hungrytoad.model.GameSettings
import com.example.hungrytoad.model.Player
import com.example.hungrytoad.ui.data.PlayerRepository
import com.example.hungrytoad.utils.SettingsManager
import com.example.hungrytoad.viewmodel.GameViewModel
import com.example.hungrytoad.viewmodel.GameViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import com.example.hungrytoad.AppStateManager
import com.example.hungrytoad.utils.AccelerometerManager
import com.example.hungrytoad.utils.SoundManager

@Composable
fun GameScreen(
    onNavigateToMenu: (String) -> Unit,
    onExitGame: () -> Unit,
    settingsManager: SettingsManager,
    currentPlayer: Player?,
    appStateManager: AppStateManager
) {
    var showMenu by remember { mutableStateOf(false) }
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(settingsManager))
    val timeLeft by gameViewModel.timeLeft.collectAsState()
    val score by gameViewModel.score.collectAsState()
    val isPaused by gameViewModel.isPaused.collectAsState()
    val gameSettings by gameViewModel.gameSettings.collectAsState(initial = GameSettings())
    val gameOver by gameViewModel.gameOver.collectAsState()

    var gravityBonusActive by remember { mutableStateOf(false) }
    var gravityBonusTimeLeft by remember { mutableStateOf(0) }
    var bonusSpawnTimer by remember { mutableStateOf(0) }
    var lastUpdateTime by remember { mutableStateOf(System.currentTimeMillis()) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val accelerometerManager = remember { AccelerometerManager(context) }
    val soundManager = remember { SoundManager(context) }

    val tilt by accelerometerManager.tilt.collectAsState()

    var frogPosition by remember { mutableStateOf(54.dp) }
    var currentFrame by remember { mutableStateOf(1) }
    var isMoving by remember { mutableStateOf(false) }
    var frogDirection by remember { mutableStateOf("right") }

    var fallingObjects by remember { mutableStateOf<List<FallingObjectInstance>>(emptyList()) }
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current

    val animatedPosition by animateDpAsState(
        targetValue = frogPosition,
        animationSpec = tween(durationMillis = 300),
        finishedListener = {
            currentFrame = 1
            isMoving = false
        }
    )

    LaunchedEffect(gravityBonusActive) {
        if (gravityBonusActive) {
            accelerometerManager.start()
        } else {
            accelerometerManager.stop()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            accelerometerManager.stop()
            soundManager.release()
        }
    }

    LaunchedEffect(isPaused, gameOver, bonusSpawnTimer, gravityBonusActive) {
        if (!isPaused && !gameOver && !gravityBonusActive) {
            delay(1000)
            bonusSpawnTimer++

            if (bonusSpawnTimer >= 15) {
                val bonus = createGravityBonus(screenSize, gameSettings.gameSpeed)
                fallingObjects = fallingObjects + bonus
                bonusSpawnTimer = 0
            }
        }
    }

    LaunchedEffect(isPaused, gameOver, gravityBonusActive, gravityBonusTimeLeft) {
        if (!isPaused && !gameOver && gravityBonusActive && gravityBonusTimeLeft > 0) {
            delay(1000)
            gravityBonusTimeLeft--
            if (gravityBonusTimeLeft <= 0) {
                gravityBonusActive = false
            }
        }
    }

    LaunchedEffect(gameOver) {
        if (gameOver && currentPlayer != null && score > 0) {
            scope.launch {
                val repository = PlayerRepository()
                repository.updateBestScore(currentPlayer.id, score)
                appStateManager.updateBestScore(score)
            }
        }
    }

    LaunchedEffect(isMoving) {
        if (isMoving) {
            while (isMoving) {
                currentFrame = (currentFrame % 8) + 1
                delay(50)
            }
        }
    }

    LaunchedEffect(isPaused, gameSettings, gameOver, gravityBonusActive) {
        if (!isPaused && !gameOver) {
            while (true) {
                delay((2000 / gameSettings.gameSpeed).toLong())
                if (!isPaused && !gameOver) {
                    val newObject = createRandomFallingObject(screenSize, gameSettings.gameSpeed)
                    fallingObjects = fallingObjects + newObject
                } else {
                    break
                }
            }
        }
    }

    LaunchedEffect(isPaused, gameOver, gravityBonusActive, tilt) {
        if (!isPaused && !gameOver) {
            while (true) {
                delay(16)
                if (!isPaused && !gameOver) {
                    val frogX = with(density) { animatedPosition.toPx() }
                    val frogY = with(density) { 298.dp.toPx() }
                    val frogWidth = with(density) { 96.dp.toPx() }
                    val frogHeight = with(density) { 96.dp.toPx() }
                    val frogRect = android.graphics.Rect(
                        frogX.toInt(),
                        frogY.toInt(),
                        (frogX + frogWidth).toInt(),
                        (frogY + frogHeight).toInt()
                    )

                    fallingObjects = fallingObjects.mapNotNull { obj ->
                        // Базовое движение - всегда падаем вниз
                        var newY = obj.y + obj.speed
                        var newX = obj.x
                        val newRotation = obj.rotation + obj.type.rotationSpeed / 60f

                        // Применяем гравитационный бонус если активен
                        if (gravityBonusActive && obj.type != FallingObject.GravityBonus) {
                            // ТОЛЬКО когда бонус активен, применяем наклон
                            val (tiltX, tiltY) = tilt

                            // Корректируем оси для горизонтального положения телефона
                            val gravityInfluence = 5.0f // Сила влияния гравитации

                            // Применяем гравитацию - объекты скатываются в сторону наклона
                            newX += tiltY * gravityInfluence
                            // В бонусном режиме отключаем обычное падение вниз
                            newY += tiltX * gravityInfluence

                            // Ограничиваем движение в пределах экрана
                            newX = newX.coerceIn(-40f, screenSize.width.toFloat())
                            newY = newY.coerceIn(-40f, screenSize.height.toFloat())
                        }
                        // Если бонус не активен - объекты просто падают вниз (newY уже увеличен выше)

                        val objSize = with(density) { 40.dp.toPx() }
                        val objRect = android.graphics.Rect(
                            newX.toInt(),
                            newY.toInt(),
                            (newX + objSize).toInt(),
                            (newY + objSize).toInt()
                        )

                        val isColliding = frogRect.intersect(objRect)
                        if (isColliding && !obj.isCollected) {
                            when (obj.type) {
                                is FallingObject.GravityBonus -> {
                                    // Активируем бонус гравитации
                                    gravityBonusActive = true
                                    gravityBonusTimeLeft = 10 // 10 секунд действия
                                    soundManager.playBugScream()
                                    null // Удаляем собранный бонус
                                }
                                is FallingObject.Bomb -> {
                                    gameViewModel.updateScore(obj.type.points)
                                    null
                                }
                                else -> {
                                    gameViewModel.updateScore(obj.type.points)
                                    null
                                }
                            }
                        } else if (newY > screenSize.height || newX < -50f || newX > screenSize.width + 50f) {
                            null // Удаляем объекты за пределами экрана
                        } else {
                            obj.copy(
                                x = newX,
                                y = newY,
                                rotation = newRotation
                            )
                        }
                    }
                } else {
                    break
                }
            }
        }
    }

    LaunchedEffect(gameOver) {
        if (gameOver) {
            gravityBonusActive = false
            gravityBonusTimeLeft = 0
            bonusSpawnTimer = 0
            accelerometerManager.stop()
        }
    }

    LaunchedEffect(isPaused) {
        if (isPaused) {
            accelerometerManager.stop()
        } else if (gravityBonusActive) {
            accelerometerManager.start()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { newSize ->
                screenSize = newSize
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (!isPaused && !isMoving && !gameOver) {
                        val screenWidth = size.width
                        val tapX = offset.x

                        if (tapX < screenWidth / 2) {
                            frogDirection = "left"
                            frogPosition = maxOf(0.dp, frogPosition - 100.dp)
                        } else {
                            frogDirection = "right"
                            frogPosition = minOf(
                                (size.width - 96.dp.toPx()).toDp(),
                                frogPosition + 100.dp
                            )
                        }
                        isMoving = true
                    }
                }
            }
    ) {
        Image(
            painter = painterResource(R.drawable.ic_background),
            contentDescription = "Фон игры",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Image(
            painter = painterResource(getFrogImageResource(currentFrame)),
            contentDescription = "Жаба",
            modifier = Modifier
                .offset(x = animatedPosition, y = 250.dp)
                .size(96.dp)
                .graphicsLayer {
                    rotationY = if (frogDirection == "left") 180f else 0f
                }
        )

        fallingObjects.forEach { obj ->
            Image(
                painter = painterResource(obj.type.drawableRes),
                contentDescription = "Падающий объект",
                modifier = Modifier
                    .offset {
                        IntOffset(
                            obj.x.toInt(),
                            obj.y.toInt()
                        )
                    }
                    .size(44.dp)
                    .graphicsLayer {
                        rotationZ = obj.rotation
                    }
            )
        }

        if (gravityBonusActive) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ГРАВИТАЦИОННЫЙ БОНУС",
                        color = Color.Yellow,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Наклоняйте телефон!",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Осталось: ${gravityBonusTimeLeft}с",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween)
        {
            GameStats(
                timeLeft = timeLeft,
                score = score,
                isPaused = isPaused,
                bestScore = currentPlayer?.bestScore ?: 0,
                bonusActive = gravityBonusActive
            )
            Spacer(modifier = Modifier.fillMaxWidth(0.68f))
            IconButton(
                onClick = { gameViewModel.togglePause() },
            ) {
                Icon(
                    painter = painterResource(if (isPaused) R.drawable.ic_play else R.drawable.ic_pause),
                    contentDescription = if (isPaused) "Продолжить" else "Пауза",
                    tint = DarkGreen,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = {
                    gameViewModel.setPaused(true)
                    showMenu = true
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_menu),
                    contentDescription = "Меню",
                    tint = DarkGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        if (gameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )
            GameOverScreen(
                score = score,
                bestScore = currentPlayer?.bestScore ?: 0,
                onRestart = {
                    gameViewModel.resetGame()
                    fallingObjects = emptyList()
                    gravityBonusActive = false
                    gravityBonusTimeLeft = 0
                    bonusSpawnTimer = 0
                }
            )
        }

        if (showMenu) {
            GameMenuDialog(
                onDismiss = { showMenu = false },
                onMenuSelected = { menuItem ->
                    showMenu = false
                    onNavigateToMenu(menuItem)
                },
                onExitGame = {
                    showMenu = false
                    onExitGame()
                }
            )
        }
    }
}

@Composable
fun GameStats(timeLeft: Int, score: Int, isPaused: Boolean, bestScore: Int, bonusActive: Boolean = false) {
    Text(
        text = "Время: $timeLeft",
        style = MaterialTheme.typography.headlineMedium,
        color = DarkGreen,
        modifier = Modifier.padding(16.dp)
    )
    Text(
        text = "Счет: $score",
        style = MaterialTheme.typography.headlineMedium,
        color = DarkGreen,
        modifier = Modifier.padding(16.dp)
    )
    Text(
        text = "Лучший: $bestScore",
        style = MaterialTheme.typography.headlineMedium,
        color = DarkGreen,
        modifier = Modifier.padding(16.dp)
    )
    if (bonusActive) {
        Text(
            text = "БОНУС!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Red,
            modifier = Modifier.padding(16.dp)
        )
    }
    if (isPaused) {
        Text(
            text = "ПАУЗА",
            style = MaterialTheme.typography.headlineMedium,
            color = DarkGreen,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun GameOverScreen(
    score: Int,
    bestScore: Int,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Игра окончена!",
            style = MaterialTheme.typography.headlineLarge,
            color = LightNude,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Ваш счет: $score",
            style = MaterialTheme.typography.headlineMedium,
            color = LightNude,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = Marsh),
            modifier = Modifier
                .width(200.dp)
                .height(60.dp)
        ) {
            Text(
                text = "Играть снова",
                style = MaterialTheme.typography.headlineMedium,
                color = LightNude
            )
        }
    }
}

@Composable
fun GameMenuDialog(
    onDismiss: () -> Unit,
    onMenuSelected: (String) -> Unit,
    onExitGame: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = Nude)
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    "Меню игры",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(start = 10.dp, bottom = 6.dp),
                    color = DarkGreen
                )

                MenuItem("Аккаунт") { onMenuSelected("account") }
                MenuItem("Настройки") { onMenuSelected("settings") }
                MenuItem("Рекорды") { onMenuSelected("records") }
                MenuItem("Правила игры") { onMenuSelected("rules") }
                MenuItem("Авторы") { onMenuSelected("authors") }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onExitGame,
                    colors = ButtonDefaults.buttonColors(containerColor = Marsh),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Выйти из игры", style = MaterialTheme.typography.bodyMedium,
                        color = LightNude)
                }
            }
        }
    }
}
@Composable
fun MenuItem(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            color = PaleGreen
        )
    }
}
private fun createGravityBonus(screenSize: IntSize, gameSpeed: Float): FallingObjectInstance {
    val x = if (screenSize.width > 40) {
        (0..(screenSize.width - 40)).random().toFloat()
    } else {
        0f
    }
    val y = -40f
    val speed = 2.5f * gameSpeed
    return FallingObjectInstance(
        type = FallingObject.GravityBonus,
        x = x,
        y = y,
        speed = speed
    )
}
private fun createRandomFallingObject(screenSize: IntSize, gameSpeed: Float): FallingObjectInstance {
    val objectTypes = listOf(
        FallingObject.Bomb,
        FallingObject.Beetle,
        FallingObject.Caterpillar,
        FallingObject.Ladybug,
        FallingObject.Mosquito
    )

    val randomType = objectTypes.random()

    val x = if (screenSize.width > 40) {
        (0..(screenSize.width - 40)).random().toFloat()
    } else {
        0f
    }

    val y = -40f

    val speed = 2.5f * gameSpeed

    return FallingObjectInstance(
        type = randomType,
        x = x,
        y = y,
        speed = speed
    )
}
private fun getFrogImageResource(frame: Int): Int {
    return when {
        frame in 1..8 -> when (frame) {
            1 -> R.drawable.ic_frog_1
            2 -> R.drawable.ic_frog_2
            3 -> R.drawable.ic_frog_3
            4 -> R.drawable.ic_frog_4
            5 -> R.drawable.ic_frog_5
            6 -> R.drawable.ic_frog_6
            7 -> R.drawable.ic_frog_7
            8 -> R.drawable.ic_frog_8
            else -> R.drawable.ic_frog_1
        }
        else -> R.drawable.ic_frog_1
    }
}