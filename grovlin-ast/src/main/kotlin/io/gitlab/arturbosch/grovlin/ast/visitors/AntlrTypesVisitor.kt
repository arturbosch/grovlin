package io.gitlab.arturbosch.grovlin.ast.visitors

import io.gitlab.arturbosch.grovlin.GrovlinParser
import io.gitlab.arturbosch.grovlin.GrovlinParserBaseVisitor
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.builtins.StringType

/**
 * @author Artur Bosch
 */
class AntlrTypesVisitor : GrovlinParserBaseVisitor<Type>() {

	override fun visitInteger(ctx: GrovlinParser.IntegerContext): Type {
		return IntType
	}

	override fun visitDecimal(ctx: GrovlinParser.DecimalContext): Type {
		return DecimalType
	}

	override fun visitBool(ctx: GrovlinParser.BoolContext): Type {
		return BoolType
	}

	override fun visitString(ctx: GrovlinParser.StringContext): Type {
		return StringType
	}
}
