package com.example.game2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game2048.ui.theme.Game2048Theme
import kotlin.math.absoluteValue
import kotlinx.coroutines.delay

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
                    GameScreen()
                }
            }
        }
    }
}

@Composable
fun GameScreen() {
    val game = remember { Game2048() }
    var gameState by remember { mutableStateOf(game.getGameState()) }
    var canMove by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        game.newGame()
        gameState = game.getGameState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBADA0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Header
        GameHeader(score = gameState.score)

        // Game Board
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
                    
                    // Schedule re-enabling moves
                    coroutineScope.launch {
                        delay(150)
                        canMove = true
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Game Status
        GameStatus(won = gameState.won && !gameState.gameOver, gameOver = gameState.gameOver)

        Spacer(modifier = Modifier.height(16.dp))

        // New Game Button
        Button(
            onClick = {
                game.newGame()
                gameState = game.getGameState()
            },
            modifier = Modifier
                .height(50.dp)
                .width(120.dp)
        ) {
            Text("New Game", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instructions
        Text(
            text = "Swipe to move tiles\nCombine tiles to reach 2048!",
            fontSize = 12.sp,
            color = Color(0xFF776E65),
            textAlign = TextAlign.Center
        )
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
    var moveTriggered by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .size(300.dp)
            .background(Color(0xFFBBADA0))
            .padding(8.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        moveTriggered = false
                    },
                    onDragEnd = {
                        moveTriggered = false
                    }
                ) { change, dragAmount ->
                    change.consume()
                    val (dx, dy) = dragAmount
                    val threshold = 100f
                    
                    // Only process once per gesture, when threshold is first exceeded
                    if (!moveTriggered) {
                        when {
                            dx.absoluteValue > threshold && dx.absoluteValue > dy.absoluteValue -> {
                                moveTriggered = true
                                if (dx > 0) onMove(Direction.RIGHT)
                                else onMove(Direction.LEFT)
                            }
                            dy.absoluteValue > threshold && dy.absoluteValue > dx.absoluteValue -> {
                                moveTriggered = true
                                if (dy > 0) onMove(Direction.DOWN)
                                else onMove(Direction.UP)
                            }
                        }
                    }
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
                                .padding(4.dp),
                            key = "$i-$j"
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

    Box(
        modifier = modifier
            .background(colors.background)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        if (value > 0) {
            Text(
                text = value.toString(),
                fontSize = when {
                    value >= 1000 -> 32.sp
                    else -> 40.sp
                },
                fontWeight = FontWeight.Bold,
                color = colors.text,
                textAlign = TextAlign.Center
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
