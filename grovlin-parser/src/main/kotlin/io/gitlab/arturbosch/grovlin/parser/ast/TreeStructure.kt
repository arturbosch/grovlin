package io.gitlab.arturbosch.grovlin.parser.ast

/**
 * @author Artur Bosch
 */
interface Node {
	fun print(indent: Int = 0): String
}

interface NodeWithName : Node {
	val name: String
	fun printTypeAndName(indent: Int = 0): String = "${times(indent)}${javaClass.simpleName}: $name\n"
}

fun times(times: Int): String {
	val builder = StringBuilder()
	for (i in 1..times) builder.append("\t")
	return builder.toString()
}

interface NodeWithStatements : Node {
	val statements: MutableList<Statement>
	fun printStatements(indent: Int = 0): String {
		val times = times(indent)
		return "$times${statements.joinToString("\n$times") { it.print() }}"
	}
}

interface Type {
	fun print(indent: Int = 0): String = javaClass.simpleName
}

interface Expression : Node
interface Statement : Node

//
// Types
//

object IntType : Type

object DecimalType : Type

//
// File & Program
//

data class GrovlinFile(override val name: String, override val statements: MutableList<Statement>) : NodeWithStatements, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent) + printStatements(indent + 1)
	}
}

data class Program(override val statements: MutableList<Statement>) : Statement, NodeWithStatements {
	override fun print(indent: Int): String {
		return "Program:\n" + printStatements(indent + 1)
	}
}

//
// Statements
//

data class TypeDeclaration(override val name: String, override val statements: MutableList<Statement>) : Statement, NodeWithStatements, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + printStatements(indent + 2)
	}
}

data class MethodDeclaration(override val name: String, override val statements: MutableList<Statement>) : Statement, NodeWithStatements, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + printStatements(indent + 2)
	}
}

data class LambdaDeclaration(override val name: String, override val statements: MutableList<Statement>) : Statement, NodeWithStatements, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + printStatements(indent + 2)
	}
}

data class PropertyDeclaration(override val name: String, val value: Expression) : Statement, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + value.print(indent + 2)
	}
}

data class VarDeclaration(override val name: String, val value: Expression) : Statement, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + value.print(indent + 2)
	}
}

data class Assignment(override val name: String, val value: Expression) : Statement, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1) + value.print(indent + 2)
	}
}

data class Print(val value: Expression) : Statement {
	override fun print(indent: Int): String {
		return "Print:\n" + value.print(indent + 1)
	}
}


//
// Expressions
//

enum class Operator(val value: String) {
	sum("+"), sub("-"), mul("*"), div("/"), mod("%")
}

interface BinaryExpression : Expression {
	val left: Expression
	val right: Expression
	val operator: Operator
	override fun print(indent: Int): String {
		return "${times(indent + 1)}${javaClass.simpleName}\n${left.print(indent + 1)}${right.print(indent + 1)}"
	}
}

data class SumExpression(override val left: Expression, override val right: Expression, override val operator: Operator = Operator.sum) : BinaryExpression

data class SubtractionExpression(override val left: Expression, override val right: Expression, override val operator: Operator = Operator.sub) : BinaryExpression

data class MultiplicationExpression(override val left: Expression, override val right: Expression, override val operator: Operator = Operator.mul) : BinaryExpression

data class DivisionExpression(override val left: Expression, override val right: Expression, override val operator: Operator = Operator.div) : BinaryExpression

data class UnaryMinusExpression(val value: Expression) : Expression {
	override fun print(indent: Int): String {
		return "${javaClass.simpleName}\n${times(indent)}${value.print(indent + 1)}"
	}
}

data class TypeConversion(val value: Expression, val targetType: Type) : Expression {
	override fun print(indent: Int): String = "${javaClass.simpleName}\n${times(indent)}${value.print(indent + 1)} as ${targetType.print(indent + 1)}"
}

data class VarReference(override val name: String) : Expression, NodeWithName {
	override fun print(indent: Int): String {
		return printTypeAndName(indent + 1)
	}
}

data class IntLit(val value: String) : Expression {
	override fun print(indent: Int): String = times(indent + 1) + value
}

data class DecLit(val value: String) : Expression {
	override fun print(indent: Int): String = times(indent + 1) + value
}