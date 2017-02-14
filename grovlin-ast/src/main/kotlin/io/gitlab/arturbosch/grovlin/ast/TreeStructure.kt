package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

//
// Interfaces
//

interface Named {
	val name: String
}

data class Reference<N : Named>(val name: String, var referred: N? = null) {
	override fun toString(): String {
		if (referred == null) {
			return "Ref($name)[Unsolved]"
		} else {
			return "Ref($name)[Solved]"
		}
	}

	fun tryToResolve(candidates: List<N>): Boolean {
		val res = candidates.find { it.name == this.name }
		referred = res
		return res != null
	}

}

interface Node {
	val position: Position?
}

interface NodeWithName : Node, Named

interface NodeWithStatements : Node {
	val statements: MutableList<Statement>
}

interface Expression : Node

interface Statement : Node

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

data class GrovlinFile(override val name: String, override val statements: MutableList<Statement>, override val position: Position? = null) : NodeWithStatements, NodeWithName

data class Program(override val statements: MutableList<Statement>, override val position: Position? = null) : Statement, NodeWithStatements

//
// Statements
//

data class TypeDeclaration(override val name: String, override val statements: MutableList<Statement>, override val position: Position? = null) : Statement, NodeWithStatements, NodeWithName

data class MethodDeclaration(override val name: String, override val statements: MutableList<Statement>, override val position: Position? = null) : Statement, NodeWithStatements, NodeWithName

data class LambdaDeclaration(override val name: String, override val statements: MutableList<Statement>, override val position: Position? = null) : Statement, NodeWithStatements, NodeWithName

data class PropertyDeclaration(override val name: String, val value: Expression, override val position: Position? = null) : Statement, NodeWithName

data class VarDeclaration(override val name: String, val value: Expression, override val position: Position? = null) : Statement, NodeWithName

data class Assignment(val reference: Reference<VarDeclaration>, val value: Expression, override val position: Position? = null) : Statement

data class Print(val value: Expression, override val position: Position? = null) : Statement


//
// Expressions
//

interface BinaryExpression : Expression {
	val left: Expression
	val right: Expression
}

data class SumExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class SubtractionExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class MultiplicationExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class DivisionExpression(override val left: Expression, override val right: Expression, override val position: Position? = null) : BinaryExpression

data class UnaryMinusExpression(val value: Expression, override val position: Position? = null) : Expression

data class TypeConversion(val value: Expression, val targetType: Type, override val position: Position? = null) : Expression

data class VarReference(val reference: Reference<VarDeclaration>, override val position: Position? = null) : Expression

data class IntLit(val value: String, override val position: Position? = null) : Expression

data class DecLit(val value: String, override val position: Position? = null) : Expression