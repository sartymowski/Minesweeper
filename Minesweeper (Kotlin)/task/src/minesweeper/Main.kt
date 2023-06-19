package minesweeper

import java.awt.Point
import kotlin.math.abs
import kotlin.random.Random

enum class CellState { UNMARKED, MARKED }

class Cell(var character: Char) {
    private var cellState = CellState.UNMARKED
    private var explored = false

    override fun toString() =
        if (!explored) {
            if (isMarked())
                "*"
            else
                "."
        } else
            character.toString()

    fun hasMine() = this.character == 'X'
    fun isNumber() = !hasMine() && !isEmpty()
    private fun isMarked() = this.cellState == CellState.MARKED
    fun isEmpty() = this.character == '/'
    fun makeMine() {
        this.character = 'X'
    }

    fun makeFree() {
        explore()
    }

    fun explore() {
        explored = true
    }

    fun isSolved() = (hasMine() && isMarked()) || (isEmpty() && !isMarked()) || isNumber()

    fun changeCellState() {
        if (this.cellState == CellState.MARKED)
            this.cellState = CellState.UNMARKED
        else
            this.cellState = CellState.MARKED
    }
}

class Minesweeper(private val size: Int, private val mines: Int) {
    private val minefield = MutableList(size) { _ -> MutableList(size) { _ -> Cell('/') } }
    private var firstRun = true

    private fun generateMinefield(selectedColumn: Int, selectedRow: Int) {
        repeat(mines) {
            var row = -1
            var column = -1
            do {
                row = Random.nextInt(0, size)
                column = Random.nextInt(0, size)
            } while (minefield[row][column].hasMine() || (abs(selectedRow - row) <= 1 && abs(selectedColumn - column) <= 1))
            minefield[row][column].makeMine()
        }
    }

    private fun MutableList<MutableList<Cell>>.isNotMine(row: Int, column: Int) = !this[row][column].hasMine()

    private fun MutableList<MutableList<Cell>>.setNumber(row: Int, column: Int) {
        if (this[row][column].isEmpty()) {
            this[row][column].character = '1'
        } else {
            this[row][column].character =
                Character.forDigit((Character.getNumericValue(this[row][column].character) + 1), 10)
        }
    }

    fun isSolved() = !firstRun && minefield.all { row -> row.all { column -> column.isSolved() } }

    private fun arePointsAroundPointFree(column: Int, row: Int): Boolean {
        var ret = true
        if (column > 0) {
            ret = ret.and(minefield[row][column - 1].isEmpty())
        }
        if (column < (minefield.size - 1)) {
            ret = ret.and(minefield[row][column + 1].isEmpty())
        }
        if (row > 0) {
            ret = ret.and(minefield[row - 1][column].isEmpty())
        }
        if (row < (minefield.size - 1)) {
            ret = ret.and(minefield[row + 1][column].isEmpty())
        }
        if (row > 0 && column > 0) {
            ret = ret.and(minefield[row - 1][column - 1].isEmpty())
        }
        if (row < (minefield.size - 1) && column < (minefield.size - 1)) {
            ret = ret.and(minefield[row + 1][column + 1].isEmpty())
        }
        if (row > 0 && column < (minefield.size - 1)) {
            ret = ret.and(minefield[row - 1][column + 1].isEmpty())
        }
        if (row < (minefield.size - 1) && column > 0) {
            ret = ret.and(minefield[row + 1][column - 1].isEmpty())
        }

        return ret
    }

    private fun collectPointsAroundPoint(column: Int, row: Int, allPoints: Set<Point>): Set<Point> {
        val points = mutableSetOf<Point>()

        if (row > 0) {
            if (allPoints.none { it == Point(column, row - 1) }) {
                points.add(Point(column, row - 1))
            }
            if (column > 0) {
                if (allPoints.none { it == Point(column - 1, row - 1) }) {
                    points.add(Point(column - 1, row - 1))
                }
            }
            if (column < minefield.size - 1) {
                if (allPoints.none { it == Point(column + 1, row - 1) }) {
                    points.add(Point(column + 1, row - 1))
                }
            }
        }
        if (column > 0) {
            if (allPoints.none { it == Point(column - 1, row) }) {
                points.add(Point(column - 1, row))
            }
            if (row < minefield.size - 1) {
                if (allPoints.none { it == Point(column - 1, row + 1) }) {
                    points.add(Point(column - 1, row + 1))
                }
            }
        }
        if (row < minefield.size - 1) {
            if (allPoints.none { it == Point(column, row + 1) }) {
                points.add(Point(column, row + 1))
            }
            if (column < minefield.size - 1) {
                if (allPoints.none { it == Point(column + 1, row + 1) }) {
                    points.add(Point(column + 1, row + 1))
                }
            }
        }
        if (column < minefield.size - 1) {
            if (allPoints.none { it == Point(column + 1, row) }) {
                points.add(Point(column + 1, row))
            }
        }

        return points
    }

    private fun collectNonMinesPointsAroundPosition(column: Int, row: Int, allPoints: Set<Point>): Set<Point> {
        val points = mutableSetOf<Point>()

        if (column > 0 && !minefield[row][column - 1].hasMine()) {
            if (allPoints.none { it == Point(column - 1, row) }) {
                points.add(Point(column - 1, row))
            }
            if (row > 0 && !minefield[row - 1][column - 1].hasMine()) {
                if (allPoints.none { it == Point(column - 1, row - 1) }) {
                    points.add(Point(column - 1, row - 1))
                }
            }
            if (row < minefield.size - 1 && !minefield[row + 1][column - 1].hasMine()) {
                if (allPoints.none { it == Point(column - 1, row + 1) }) {
                    points.add(Point(column - 1, row + 1))
                }
            }
        }
        if (row > 0 && !minefield[row - 1][column].hasMine()) {
            if (allPoints.none { it == Point(column, row - 1) }) {
                points.add(Point(column, row - 1))
            }
            if (column < minefield.size - 1 && !minefield[row - 1][column + 1].hasMine()) {
                if (allPoints.none { it == Point(column + 1, row - 1) }) {
                    points.add(Point(column + 1, row - 1))
                }
            }
        }
        if (column < minefield.size - 1 && !minefield[row][column + 1].hasMine()) {
            if (allPoints.none { it == Point(column + 1, row) }) {
                points.add(Point(column + 1, row))
            }
            if (row < minefield.size - 1 && !minefield[row + 1][column + 1].hasMine()) {
                if (allPoints.none { it == Point(column + 1, row + 1) }) {
                    points.add(Point(column + 1, row + 1))
                }
            }
        }
        if (row < minefield.size - 1 && !minefield[row + 1][column].hasMine()) {
            if (allPoints.none { it == Point(column, row + 1) }) {
                points.add(Point(column, row + 1))
            }
        }

        return points
    }

    private fun collectAllAvailablePoints(column: Int, row: Int, allPoints: MutableSet<Point>) {
        if (arePointsAroundPointFree(column, row)) {
            val localPoints = collectPointsAroundPoint(column, row, allPoints)
            allPoints.addAll(localPoints)
            localPoints.forEach { collectAllAvailablePoints(it.x, it.y, allPoints) }
        } else {
            val nonMinesPoints = collectNonMinesPointsAroundPosition(column, row, allPoints)
            allPoints.addAll(nonMinesPoints)
            nonMinesPoints.forEach {
                if (minefield[it.y][it.x].isEmpty()) {
                    collectAllAvailablePoints(it.x, it.y, allPoints)
                }
            }
        }
    }

    private fun exploreEmptyCell(column: Int, row: Int) {
        val allPoints = mutableSetOf(Point(column, row))
        collectAllAvailablePoints(column, row, allPoints)
        allPoints.forEach {
            if (minefield[it.y][it.x].isEmpty()) {
                minefield[it.y][it.x].makeFree()
            } else {
                minefield[it.y][it.x].explore()
            }
        }
    }

    fun processCommand(column: Int, row: Int, command: String) {
        if (command == "mine") {
            minefield[row][column].changeCellState()
            printMinefield()
        } else if (command == "free") {
            if (firstRun) {
                firstRun = false
                generateMinefield(column, row)
                fillMinefieldByNumbers()
            }
            if (minefield[row][column].hasMine()) {
                minefield.forEachIndexed { _, row ->
                    row.forEachIndexed { _, col ->
                        if (col.hasMine()) {
                            col.explore()
                        }
                    }
                }
                minefield[row][column].explore()
                printMinefield()
                println("You stepped on a mine and failed!")
            }
            if (minefield[row][column].isEmpty()) {
                exploreEmptyCell(column, row)
                printMinefield()
            } else if (minefield[row][column].isNumber()) {
                exploreEmptyCell(column, row)
                printMinefield()
            } else {
            }
        }
    }

    private fun fillMinefieldByNumbers() {
        minefield.forEachIndexed { indexRow, row ->
            row.forEachIndexed { indexColumn, col ->
                if (col.hasMine()) {
                    if (indexRow > 0) {
                        if (indexColumn > 0 && minefield.isNotMine(indexRow - 1, indexColumn - 1)) {
                            minefield.setNumber(indexRow - 1, indexColumn - 1)
                        }
                        if (minefield.isNotMine(indexRow - 1, indexColumn)) {
                            minefield.setNumber(indexRow - 1, indexColumn)
                        }
                        if (indexColumn < minefield.size - 1 && minefield.isNotMine(indexRow - 1, indexColumn + 1)) {
                            minefield.setNumber(indexRow - 1, indexColumn + 1)
                        }
                    }
                    if (indexColumn > 0 && minefield.isNotMine(indexRow, indexColumn - 1)) {
                        minefield.setNumber(indexRow, indexColumn - 1)
                    }
                    if (indexColumn < minefield.size - 1 && minefield.isNotMine(indexRow, indexColumn + 1)) {
                        minefield.setNumber(indexRow, indexColumn + 1)
                    }
                    if (indexRow < minefield.size - 1) {
                        if (indexColumn > 0 && minefield.isNotMine(indexRow + 1, indexColumn - 1)) {
                            minefield.setNumber(indexRow + 1, indexColumn - 1)
                        }
                        if (minefield.isNotMine(indexRow + 1, indexColumn)) {
                            minefield.setNumber(indexRow + 1, indexColumn)
                        }
                        if (indexColumn < minefield.size - 1 && minefield.isNotMine(indexRow + 1, indexColumn + 1)) {
                            minefield.setNumber(indexRow + 1, indexColumn + 1)
                        }
                    }
                }
            }
        }
    }

    fun printMinefield() {
        println(" |123456789|")
        println("-|---------|")
        repeat(size) { index ->
            println(minefield[index].joinToString(prefix = "${index + 1}|", postfix = "|", separator = ""))
        }
        println("-|---------|")
    }
}

class Game {
    fun play() {
        println("How many mines do you want on the field?")
        val minesweeper = Minesweeper(9, readln().toInt())
        minesweeper.printMinefield()

        do {
            println("Set/unset mine marks or claim a cell as free:")
            val (x, y, command) = readln().split(" ")
            minesweeper.processCommand(x.toInt() - 1, y.toInt() - 1, command)
        } while (!minesweeper.isSolved())
        println("Congratulations! You found all the mines!")
    }
}

fun main() {
    val game = Game()
    game.play()
}
