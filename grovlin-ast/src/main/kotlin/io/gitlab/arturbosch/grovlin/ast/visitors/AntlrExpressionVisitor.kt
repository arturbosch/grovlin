package io.gitlab.arturbosch.grovlin.ast.visitors

import io.gitlab.arturbosch.grovlin.GrovlinParser
import io.gitlab.arturbosch.grovlin.GrovlinParserBaseVisitor
import io.gitlab.arturbosch.grovlin.ast.AndExpression
import io.gitlab.arturbosch.grovlin.ast.BoolLit
import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.DecLit
import io.gitlab.arturbosch.grovlin.ast.DivisionExpression
import io.gitlab.arturbosch.grovlin.ast.EqualExpression
import io.gitlab.arturbosch.grovlin.ast.Expression
import io.gitlab.arturbosch.grovlin.ast.GetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.GreaterEqualExpression
import io.gitlab.arturbosch.grovlin.ast.GreaterExpression
import io.gitlab.arturbosch.grovlin.ast.IntLit
import io.gitlab.arturbosch.grovlin.ast.IntRangeExpression
import io.gitlab.arturbosch.grovlin.ast.LessEqualExpression
import io.gitlab.arturbosch.grovlin.ast.LessExpression
import io.gitlab.arturbosch.grovlin.ast.MinusExpression
import io.gitlab.arturbosch.grovlin.ast.MultiplicationExpression
import io.gitlab.arturbosch.grovlin.ast.NotExpression
import io.gitlab.arturbosch.grovlin.ast.ObjectCreation
import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType
import io.gitlab.arturbosch.grovlin.ast.OrExpression
import io.gitlab.arturbosch.grovlin.ast.ParenExpression
import io.gitlab.arturbosch.grovlin.ast.Reference
import io.gitlab.arturbosch.grovlin.ast.SetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.StringLit
import io.gitlab.arturbosch.grovlin.ast.SubtractionExpression
import io.gitlab.arturbosch.grovlin.ast.SumExpression
import io.gitlab.arturbosch.grovlin.ast.ThisReference
import io.gitlab.arturbosch.grovlin.ast.TypeConversion
import io.gitlab.arturbosch.grovlin.ast.UnequalExpression
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.XorExpression
import io.gitlab.arturbosch.grovlin.ast.toPosition

/**
 * @author Artur Bosch
 */
class AntlrExpressionVisitor : GrovlinParserBaseVisitor<Expression>() {

	private val typeVisitor = AntlrTypesVisitor()

	override fun visitDecimalLiteral(ctx: GrovlinParser.DecimalLiteralContext): Expression {
		return DecLit(ctx.text)
	}

	override fun visitSetterAccessExpression(ctx: GrovlinParser.SetterAccessExpressionContext): Expression {
		val scopeExpr = ctx.scope?.let { visit(it) }
		return SetterAccessExpression(scopeExpr, ctx.assignment().ID().text,
				visit(ctx.assignment().expression()))
	}

	override fun visitMinusExpression(ctx: GrovlinParser.MinusExpressionContext): Expression {
		return MinusExpression(visit(ctx.expression()))
	}

	override fun visitIntLiteral(ctx: GrovlinParser.IntLiteralContext): Expression {
		return IntLit(ctx.text)
	}

	override fun visitNotExpression(ctx: GrovlinParser.NotExpressionContext): Expression {
		return NotExpression(visit(ctx.expression()))
	}

	override fun visitParenExpression(ctx: GrovlinParser.ParenExpressionContext): Expression {
		return ParenExpression(visit(ctx.expression()))
	}

	override fun visitBinaryOperation(ctx: GrovlinParser.BinaryOperationContext): Expression {
		val leftExpr = visit(ctx.left)
		val rightExpr = visit(ctx.right)
		return when (ctx.operator.text) {
			"+" -> SumExpression(leftExpr, rightExpr)
			"-" -> SubtractionExpression(leftExpr, rightExpr)
			"*" -> MultiplicationExpression(leftExpr, rightExpr)
			"/" -> DivisionExpression(leftExpr, rightExpr)
			"&&" -> AndExpression(leftExpr, rightExpr)
			"||" -> OrExpression(leftExpr, rightExpr)
			"^" -> XorExpression(leftExpr, rightExpr)
			"==" -> EqualExpression(leftExpr, rightExpr)
			"!=" -> UnequalExpression(leftExpr, rightExpr)
			">=" -> GreaterEqualExpression(leftExpr, rightExpr)
			"<=" -> LessEqualExpression(leftExpr, rightExpr)
			">" -> GreaterExpression(leftExpr, rightExpr)
			"<" -> LessExpression(leftExpr, rightExpr)
			else -> throw UnsupportedOperationException(javaClass.canonicalName)
		}.apply { position = ctx.toPosition() }
	}

	override fun visitTypeConversion(ctx: GrovlinParser.TypeConversionContext): Expression {
		val type = typeVisitor.visit(ctx.targetType)
		val expression = visit(ctx.value)
		return TypeConversion(expression, type)
	}

	override fun visitIntRangeExpression(ctx: GrovlinParser.IntRangeExpressionContext): Expression {
		return IntRangeExpression(IntLit(ctx.INTLIT(0).text), IntLit(ctx.INTLIT(1).text))
	}

	override fun visitBoolLiteral(ctx: GrovlinParser.BoolLiteralContext): Expression {
		return BoolLit(ctx.BOOLLIT().text.toBoolean())
	}

	override fun visitVarReference(ctx: GrovlinParser.VarReferenceContext): Expression {
		return VarReference(Reference(ctx.text))
	}

	override fun visitObjectCreationExpression(ctx: GrovlinParser.ObjectCreationExpressionContext): Expression {
		return ObjectCreation(ObjectOrTypeType(ctx.TYPEID().text))
	}

	override fun visitCallExpression(ctx: GrovlinParser.CallExpressionContext): Expression {
		val scope = ctx.scope?.let { visit(it) }
		val arguments = ctx.argumentList()?.argument()?.map { visitArgument(it) }
		return CallExpression(scope, ctx.methodName.text, arguments ?: emptyList())
	}

	override fun visitArgument(ctx: GrovlinParser.ArgumentContext): Expression {
		return visit(ctx.expression())
	}

	override fun visitGetterAccessExpression(ctx: GrovlinParser.GetterAccessExpressionContext): Expression {
		val scope = ctx.scope?.let { visit(it) }
		return GetterAccessExpression(scope, ctx.fieldName.text)
	}

	override fun visitStringLiteral(ctx: GrovlinParser.StringLiteralContext): Expression {
		return StringLit(ctx.text)
	}

	override fun visitThisExpression(ctx: GrovlinParser.ThisExpressionContext): Expression {
		return ThisReference(Reference("this"))
	}
}
