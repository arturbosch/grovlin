package io.gitlab.arturbosch.grovlin.ast.visitors.antlr

import io.gitlab.arturbosch.grovlin.GrovlinParser
import io.gitlab.arturbosch.grovlin.GrovlinParserBaseVisitor
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.builtins.StringType
import io.gitlab.arturbosch.grovlin.ast.toPosition

/**
 * @author Artur Bosch
 */
class AntlrTypesVisitor : GrovlinParserBaseVisitor<Type>() {

	override fun visitUserType(ctx: GrovlinParser.UserTypeContext): Type {
		return Type.of(ctx.text).apply {
			position = ctx.toPosition()
		}
	}

	override fun visitInteger(ctx: GrovlinParser.IntegerContext): Type {
		return IntType.apply {
			position = ctx.toPosition()
		}
	}

	override fun visitDecimal(ctx: GrovlinParser.DecimalContext): Type {
		return DecimalType.apply {
			position = ctx.toPosition()
		}
	}

	override fun visitBool(ctx: GrovlinParser.BoolContext): Type {
		return BoolType.apply {
			position = ctx.toPosition()
		}
	}

	override fun visitString(ctx: GrovlinParser.StringContext): Type {
		return StringType.apply {
			position = ctx.toPosition()
		}
	}
}
