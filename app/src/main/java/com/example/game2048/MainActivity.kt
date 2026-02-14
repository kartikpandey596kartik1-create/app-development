package com.example.game2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.game2048.ui.theme.Game2048Theme
import kotlin.math.absoluteValue
import kotlinx.coroutines.delay

enum class AppScreen {
    SPLASH, MENU, GAME
}

@Immutable
data class TileColors(val background: Color, val text: Color)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Game2048Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

    when (currentScreen) {
        AppScreen.SPLASH -> SplashScreen(onNavigate = { currentScreen = AppScreen.MENU })
        AppScreen.MENU -> MenuScreen(onNavigate = { currentScreen = AppScreen.GAME })
        AppScreen.GAME -> GameScreen(onBack = { currentScreen = AppScreen.MENU })
    }
}

@Composable
fun SplashScreen(onNavigate: () -> Unit) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000)
        isLoading = false
        onNavigate()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBADA0)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Merge_square_numbers.svg/1024px-Merge_square_numbers.svg.png",
                contentDescription = "2048 Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "2048",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF776E65)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                fontSize = 18.sp,
                color = Color(0xFF9F8A78)
            )
        }
    }
}

@Composable
fun MenuScreen(onNavigate: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBADA0)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Merge_square_numbers.svg/1024px-Merge_square_numbers.svg.png",
                contentDescription = "2048 Logo",
                modifier = Modifier
                    .size(180.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "2048",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF776E65)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Merge tiles to reach 2048!",
                fontSize = 16.sp,
                color = Color(0xFF9F8A78),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onNavigate,
                modifier = Modifier
                    .height(60.dp)
                    .width(150.dp)
            ) {
                Text("Play Game", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun GameScreen(onBack: () -> Unit = {}) {
    val game = remember { Game2048() }
    var gameState by remember { mutableStateOf(game.getGameState()) }
    var canMove by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        game.newGame()
        gameState = game.getGameState()
    }

    // Handle debounce timer
    LaunchedEffect(canMove) {
        if (!canMove) {
            delay(150)
            canMove = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBADA0)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Top bar with back button and score
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.height(40.dp)
            ) {
                Text("← Back", fontSize = 14.sp)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Score",
                    fontSize = 12.sp,
                    color = Color(0xFFBDAC9F)
                )
                Text(
                    text = gameState.score.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(0xFFBBADA0))
                        .padding(8.dp)
                )
            }
        }

        // Game Board - Fullscreen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            GameBoard(
                board = gameState.board,
                onMove = { direction ->
                    if (canMove && !gameState.gameOver) {
                        canMove = false
                        when (direction) {
                            Direction.UP -> game.moveUp()
                            Direction.DOWN -> game.moveDown()
                            Direction.LEFT -> game.moveLeft()
                            Direction.RIGHT -> game.moveRight()
                        }
                        gameState = game.getGameState()
                    }
                }
            )
        }

        // Bottom bar with controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            GameStatus(won = gameState.won && !gameState.gameOver, gameOver = gameState.gameOver)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    game.newGame()
                    gameState = game.getGameState()
                },
                modifier = Modifier.height(45.dp)
            ) {
                Text("New Game", fontSize = 14.sp)
            }
        }
    }
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

@Composable
fun GameHeader(score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "2048",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF776E65)
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Score",
                fontSize = 12.sp,
                color = Color(0xFFBDAC9F)
            )
            Text(
                text = score.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .background(Color(0xFFBBADA0))
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun GameBoard(
    board: Array<IntArray>,
    onMove: (Direction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBADA0))
            .padding(8.dp)
            .aspectRatio(1f)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    var currentPosition = down.position
                    
                    do {
                        val event = awaitPointerEvent()
                        currentPosition = event.changes.firstOrNull()?.position ?: currentPosition
                        event.changes.forEach { it.consume() }
                        
                        // Check if gesture ended
                        if (!event.changes.any { it.pressed }) {
                            val dx = currentPosition.x - down.position.x
                            val dy = currentPosition.y - down.position.y
                            val threshold = 80f
                            
                            // Process gesture only on release
                            when {
                                dx.absoluteValue > threshold && dx.absoluteValue > dy.absoluteValue -> {
                                    if (dx > 0) onMove(Direction.RIGHT)
                                    else onMove(Direction.LEFT)
                                }
                                dy.absoluteValue > threshold && dy.absoluteValue > dx.absoluteValue -> {
                                    if (dy > 0) onMove(Direction.DOWN)
                                    else onMove(Direction.UP)
                                }
                            }
                            break
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            repeat(4) { i ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    repeat(4) { j ->
                        GameTile(
                            value = board[i][j],
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameTile(
    value: Int,
    modifier: Modifier = Modifier,
    key: String = ""
) {
    val colors = remember(value) { getTileColors(value) }

    BoxWithConstraints(
        modifier = modifier
            .background(colors.background)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        if (value > 0) {
            // Scale font size based on tile size
            val baseFontSize = maxWidth / 4
            val fontSize = when {
                value >= 1000 -> baseFontSize * 0.7f
                value >= 100 -> baseFontSize * 0.8f
                else -> baseFontSize * 0.9f
            }
            
            Text(
                text = value.toString(),
                fontSize = fontSize.value.sp,
                fontWeight = FontWeight.Bold,
                color = colors.text,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun GameStatus(won: Boolean, gameOver: Boolean) {
    if (won) {
        Text(
            text = "You Win! Keep Playing",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF776E65),
            textAlign = TextAlign.Center
        )
    }

    if (gameOver) {
        Text(
            text = "Game Over!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEDC22E),
            textAlign = TextAlign.Center
        )
    }
}

@Stable
fun getTileColors(value: Int): TileColors {
    return when (value) {
        0 -> TileColors(Color(0xFFCDC1B4), Color(0xFFCDC1B4))
        2 -> TileColors(Color(0xFFeee4da), Color(0xFF776E65))
        4 -> TileColors(Color(0xFFede0c8), Color(0xFF776E65))
        8 -> TileColors(Color(0xFFf2b179), Color(0xFFf9f6f2))
        16 -> TileColors(Color(0xFFf59563), Color(0xFFf9f6f2))
        32 -> TileColors(Color(0xFFf67c5f), Color(0xFFf9f6f2))
        64 -> TileColors(Color(0xFFf65e3b), Color(0xFFf9f6f2))
        128 -> TileColors(Color(0xFFedcf72), Color(0xFF776E65))
        256 -> TileColors(Color(0xFFedcc61), Color(0xFF776E65))
        512 -> TileColors(Color(0xFFedc850), Color(0xFF776E65))
        1024 -> TileColors(Color(0xFFec483f), Color(0xFFf9f6f2))
        2048 -> TileColors(Color(0xFFedc22e), Color(0xFF776E65))
        else -> TileColors(Color(0xFF3c3c2f), Color(0xFFF9F6F2))
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    Game2048Theme {
        GameScreen()
    }
}
