package com.example.game2048

import androidx.compose.runtime.Immutable
import kotlin.random.Random

@Immutable
data class GameState(
    val board: Array<IntArray> = Array(4) { IntArray(4) },
    val score: Int = 0,
    val gameOver: Boolean = false,
    val won: Boolean = false,
    val moves: Int = 0,
    val boardSize: Int = 4,
    val targetValue: Int = 2048
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (!board.contentDeepEquals(other.board)) return false
        if (gameState.score != other.score) return false
        if (gameState.gameOver != other.gameOver) return false
        if (gameState.won != other.won) return false
        if (gameState.moves != other.moves) return false
        if (gameState.boardSize != other.boardSize) return false
        if (gameState.targetValue != other.targetValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = board.contentDeepHashCode()
        result = 31 * result + score
        result = 31 * result + gameOver.hashCode()
        result = 31 * result + won.hashCode()
        result = 31 * result + moves
        result = 31 * result + boardSize
        result = 31 * result + targetValue
        return result
    }
}

@Immutable
data class LeaderboardEntry(
    val playerName: String = "Player",
    val score: Int,
    val time: Long,
    val boardSize: Int,
    val targetValue: Int,
    val timestamp: Long = System.currentTimeMillis()
)

class Game2048(val boardSize: Int = 4, val targetValue: Int = 2048) {
    private var gameState = GameState(
        board = Array(boardSize) { IntArray(boardSize) },
        boardSize = boardSize,
        targetValue = targetValue
    )

    fun getGameState(): GameState = gameState

    fun newGame() {
        gameState = GameState(
            board = Array(boardSize) { IntArray(boardSize) },
            boardSize = boardSize,
            targetValue = targetValue
        )
        addNewTile()
        addNewTile()
    }

    private fun addNewTile() {
        val emptyTiles = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                if (gameState.board[i][j] == 0) {
                    emptyTiles.add(Pair(i, j))
                }
            }
        }
        if (emptyTiles.isNotEmpty()) {
            val (row, col) = emptyTiles[Random.nextInt(emptyTiles.size)]
            gameState.board[row][col] = if (Random.nextDouble() < 0.9) 2 else 4
        }
    }

    fun moveLeft(): Boolean {
        val newBoard = gameState.board.map { it.copyOf() }.toTypedArray()
        var scoreAdded = 0
        var moved = false

        for (i in 0 until boardSize) {
            val row = newBoard[i]
            val (compactedRow, changeScore) = compactRow(row)
            if (!row.contentEquals(compactedRow)) {
                moved = true
            }
            scoreAdded += changeScore
            newBoard[i] = compactedRow
        }

        if (moved) {
            gameState = gameState.copy(
                board = newBoard,
                score = gameState.score + scoreAdded,
                moves = gameState.moves + 1
            )
            addNewTile()
            checkGameState()
            return true
        }
        return false
    }

    fun moveRight(): Boolean {
        val newBoard = gameState.board.map { it.reversedArray() }.toTypedArray()
        var scoreAdded = 0
        var moved = false

        for (i in 0 until boardSize) {
            val row = newBoard[i]
            val (compactedRow, changeScore) = compactRow(row)
            if (!row.contentEquals(compactedRow)) {
                moved = true
            }
            scoreAdded += changeScore
            newBoard[i] = compactedRow.reversedArray()
        }

        if (moved) {
            gameState = gameState.copy(
                board = newBoard,
                score = gameState.score + scoreAdded,
                moves = gameState.moves + 1
            )
            addNewTile()
            checkGameState()
            return true
        }
        return false
    }

    fun moveUp(): Boolean {
        val newBoard = Array(boardSize) { IntArray(boardSize) }
        var totalScore = 0
        var moved = false

        for (j in 0 until boardSize) {
            val column = IntArray(boardSize) { i -> gameState.board[i][j] }
            val (compactedCol, scoreAdded) = compactRow(column)
            totalScore += scoreAdded
            
            for (i in 0 until boardSize) {
                newBoard[i][j] = compactedCol[i]
            }
            
            if (!column.contentEquals(compactedCol)) {
                moved = true
            }
        }

        if (moved) {
            gameState = gameState.copy(
                board = newBoard,
                score = gameState.score + totalScore,
                moves = gameState.moves + 1
            )
            addNewTile()
            checkGameState()
            return true
        }
        return false
    }

    fun moveDown(): Boolean {
        val newBoard = Array(boardSize) { IntArray(boardSize) }
        var totalScore = 0
        var moved = false

        for (j in 0 until boardSize) {
            val column = IntArray(boardSize) { i -> gameState.board[i][j] }
            val reversed = column.reversedArray()
            val (compactedCol, scoreAdded) = compactRow(reversed)
            totalScore += scoreAdded
            
            val compactedReversed = compactedCol.reversedArray()
            for (i in 0 until boardSize) {
                newBoard[i][j] = compactedReversed[i]
            }
            
            if (!column.contentEquals(compactedReversed)) {
                moved = true
            }
        }

        if (moved) {
            gameState = gameState.copy(
                board = newBoard,
                score = gameState.score + totalScore,
                moves = gameState.moves + 1
            )
            addNewTile()
            checkGameState()
            return true
        }
        return false
    }

    private fun compactRow(row: IntArray): Pair<IntArray, Int> {
        val compacted = row.filter { it != 0 }.toIntArray()
        var score = 0
        val result = IntArray(boardSize)

        var pos = 0
        var i = 0
        while (i < compacted.size) {
            if (i + 1 < compacted.size && compacted[i] == compacted[i + 1]) {
                result[pos] = compacted[i] * 2
                score += compacted[i] * 2
                i += 2
            } else {
                result[pos] = compacted[i]
                i++
            }
            pos++
        }

        return Pair(result, score)
    }

    private fun checkGameState() {
        var hasEmpty = false
        var hasTarget = false

        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                if (gameState.board[i][j] == 0) hasEmpty = true
                if (gameState.board[i][j] == gameState.targetValue) hasTarget = true
            }
        }

        if (hasTarget && !gameState.won) {
            gameState = gameState.copy(won = true)
        }

        if (!hasEmpty && !canMove()) {
            gameState = gameState.copy(gameOver = true)
        }
    }

    private fun canMove(): Boolean {
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                if (gameState.board[i][j] == 0) return true
                if (j < boardSize - 1 && gameState.board[i][j] == gameState.board[i][j + 1]) return true
                if (i < boardSize - 1 && gameState.board[i][j] == gameState.board[i + 1][j]) return true
            }
        }
        return false
    }
}

object LeaderboardManager {
    private val leaderboards = mutableMapOf<Int, MutableList<LeaderboardEntry>>()

    init {
        for (size in 4..10) {
            leaderboards[size] = mutableListOf()
        }
    }

    fun addEntry(entry: LeaderboardEntry) {
        val board = leaderboards[entry.boardSize] ?: mutableListOf()
        board.add(entry)
        board.sortBy { it.time }
        leaderboards[entry.boardSize] = board.take(10).toMutableList()
    }

    fun getLeaderboard(boardSize: Int): List<LeaderboardEntry> {
        return leaderboards[boardSize]?.sortedBy { it.time } ?: emptyList()
    }

    fun getAllLeaderboards(): Map<Int, List<LeaderboardEntry>> {
        return leaderboards.mapValues { it.value.sortedBy { entry -> entry.time } }
    }
}