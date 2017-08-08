package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.builtins.StringType

/**
 * @author Artur Bosch
 */

class IntLit(val value: String) : Expression(), NodeWithType {
	override var type: Type = IntType
}

class BoolLit(val value: Boolean) : Expression(), NodeWithType {
	override var type: Type = BoolType
}

class DecLit(val value: String) : Expression(), NodeWithType {
	override var type: Type = DecimalType
}

class StringLit(val value: String) : Expression(), NodeWithType {
	override var type: Type = StringType
}
