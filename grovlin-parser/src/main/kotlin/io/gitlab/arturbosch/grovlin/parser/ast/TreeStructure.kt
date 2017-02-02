package io.gitlab.arturbosch.grovlin.parser.ast

/**
 * @author Artur Bosch
 */
interface Node

interface NodeWithName : Node {
	val name: String
}

interface Type
interface Expression
interface Statement

data class GrovlinFile(val statement: MutableList<Statement>) : Node

//
// Types
//

object IntType : Type

object DecimalType : Type

//
// Statements
//

data class TypeDeclaration(override val name: String, val list: MutableList<Statement>) : Statement, NodeWithName

data class MethodDeclaration(override val name: String, val statements: MutableList<Statement>) : Statement, NodeWithName

data class LambdaDeclaration(override val name: String, val statements: MutableList<Statement>) : Statement, NodeWithName

data class PropertyDeclaration(override val name: String, val value: Expression) : Statement, NodeWithName

data class VarDeclaration(override val name: String, val value: Expression) : Statement, NodeWithName

data class Assignment(override val name: String, val value: Expression) : Statement, NodeWithName

data class Print(val value: Expression) : Statement

//
// Expressions
//

interface BinaryExpression : Expression {
	val left: Expression
	val right: Expression
}

data class SumExpression(override val left: Expression, override val right: Expression) : BinaryExpression

data class SubtractionExpression(override val left: Expression, override val right: Expression) : BinaryExpression

data class MultiplicationExpression(override val left: Expression, override val right: Expression) : BinaryExpression

data class DivisionExpression(override val left: Expression, override val right: Expression) : BinaryExpression

data class UnaryMinusExpression(val value: Expression) : Expression

data class TypeConversion(val value: Expression, val targetType: Type) : Expression

data class VarReference(val varName: String) : Expression

data class IntLit(val value: String) : Expression

data class DecLit(val value: String) : Expression

