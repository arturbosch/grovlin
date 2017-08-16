package io.gitlab.arturbosch.grovlin.ast.visitors.antlr

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
import io.gitlab.arturbosch.grovlin.ast.SetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.StringLit
import io.gitlab.arturbosch.grovlin.ast.SubtractionExpression
import io.gitlab.arturbosch.grovlin.ast.SumExpression
import io.gitlab.arturbosch.grovlin.ast.ThisReference
import io.gitlab.arturbosch.grovlin.ast.TypeConversion
import io.gitlab.arturbosch.grovlin.ast.UnequalExpression
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.XorExpression
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_PRINTLN_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_PRINT_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_RAND_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_READLINE_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_TO_STRING_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.Print
import io.gitlab.arturbosch.grovlin.ast.builtins.PrintLn
import io.gitlab.arturbosch.grovlin.ast.builtins.RandomNumber
import io.gitlab.arturbosch.grovlin.ast.builtins.ReadLine
import io.gitlab.arturbosch.grovlin.ast.builtins.ToString
import io.gitlab.arturbosch.grovlin.ast.toPosition

/**
 * @author Artur Bosch
 */
class AntlrExpressionVisitor : GrovlinParserBaseVisitor<Expression>() {

	val typeVisitor = AntlrTypesVisitor()

	override fun visitDecimalLiteral(ctx: GrovlinParser.DecimalLiteralContext): Expression {
		return DecLit(ctx.text).apply {
			position = ctx.toPosition()
		}
	}

	override fun visitSetterAccessExpression(ctx: GrovlinParser.SetterAccessExpressionContext): Expression {
		val scopeExpr = ctx.scope?.let { visit(it) }
		val expression = visit(ctx.assignment().expression())
		return SetterAccessExpression(scopeExpr,
				ctx.assignment().ID().text,
				expression).apply {
			position = ctx.toPosition()
			expression.parent = this
			scopeExpr?.parent = this
			children = scopeExpr?.let { listOf(it, expression) } ?: listOf(expression)
		}
	}

	override fun visitMinusExpression(ctx: GrovlinParser.MinusExpressionContext): Expression {
		val expression = visit(ctx.expression())
		return MinusExpression(expression).apply {
			position = ctx.toPosition()
			expression.parent = this
			children = listOf(expression)
		}
	}

	override fun visitIntLiteral(ctx: GrovlinParser.IntLiteralContext): Expression {
		return IntLit(ctx.text).apply {
			position = ctx.toPosition()
		}
	}

	override fun visitNotExpression(ctx: GrovlinParser.NotExpressionContext): Expression {
		val expression = visit(ctx.expression())
		return NotExpression(expression).apply {
			position = ctx.toPosition()
			expression.parent = this
			children = listOf(expression)
		}
	}

	override fun visitParenExpression(ctx: GrovlinParser.ParenExpressionContext): Expression {
		val expression = visit(ctx.expression())
		return ParenExpression(expression).apply {
			position = ctx.toPosition()
			expression.parent = this
			children = listOf(expression)
		}
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
		}.apply {
			position = ctx.toPosition()
			children = listOf(left, right)
			left.parent = this
			right.parent = this
		}
	}

	override fun visitTypeConversion(ctx: GrovlinParser.TypeConversionContext): Expression {
		val type = typeVisitor.visit(ctx.targetType)
		val expression = visit(ctx.value)
		return TypeConversion(expression, type).apply {
			position = ctx.toPosition()
			children = listOf(value, targetType)
			value.parent = this
			targetType.parent = this
		}
	}

	override fun visitIntRangeExpression(ctx: GrovlinParser.IntRangeExpressionContext): Expression {
		val startLit = ctx.INTLIT(0)
		val start = IntLit(startLit.text).apply { position = startLit.toPosition() }
		val endLit = ctx.INTLIT(1)
		val endExclusive = IntLit(endLit.text).apply { position = endLit.toPosition() }
		return IntRangeExpression(start, endExclusive).apply {
			position = ctx.toPosition()
			children = listOf(start, endExclusive)
			start.parent = this
			endExclusive.parent = this
		}
	}

	override fun visitBoolLiteral(ctx: GrovlinParser.BoolLiteralContext): Expression {
		return BoolLit(ctx.BOOLLIT().text.toBoolean()).apply {
			position = ctx.toPosition()
		}
	}

	override fun visitVarReference(ctx: GrovlinParser.VarReferenceContext): Expression {
		return VarReference(ctx.text).apply {
			position = ctx.toPosition()
		}
	}

	override fun visitObjectCreationExpression(ctx: GrovlinParser.ObjectCreationExpressionContext): Expression {
		val type = ObjectOrTypeType(ctx.TYPEID().text).apply { position = ctx.TYPEID().toPosition() }
		return ObjectCreation(type).apply {
			position = ctx.toPosition()
			children = listOf(type)
			type.parent = this
		}
	}

	override fun visitCallExpression(ctx: GrovlinParser.CallExpressionContext): Expression {
		val scope = ctx.scope?.let { visit(it) }
		val arguments = ctx.argumentList()?.argument()?.map { visitArgument(it) }
		val methodName = ctx.methodName.text
		return convertToCallConsiderBuiltins(scope, methodName, arguments ?: emptyList()).apply {
			position = ctx.toPosition()
			children = (arguments?.plus(scope))?.filterNotNull() ?: emptyList()
			scope?.parent = this
			arguments?.forEach { it.parent = this }
		}
	}

	private fun convertToCallConsiderBuiltins(scope: Expression?,
											  methodName: String,
											  arguments: List<Expression>): Expression {

		fun normalCall() = CallExpression(scope, methodName, arguments)

		fun singleArgumentCall() = when (methodName) {
			BUILTIN_PRINTLN_NAME -> PrintLn(arguments[0])
			BUILTIN_PRINT_NAME -> Print(arguments[0])
			BUILTIN_RAND_NAME -> RandomNumber(arguments[0])
			else -> normalCall()
		}

		fun zeroArgumentCall() = when (methodName) {
			BUILTIN_PRINTLN_NAME -> PrintLn(StringLit(""))
			BUILTIN_PRINT_NAME -> Print(StringLit(""))
			BUILTIN_READLINE_NAME -> ReadLine()
			else -> normalCall()
		}

		return if (scope == null) {
			when {
				arguments.isEmpty() -> zeroArgumentCall()
				arguments.size == 1 -> singleArgumentCall()
				else -> normalCall()
			}
		} else when (methodName) {
			BUILTIN_TO_STRING_NAME -> ToString(scope)
			else -> normalCall()
		}
	}

	override fun visitArgument(ctx: GrovlinParser.ArgumentContext): Expression {
		return visit(ctx.expression())
	}

	override fun visitGetterAccessExpression(ctx: GrovlinParser.GetterAccessExpressionContext): Expression {
		val scope = ctx.scope?.let { visit(it) }
		return GetterAccessExpression(scope, ctx.fieldName.text).apply {
			position = ctx.toPosition()
			scope?.let { children = listOf(scope) }
			scope?.parent = this
		}
	}

	override fun visitStringLiteral(ctx: GrovlinParser.StringLiteralContext): Expression {
		var value = ctx.STRINGLIT().text
		assert(value.isNotEmpty()) { "String literal must start and end with quotation marks!" }
		value = value.substring(1, value.lastIndex)
		return StringLit(value).apply {
			position = ctx.toPosition()
		}
	}

	override fun visitThisExpression(ctx: GrovlinParser.ThisExpressionContext): Expression {
		return ThisReference("this").apply {
			position = ctx.toPosition()
		}
	}
}
