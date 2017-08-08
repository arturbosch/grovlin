package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

abstract class ConditionalStatement(val condition: Expression,
									val thenStatement: BlockStatement) : Statement()

class IfStatement(condition: Expression,
				  thenStatement: BlockStatement,
				  val elifs: MutableList<ElifStatement>,
				  val elseStatement: BlockStatement?)
	: ConditionalStatement(condition, thenStatement)

class ElifStatement(condition: Expression,
					thenStatement: BlockStatement)
	: ConditionalStatement(condition, thenStatement)

class BlockStatement(override val statements: MutableList<Statement>)
	: Statement(), NodeWithStatements

class ExpressionStatement(val expression: Expression) : Statement()

class Assignment(val varReference: VarReference,
				 val value: Expression)
	: Statement(), NodeWithReference<VariableDeclaration> {

	val varName: String = varReference.varName
	override val reference: Reference<VariableDeclaration> = varReference.reference
}

class ForStatement(val varDeclaration: VarDeclaration,
				   val expression: Expression,
				   override val block: BlockStatement)
	: Statement(), NodeWithBlock

class WhileStatement(condition: Expression,
					 thenStatement: BlockStatement)
	: ConditionalStatement(condition, thenStatement)

class ReturnStatement(val expression: Expression)
	: Statement()
