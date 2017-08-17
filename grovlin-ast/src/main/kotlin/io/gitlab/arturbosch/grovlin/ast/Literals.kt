package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.builtins.StringType

/**
 * @author Artur Bosch
 */

class IntLit(val value: String) : Expression(), NodeWithType {
	override var type: Type = IntType
	override var evaluationType: Type? = type
}

class BoolLit(val value: Boolean) : Expression(), NodeWithType {
	override var type: Type = BoolType
	override var evaluationType: Type? = type
}

class DecLit(val value: String) : Expression(), NodeWithType {
	override var type: Type = DecimalType
	override var evaluationType: Type? = type
}

class StringLit(val value: String) : Expression(), NodeWithType {
	override var type: Type = StringType
	override var evaluationType: Type? = type
}
