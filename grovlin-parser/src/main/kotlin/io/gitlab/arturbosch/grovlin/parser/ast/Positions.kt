package io.gitlab.arturbosch.grovlin.parser.ast

/**
 * @author Artur Bosch
 */

data class Point(val line: Int, val column: Int) {

	override fun toString() = "Line $line, Column $column"

	fun offset(lines: List<String>): Int {
		val newLines = this.line - 1
		return lines.subList(0, newLines).foldRight(0) { it, acc -> it.length + acc } + newLines + column
	}

	fun isBefore(other: Point): Boolean = line < other.line || (line == other.line && column < other.column)

	fun isAfter(other: Point): Boolean = line > other.line || (line == other.line && column > other.column)
}

data class Position(val start: Point, val end: Point) {

	init {
		if (end.isBefore(start)) {
			throw IllegalArgumentException("End should follows start")
		}
	}

	fun text(code: String): String {
		val lines = code.split("\n")
		return code.substring(start.offset(lines), end.offset(lines))
	}

	fun length(code: String): Int {
		val lines = code.split("\n")
		return end.offset(lines) - start.offset(lines)
	}
}