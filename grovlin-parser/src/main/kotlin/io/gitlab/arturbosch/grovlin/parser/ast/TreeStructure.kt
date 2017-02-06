package io.gitlab.arturbosch.grovlin.parser.ast

/**
 * @author Artur Bosch
 */

//
// Interfaces
//

interface Node {
	val position: Position?
	fun print(indent: Int = 0): String
}

interface NodeWithName : Node {
	val name: String
	fun printTypeAndName(indent: Int = 0): String = "${times(indent)}${javaClass.simpleName}: $name\n"
}

interface NodeWithStatements : Node {
	val statements: MutableList<Statement>
	fun printStatements(indent: Int = 0): String {
		val times = times(indent)
		return "$times${statements.joinToString("\n$times") { it.print() }}"
	}
}

interface Expression : Node

interface Statement : Node

fun times(times: Int): String {
	val builder = StringBuilder()
	for (i in 1..times) builder.append("\t")
	return builder.toString()
}

//
// Types
//

interface Type {
	fun print(indent: Int = 0): String = javaClass.simpleName
}

object IntType : Type

object DecimalType : Type

//
// File & Program
//

data class GrovlinFile(override val name: String, override val statements: MutableList<Statement>, override val position: Position? = null) : NodeWithStatements, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent) + printStatements(indent + 1)
	}
}

data class Program(override val statements: MutableList<Statement>, override val position: Position? = null) : Statement, NodeWithStatements {
	override fun print(indent: Int): String {
		return "Program:\n" + printStatements(indent + 1)
	}
}

//
// Statements
//

data class TypeDeclaration(override val name: String, override val statements: MutableList<Statement>, override val position: Position? = null) : Statement, NodeWithStatements, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + printStatements(indent + 2)
	}
}

data class MethodDeclaration(override val name: String, override val statements: MutableList<Statement>, override val position: Position? = null) : Statement, NodeWithStatements, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + printStatements(indent + 2)
	}
}

data class LambdaDeclaration(override val name: String, override val statements: MutableList<Statement>, override val position: Position? = null) : Statement, NodeWithStatements, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + printStatements(indent + 2)
	}
}

data class PropertyDeclaration(override val name: String, val value: Expression, override val position: Position? = null) : Statement, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + value.print(indent + 2)
	}
}

data class VarDeclaration(override val name: String, val value: Expression, override val position: Position? = null) : Statement, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + value.print(indent + 2)
	}
}

data class Assignment(override val name: String, val value: Expression, override val position: Position? = null) : Statement, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + value.print(indent + 2)
	}
}

data class Print(val value: Expression, override val position: Position? = null) : Statement {
	override fun print(indent: Int): String {
		return "Print:\n" + value.print(indent + 1)
	}
}


//
// Expressions
//

interface BinaryExpression : Expression {
	val left: Expression
	val right: Expression
	override fun print(indent: Int): String {
		return "${times(indent + 1)}${javaClass.simpleName}\n${left.print(indent + 1)}${right.print(indent + 1)}"
	}
}

data class SumExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class SubtractionExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class MultiplicationExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class DivisionExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class UnaryMinusExpression(val value: Expression, override val position: Position? = null) : Expression {
	override fun print(indent: Int): String {
		return "${javaClass.simpleName}\n${times(indent)}${value.print(indent + 1)}"
	}
}

data class TypeConversion(val value: Expression, val targetType: Type, override val position: Position? = null) : Expression {
	override fun print(indent: Int): String = "${javaClass.simpleName}\n${times(indent)}${value.print(indent + 1)} as ${targetType.print(indent + 1)}"
}

data class VarReference(override val name: String, override val position: Position? = null) : Expression, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1)
	}
}

data class IntLit(val value: String, override val position: Position? = null) : Expression {
	override fun print(indent: Int): String = times(indent + 1) + value
}

data class DecLit(val value: String, override val position: Position? = null) : Expression {
	override fun print(indent: Int): String = times(indent + 1) + value
}