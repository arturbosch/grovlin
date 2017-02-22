package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

interface Type : Named

abstract class PrimitiveType : Type {
	override fun toString(): String = name
}

object BoolType : PrimitiveType() {
	override val name: String
		get() = "Bool"
}

object IntType : PrimitiveType() {
	override val name: String
		get() = "Int"
}

object DecimalType : PrimitiveType() {
	override val name: String
		get() = "Decimal"
}

object UnknownType : Type {
	override val name: String
		get() = "Unknown"

	override fun toString(): String = name
}
