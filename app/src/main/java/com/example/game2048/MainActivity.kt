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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import com.example.game2048.ui.theme.Game2048Theme
import kotlin.math.absoluteValue
import kotlinx.coroutines.delay

enum class AppScreen {
    SPLASH, MENU, SETTINGS, GAME, LEADERBOARD, PROFILE, CHALLENGES, STATS
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
    var selectedChallenge by remember { mutableStateOf<Challenge?>(null) }

    when (currentScreen) {
        AppScreen.SPLASH -> SplashScreen(onNavigate = { currentScreen = AppScreen.MENU })
        AppScreen.MENU -> MenuScreen(
            onPlayGame = { currentScreen = AppScreen.SETTINGS },
            onViewLeaderboard = { currentScreen = AppScreen.LEADERBOARD },
            onViewProfile = { currentScreen = AppScreen.PROFILE },
            onViewChallenges = { currentScreen = AppScreen.CHALLENGES },
            onViewStats = { currentScreen = AppScreen.STATS }
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
        AppScreen.PROFILE -> ProfileScreen(
            onBack = { currentScreen = AppScreen.MENU }
        )
        AppScreen.CHALLENGES -> ChallengesScreen(
            onStartChallenge = { challenge ->
                selectedChallenge = challenge
                selectedBoardSize = challenge.boardSize
                selectedTargetValue = challenge.targetValue
                currentScreen = AppScreen.GAME
            },
            onBack = { currentScreen = AppScreen.MENU }
        )
        AppScreen.STATS -> StatsScreen(
            onBack = { currentScreen = AppScreen.MENU }
        )
    }
}

@Composable
fun SplashScreen(onNavigate: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000)
        onNavigate()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBADA0)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.splash_bg),
            contentDescription = "Splash Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Optional: Add a semi-transparent overlay if needed
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )
        
        // Loading indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            androidx.compose.material3.CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun MenuScreen(
    onPlayGame: () -> Unit,
    onViewLeaderboard: () -> Unit,
    onViewProfile: () -> Unit,
    onViewChallenges: () -> Unit,
    onViewStats: () -> Unit
) {
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(16.dp)
        ) {
            AsyncImage(
                model = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Merge_square_numbers.svg/1024px-Merge_square_numbers.svg.png",
                contentDescription = "NumMerge Logo",
                modifier = Modifier
                    .size(140.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "NumMerge",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF776E65)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Merge. Combine. Conquer.",
                fontSize = 16.sp,
                color = Color(0xFF776E65),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(40.dp))
            
            // Main buttons
            Button(
                onClick = onPlayGame,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.85f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEC483F)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("▶ Play Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onViewChallenges,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.85f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEDC22E)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("⚡ Challenges", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onViewLeaderboard,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.85f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF2B179)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🏆 Leaderboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onViewProfile,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.85f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF776E65)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("👤 Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onViewStats,
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.85f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9F8A78)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("📊 Statistics", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
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
            // Top status bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBack,
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
                        Text("🏆 Save to NumMerge", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    var playerName by remember { mutableStateOf(StatsManager.getStats().playerName) }
    var isEditing by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBADA0), Color(0xFF9F8A78))
                )
            )
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                text = "👤 My Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF776E65)
            )
            
            Spacer(modifier = Modifier.width(100.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Player name section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEC483F), shape = RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isEditing) {
                    androidx.compose.material3.TextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        label = { Text("Enter your name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(8.dp)),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            StatsManager.setPlayerName(playerName)
                            isEditing = false
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(40.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEDC22E)
                        )
                    ) {
                        Text("Save Name", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                } else {
                    Text(
                        text = "Welcome!",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = playerName.ifEmpty { "Anonymous Player" },
                        fontSize = 28.sp,
                        color = Color(0xFFEDC22E),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { isEditing = true },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(40.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF2B179)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("✏️ Edit Name", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick stats
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF2B179), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Games", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(
                        StatsManager.getStats().totalGames.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFEDC22E), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Wins", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
                    Text(
                        StatsManager.getStats().totalWins.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ChallengesScreen(onStartChallenge: (Challenge) -> Unit, onBack: () -> Unit) {
    val challenges = ChallengeManager.getChallenges()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBADA0), Color(0xFF9F8A78))
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                text = "⚡ Challenges",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF776E65)
            )
            
            Spacer(modifier = Modifier.width(100.dp))
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(challenges.size) { index ->
                val challenge = challenges[index]
                Button(
                    onClick = { onStartChallenge(challenge) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF2B179)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${challenge.icon} ${challenge.name}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = challenge.description,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = "→",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsScreen(onBack: () -> Unit) {
    val stats = StatsManager.getStats()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFBBADA0), Color(0xFF9F8A78))
                )
            )
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                text = "📊 Statistics",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF776E65)
            )
            
            Spacer(modifier = Modifier.width(100.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Main stats grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox(icon = "🎮", label = "Total Games", value = stats.totalGames.toString(), Color(0xFFEC483F))
                StatBox(icon = "🏆", label = "Total Wins", value = stats.totalWins.toString(), Color(0xFFEDC22E))
            }
            
            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox(icon = "⭐", label = "Best Score", value = stats.bestScore.toString(), Color(0xFFF2B179))
                StatBox(icon = "⚡", label = "Best Time", value = "${stats.bestTime / 1000}s", Color(0xFF776E65))
            }
            
            // Win rate
            val winRate = if (stats.totalGames > 0) {
                ((stats.totalWins.toFloat() / stats.totalGames.toFloat()) * 100).toInt()
            } else {
                0
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF9F8A78), shape = RoundedCornerShape(12.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Win Rate", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$winRate%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEDC22E))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatBox(icon: String, label: String, value: String, bgColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
