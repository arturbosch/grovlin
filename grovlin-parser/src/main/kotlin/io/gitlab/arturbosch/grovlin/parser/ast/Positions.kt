package io.gitlab.arturbosch.grovlin.parser.ast

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token

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

fun pos(startLine: Int, startColumn: Int, endLine: Int, endColumn: Int) = Position(Point(startLine, startColumn), Point(endLine, endColumn))

// Node extensions

fun Node.isBefore(other: Node): Boolean = position != null && other.position != null && position!!.start.isBefore(other.position!!.start)

fun Node.isAfter(other: Node): Boolean = position != null && other.position != null && position!!.start.isAfter(other.position!!.start)

// Grovlin Parser

fun Token.startPoint() = Point(line, charPositionInLine)

fun Token.endPoint() = Point(line, charPositionInLine + text.length)

fun ParserRuleContext.toPosition(): Position? {
	return Position(start.startPoint(), stop.endPoint())
}