package io.gitlab.arturbosch.grovlin.ast.builtins

import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType
import io.gitlab.arturbosch.grovlin.ast.symbols.T_STRING_INDEX

/**
 * @author Artur Bosch
 */
abstract class BuiltinObjectType(name: String) : ObjectOrTypeType(name)

object StringType : BuiltinObjectType("String") {
	override val typeIndex: Int = T_STRING_INDEX
}
