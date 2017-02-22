package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

data class IntLit(val value: String, override val position: Position? = null) : Expression, NodeWithType {
	override var type: Type = IntType
}

data class BoolLit(val value: Boolean, override val position: Position? = null) : Expression, NodeWithType {
	override var type: Type = BoolType
}

data class DecLit(val value: String, override val position: Position? = null) : Expression, NodeWithType {
	override var type: Type = DecimalType
}