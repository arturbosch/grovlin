package io.gitlab.arturbosch.grovlin.ast

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
