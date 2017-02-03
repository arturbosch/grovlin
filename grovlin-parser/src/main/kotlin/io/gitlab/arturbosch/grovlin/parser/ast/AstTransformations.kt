package io.gitlab.arturbosch.grovlin.parser.ast

import io.gitlab.arturbosch.grovlin.GrovlinParser.AssignmentStatementContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.BinaryOperationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.DecimalContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.DecimalLiteralContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.DefDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.DefMemberDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.ExpressionContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.GrovlinFileContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.IntLiteralContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.IntegerContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.LambdaDefinitionContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.MemberDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.MemberDeclarationStatementContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.MethodDefinitionContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.ParenExpressionContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.PrintStatementContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.ProgramStatementContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.PropertyMemberDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.StatementContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.TypeContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.TypeConversionContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.TypeMemberDeclarationContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.VarDeclarationStatementContext
import io.gitlab.arturbosch.grovlin.GrovlinParser.VarReferenceContext
import java.util.ArrayList

/**
 * @author Artur Bosch
 */
fun GrovlinFileContext.toAsT(): GrovlinFile = GrovlinFile("File.grovlin", statements().statement().mapTo(ArrayList()) { it.toAst() })

fun StatementContext.toAst(): Statement = when (this) {
	is MemberDeclarationStatementContext -> memberDeclaration().toAst()
	is VarDeclarationStatementContext -> VarDeclaration(varDeclaration().assignment().ID().text,
			varDeclaration().assignment().expression().toAst())
	is AssignmentStatementContext -> Assignment(assignment().ID().text, assignment().expression().toAst())
	is PrintStatementContext -> Print(print().expression().toAst())
	is ProgramStatementContext -> Program(program().statements().statement().mapTo(ArrayList()) { it.toAst() })
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

fun MemberDeclarationContext.toAst(): Statement = when (this) {
	is PropertyMemberDeclarationContext -> PropertyDeclaration(propertyDeclaration().assignment().ID().text,
			propertyDeclaration().assignment().expression().toAst())
	is TypeMemberDeclarationContext -> TypeDeclaration(typeDeclaration().ID().text,
			typeDeclaration().memberDeclaration().mapTo(ArrayList()) { it.toAst() })
	is DefMemberDeclarationContext -> defDeclaration().toAst()
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

fun DefDeclarationContext.toAst(): Statement = when (this) {
	is MethodDefinitionContext -> MethodDeclaration(methodDeclaration().ID().text,
			methodDeclaration().statements().statement().mapTo(ArrayList()) { it.toAst() })
	is LambdaDefinitionContext -> LambdaDeclaration(lambdaDeclaration().ID().text,
			lambdaDeclaration().statements().statement().mapTo(ArrayList()) { it.toAst() })
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

fun ExpressionContext.toAst(): Expression = when (this) {
	is BinaryOperationContext -> toAst()
	is IntLiteralContext -> IntLit(text)
	is DecimalLiteralContext -> DecLit(text)
	is ParenExpressionContext -> expression().toAst()
	is VarReferenceContext -> VarReference(text)
	is TypeConversionContext -> TypeConversion(expression().toAst(), targetType.toAst())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun BinaryOperationContext.toAst(): Expression = when (operator.text) {
	"+" -> SumExpression(left.toAst(), right.toAst())
	"-" -> SubtractionExpression(left.toAst(), right.toAst())
	"*" -> MultiplicationExpression(left.toAst(), right.toAst())
	"/" -> DivisionExpression(left.toAst(), right.toAst())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

fun TypeContext.toAst(): Type = when (this) {
	is IntegerContext -> IntType
	is DecimalContext -> DecimalType
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

