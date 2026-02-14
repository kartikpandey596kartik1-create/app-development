package com.example.game2048

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import coil.compose.AsyncImage
import com.example.game2048.ui.theme.Game2048Theme
import kotlin.math.absoluteValue
import kotlinx.coroutines.delay

enum class AppScreen {
    SPLASH, MENU, SETTINGS, GAME, LEADERBOARD
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
    var selectedBoardSize by remember { mutableStateOf(4) }
    var selectedTargetValue by remember { mutableStateOf(2048) }

    when (currentScreen) {
        AppScreen.SPLASH -> SplashScreen(onNavigate = { currentScreen = AppScreen.MENU })
        AppScreen.MENU -> MenuScreen(
            onPlayGame = { currentScreen = AppScreen.SETTINGS },
            onViewLeaderboard = { currentScreen = AppScreen.LEADERBOARD }
        )
        AppScreen.SETTINGS -> SettingsScreen(
            selectedSize = selectedBoardSize,
            selectedTarget = selectedTargetValue,
            onSizeSelected = { selectedBoardSize = it },
            onTargetSelected = { selectedTargetValue = it },
            onStartGame = { currentScreen = AppScreen.GAME },
            onBack = { currentScreen = AppScreen.MENU }
        )
        AppScreen.GAME -> GameScreen(
            boardSize = selectedBoardSize,
            targetValue = selectedTargetValue,
            onBack = { currentScreen = AppScreen.MENU }
        )
        AppScreen.LEADERBOARD -> LeaderboardScreen(
            onBack = { currentScreen = AppScreen.MENU }
        )
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
fun MenuScreen(onPlayGame: () -> Unit, onViewLeaderboard: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBADA0), Color(0xFF9F8A78))
                )
            ),
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
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF776E65)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Merge tiles to reach your goal!",
                fontSize = 18.sp,
                color = Color(0xFF776E65),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(56.dp))
            Button(
                onClick = onPlayGame,
                modifier = Modifier
                    .height(56.dp)
                    .width(200.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEC483F)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("▶ Play Game", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onViewLeaderboard,
                modifier = Modifier
                    .height(56.dp)
                    .width(200.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF2B179)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🏆 Leaderboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun SettingsScreen(
    selectedSize: Int,
    selectedTarget: Int,
    onSizeSelected: (Int) -> Unit,
    onTargetSelected: (Int) -> Unit,
    onStartGame: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBADA0), Color(0xFF9F8A78))
                )
            )
            .padding(16.dp)
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier
                .height(40.dp)
                .width(100.dp),
            shape = RoundedCornerShape(8.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFFCDC1B4)
            )
        ) {
            Text("← Back", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "⚙ Game Settings",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF776E65)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Board Size Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF2B179), shape = RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Board Size",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "$selectedSize × $selectedSize",
                    fontSize = 28.sp,
                    color = Color(0xFF776E65),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (size in 4..10) {
                        Button(
                            onClick = { onSizeSelected(size) },
                            modifier = Modifier
                                .padding(4.dp)
                                .height(44.dp)
                                .width(44.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = if (selectedSize == size) Color(0xFFEC483F) else Color(0xFFCDC1B4)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(size.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (selectedSize == size) Color.White else Color.Black)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Target Value Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEC483F), shape = RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Target Value",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "$selectedTarget",
                    fontSize = 32.sp,
                    color = Color(0xFFEDC22E),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (target in listOf(2048, 4096, 8192, 16384)) {
                        Button(
                            onClick = { onTargetSelected(target) },
                            modifier = Modifier
                                .padding(6.dp)
                                .fillMaxWidth(0.8f)
                                .height(48.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = if (selectedTarget == target) Color(0xFF776E65) else Color(0xFFCDC1B4)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(target.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (selectedTarget == target) Color.White else Color.Black)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onStartGame,
            modifier = Modifier
                .height(56.dp)
                .width(200.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEDC22E)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("🎮 Start Game", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun LeaderboardScreen(onBack: () -> Unit) {
    var selectedSize by remember { mutableStateOf(4) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBADA0), Color(0xFF9F8A78))
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .height(40.dp)
                    .width(100.dp),
                shape = RoundedCornerShape(8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFCDC1B4)
                )
            ) {
                Text("← Back", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            
            Text(
                text = "🏆 Leaderboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF776E65)
            )
            
            Spacer(modifier = Modifier.width(100.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Board size selector
        Text(
            text = "Select Board Size",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF776E65)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for (size in 4..10) {
                Button(
                    onClick = { selectedSize = size },
                    modifier = Modifier
                        .padding(4.dp)
                        .height(40.dp)
                        .width(40.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (selectedSize == size) Color(0xFFEC483F) else Color(0xFFCDC1B4)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(size.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedSize == size) Color.White else Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Leaderboard entries
        val entries = LeaderboardManager.getLeaderboard(selectedSize)
        
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2B179), shape = RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📊 No scores yet for $selectedSize×$selectedSize",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(entries.size) { index ->
                    val entry = entries[index]
                    val medalEmoji = when (index) {
                        0 -> "🥇"
                        1 -> "🥈"
                        2 -> "🥉"
                        else -> "  "
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                color = when (index) {
                                    0 -> Color(0xFFEDC22E).copy(alpha = 0.3f)
                                    1 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                                    2 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                                    else -> Color(0xFFF2B179).copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(2.dp, when (index) {
                                0 -> Color(0xFFEDC22E)
                                1 -> Color(0xFFC0C0C0)
                                2 -> Color(0xFFCD7F32)
                                else -> Color(0xFFF2B179)
                            }, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rank with medal
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(50.dp)
                            ) {
                                Text(
                                    text = medalEmoji,
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = "#${index + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF776E65)
                                )
                            }
                            
                            // Player info
                            Column(modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)) {
                                Text(
                                    text = entry.playerName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF776E65)
                                )
                                Row {
                                    Text(
                                        text = "Score: ${entry.score}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF9F8A78),
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Text(
                                        text = "Target: ${entry.targetValue}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF9F8A78)
                                    )
                                }
                            }
                            
                            // Time
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF2B179), shape = RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "⏱",
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${entry.time / 1000}s",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameScreen(
    boardSize: Int = 4,
    targetValue: Int = 2048,
    onBack: () -> Unit = {}
) {
    val game = remember { Game2048(boardSize, targetValue) }
    var gameState by remember { mutableStateOf(game.getGameState()) }
    var canMove by remember { mutableStateOf(true) }
    var gameStartTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var elapsedTime by remember { mutableStateOf(0L) }

    // Update elapsed time
    LaunchedEffect(gameState.gameOver || gameState.won) {
        while (!gameState.gameOver && !gameState.won) {
            delay(1000)
            elapsedTime = System.currentTimeMillis() - gameStartTime
        }
    }

    LaunchedEffect(Unit) {
        game.newGame()
        gameState = game.getGameState()
        gameStartTime = System.currentTimeMillis()
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
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBADA0), Color(0xFFA9998C))
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Top bar with back button, score, and time
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (gameState.won || gameState.gameOver) {
                        onBack()
                    }
                },
                modifier = Modifier
                    .height(40.dp)
                    .width(90.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFCDC1B4)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("← Back", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            
            // Score Card
            Box(
                modifier = Modifier
                    .background(Color(0xFFEC483F), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Score",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = gameState.score.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEDC22E)
                    )
                }
            }
            
            // Time Card
            Box(
                modifier = Modifier
                    .background(Color(0xFFF2B179), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Time",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${elapsedTime / 1000}s",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF776E65)
                    )
                }
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
                boardSize = boardSize,
                onMove = { direction ->
                    if (canMove && !gameState.gameOver && !gameState.won) {
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
                .padding(16.dp)
        ) {
            GameStatus(won = gameState.won && !gameState.gameOver, gameOver = gameState.gameOver)

            if (gameState.won || gameState.gameOver) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        elapsedTime = 0L
                        gameStartTime = System.currentTimeMillis()
                        game.newGame()
                        gameState = game.getGameState()
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(0.7f),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF2B179)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🔄 New Game", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                
                if (gameState.won && !gameState.gameOver) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            // Add to leaderboard
                            LeaderboardManager.addEntry(
                                LeaderboardEntry(
                                    playerName = "Player",
                                    score = gameState.score,
                                    time = elapsedTime,
                                    boardSize = boardSize,
                                    targetValue = targetValue
                                )
                            )
                            onBack()
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth(0.7f),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEDC22E)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("🏆 Save to Leaderboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        elapsedTime = 0L
                        gameStartTime = System.currentTimeMillis()
                        game.newGame()
                        gameState = game.getGameState()
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(0.7f),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF2B179)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("🔄 New Game", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

@Composable
fun GameBoard(
    board: Array<IntArray>,
    boardSize: Int = 4,
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
            repeat(boardSize) { i ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    repeat(boardSize) { j ->
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
    val scale = remember { androidx.compose.animation.core.Animatable(1f) }
    
    LaunchedEffect(value) {
        if (value > 0) {
            scale.animateTo(1.1f, animationSpec = androidx.compose.animation.core.tween(150))
            scale.animateTo(1f, animationSpec = androidx.compose.animation.core.tween(150))
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .background(
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(colors.background, colors.background.copy(alpha = 0.8f))
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .aspectRatio(1f)
            .graphicsLayer(scaleX = scale.value, scaleY = scale.value),
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
                maxLines = 1,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun GameStatus(won: Boolean, gameOver: Boolean) {
    if (won) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEDC22E), shape = RoundedCornerShape(10.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎉 You Win! 🎉",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF776E65),
                textAlign = TextAlign.Center
            )
        }
    }

    if (gameOver) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEC483F), shape = RoundedCornerShape(10.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "💔 Game Over!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
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
        4096 -> TileColors(Color(0xFFe74c3c), Color(0xFFf9f6f2))
        8192 -> TileColors(Color(0xFF9b59b6), Color(0xFFf9f6f2))
        16384 -> TileColors(Color(0xFF2c3e50), Color(0xFFf9f6f2))
        else -> TileColors(Color(0xFF1a1a1a), Color(0xFFF9F6F2))
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    Game2048Theme {
        GameScreen()
    }
}
