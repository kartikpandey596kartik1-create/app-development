package com.example.game2048

import androidx.compose.runtime.Immutable
import kotlin.random.Random

@Immutable
data class GameState(
    val board: Array<IntArray> = Array(4) { IntArray(4) },
    val score: Int = 0,
    val gameOver: Boolean = false,
    val won: Boolean = false,
    val moves: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (!board.contentDeepEquals(other.board)) return false
        if (score != other.score) return false
        if (gameOver != other.gameOver) return false
        if (won != other.won) return false
        if (moves != other.moves) return false

        return true
    }

    override fun hashCode(): Int {
        var result = board.contentDeepHashCode()
        result = 31 * result + score
        result = 31 * result + gameOver.hashCode()
        result = 31 * result + won.hashCode()
        result = 31 * result + moves
        return result
    }
}

class Game2048 {
    private var gameState = GameState()

    fun getGameState(): GameState = gameState

    fun newGame() {
        gameState = GameState(board = Array(4) { IntArray(4) })
        addNewTile()
        addNewTile()
    }

    private fun addNewTile() {
        val emptyTiles = mutableListOf<Pair<Int, Int>>()
        for (i in 0..3) {
            for (j in 0..3) {
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

        for (i in 0..3) {
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

        for (i in 0..3) {
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
        val newBoard = Array(4) { IntArray(4) }
        var totalScore = 0
        var moved = false

        for (j in 0..3) {
            val column = IntArray(4) { i -> gameState.board[i][j] }
            val (compactedCol, scoreAdded) = compactRow(column)
            totalScore += scoreAdded
            
            for (i in 0..3) {
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
        val newBoard = Array(4) { IntArray(4) }
        var totalScore = 0
        var moved = false

        for (j in 0..3) {
            val column = IntArray(4) { i -> gameState.board[i][j] }
            val reversed = column.reversedArray()
            val (compactedCol, scoreAdded) = compactRow(reversed)
            totalScore += scoreAdded
            
            val compactedReversed = compactedCol.reversedArray()
            for (i in 0..3) {
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
        val result = IntArray(4)

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

    private fun boardsEqual(board1: Array<IntArray>, board2: Array<IntArray>): Boolean {
        return board1.contentDeepEquals(board2)
    }

    private fun checkGameState() {
        var hasEmpty = false
        var has2048 = false

        for (i in 0..3) {
            for (j in 0..3) {
                if (gameState.board[i][j] == 0) hasEmpty = true
                if (gameState.board[i][j] == 2048) has2048 = true
            }
        }

        if (has2048 && !gameState.won) {
            gameState = gameState.copy(won = true)
        }

        if (!hasEmpty && !canMove()) {
            gameState = gameState.copy(gameOver = true)
        }
    }

    private fun canMove(): Boolean {
        for (i in 0..3) {
            for (j in 0..3) {
                if (gameState.board[i][j] == 0) return true
                if (j < 3 && gameState.board[i][j] == gameState.board[i][j + 1]) return true
                if (i < 3 && gameState.board[i][j] == gameState.board[i + 1][j]) return true
            }
        }
        return false
    }
}
