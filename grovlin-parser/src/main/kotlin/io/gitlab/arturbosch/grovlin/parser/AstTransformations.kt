package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.GrovlinParser.*
import io.gitlab.arturbosch.grovlin.parser.ast.Assignment
import io.gitlab.arturbosch.grovlin.parser.ast.DecLit
import io.gitlab.arturbosch.grovlin.parser.ast.DecimalType
import io.gitlab.arturbosch.grovlin.parser.ast.DivisionExpression
import io.gitlab.arturbosch.grovlin.parser.ast.Expression
import io.gitlab.arturbosch.grovlin.parser.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.parser.ast.IntLit
import io.gitlab.arturbosch.grovlin.parser.ast.IntType
import io.gitlab.arturbosch.grovlin.parser.ast.LambdaDeclaration
import io.gitlab.arturbosch.grovlin.parser.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.parser.ast.MultiplicationExpression
import io.gitlab.arturbosch.grovlin.parser.ast.Print
import io.gitlab.arturbosch.grovlin.parser.ast.Program
import io.gitlab.arturbosch.grovlin.parser.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.parser.ast.Statement
import io.gitlab.arturbosch.grovlin.parser.ast.SubtractionExpression
import io.gitlab.arturbosch.grovlin.parser.ast.SumExpression
import io.gitlab.arturbosch.grovlin.parser.ast.Type
import io.gitlab.arturbosch.grovlin.parser.ast.TypeConversion
import io.gitlab.arturbosch.grovlin.parser.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.parser.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.parser.ast.VarReference
import io.gitlab.arturbosch.grovlin.parser.ast.toPosition
import java.util.ArrayList

/**
 * @author Artur Bosch
 */
fun GrovlinFileContext.toAsT(): GrovlinFile = GrovlinFile("File.grovlin",
		statements().statement().mapTo(ArrayList()) { it.toAst() }, toPosition())

fun StatementContext.toAst(): Statement = when (this) {
	is MemberDeclarationStatementContext -> memberDeclaration().toAst()
	is VarDeclarationStatementContext -> VarDeclaration(varDeclaration().assignment().ID().text,
			varDeclaration().assignment().expression().toAst(), toPosition())
	is AssignmentStatementContext -> Assignment(assignment().ID().text, assignment().expression().toAst(), toPosition())
	is PrintStatementContext -> Print(print().expression().toAst(), toPosition())
	is ProgramStatementContext -> Program(program().statements().statement().mapTo(ArrayList()) { it.toAst() }, toPosition())
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

fun MemberDeclarationContext.toAst(): Statement = when (this) {
	is PropertyMemberDeclarationContext -> PropertyDeclaration(propertyDeclaration().assignment().ID().text,
			propertyDeclaration().assignment().expression().toAst(), toPosition())
	is TypeMemberDeclarationContext -> TypeDeclaration(typeDeclaration().ID().text,
			typeDeclaration().memberDeclaration().mapTo(ArrayList()) { it.toAst() }, toPosition())
	is DefMemberDeclarationContext -> defDeclaration().toAst()
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

fun DefDeclarationContext.toAst(): Statement = when (this) {
	is MethodDefinitionContext -> MethodDeclaration(methodDeclaration().ID().text,
			methodDeclaration().statements().statement().mapTo(ArrayList()) { it.toAst() }, toPosition())
	is LambdaDefinitionContext -> LambdaDeclaration(lambdaDeclaration().ID().text,
			lambdaDeclaration().statements().statement().mapTo(ArrayList()) { it.toAst() }, toPosition())
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

fun ExpressionContext.toAst(): Expression = when (this) {
	is BinaryOperationContext -> toAst()
	is IntLiteralContext -> IntLit(text, toPosition())
	is DecimalLiteralContext -> DecLit(text, toPosition())
	is ParenExpressionContext -> expression().toAst()
	is VarReferenceContext -> VarReference(text, toPosition())
	is TypeConversionContext -> TypeConversion(expression().toAst(), targetType.toAst(), toPosition())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun BinaryOperationContext.toAst(): Expression = when (operator.text) {
	"+" -> SumExpression(left.toAst(), right.toAst(), toPosition())
	"-" -> SubtractionExpression(left.toAst(), right.toAst(), toPosition())
	"*" -> MultiplicationExpression(left.toAst(), right.toAst(), toPosition())
	"/" -> DivisionExpression(left.toAst(), right.toAst(), toPosition())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun TypeContext.toAst(): Type = when (this) {
	is IntegerContext -> IntType
	is DecimalContext -> DecimalType
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

