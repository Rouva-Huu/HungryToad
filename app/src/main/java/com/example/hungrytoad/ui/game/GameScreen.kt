package com.example.hungrytoad.ui.game

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import com.example.hungrytoad.utils.SettingsManager
import com.example.hungrytoad.viewmodel.GameViewModel
import com.example.hungrytoad.viewmodel.GameViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    onNavigateToMenu: (String) -> Unit,
    onExitGame: () -> Unit,
    settingsManager: SettingsManager
) {
    var showMenu by remember { mutableStateOf(false) }
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(settingsManager))

    val timeLeft by gameViewModel.timeLeft.collectAsState()
    val score by gameViewModel.score.collectAsState()
    val isPaused by gameViewModel.isPaused.collectAsState()
    val gameSettings by gameViewModel.gameSettings.collectAsState(initial = GameSettings())
    val gameOver by gameViewModel.gameOver.collectAsState()

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
    LaunchedEffect(isMoving) {
        if (isMoving) {
            while (isMoving) {
                currentFrame = (currentFrame % 8) + 1
                delay(50)
            }
        }
    }

    LaunchedEffect(isPaused, gameSettings, gameOver) {
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

    LaunchedEffect(isPaused, gameOver) {
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

                        val newY = obj.y + obj.speed
                        val newRotation = obj.rotation + obj.type.rotationSpeed / 60f

                        val objSize = with(density) { 40.dp.toPx() }
                        val objRect = android.graphics.Rect(
                            obj.x.toInt(),
                            newY.toInt(),
                            (obj.x + objSize).toInt(),
                            (newY + objSize).toInt()
                        )

                        val isColliding = frogRect.intersect(objRect)

                        if (isColliding && !obj.isCollected) {
                            when (obj.type) {
                                is FallingObject.Bomb -> {
                                    gameViewModel.updateScore(obj.type.points)
                                    null
                                }
                                else -> {
                                    gameViewModel.updateScore(obj.type.points)
                                    null
                                }
                            }
                        } else if (newY > screenSize.height || newY > with(density) { 300.dp.toPx() }) {
                            null
                        } else {
                            obj.copy(
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween)
        {
            GameStats(
                timeLeft = timeLeft,
                score = score,
                isPaused = isPaused
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
                onRestart = {
                    gameViewModel.resetGame()
                    fallingObjects = emptyList()
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
fun GameStats(timeLeft: Int, score: Int, isPaused: Boolean) {
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
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Меню игры",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(start = 8.dp, bottom = 10.dp),
                    color = DarkGreen
                )

                MenuItem("Аккаунт") { onMenuSelected("account") }
                MenuItem("Настройки") { onMenuSelected("settings") }
                MenuItem("Правила игры") { onMenuSelected("rules") }
                MenuItem("Авторы") { onMenuSelected("authors") }

                Spacer(modifier = Modifier.height(10.dp))

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
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            color = PaleGreen
        )
    }
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

    val speed = 1f * gameSpeed

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