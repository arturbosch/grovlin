package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

interface Type : Named

interface NumberType : Type

abstract class PrimitiveType : Type {
	override fun toString(): String = name
}

class ObjectOrTypeType(override val name: String) : Type

object BoolType : PrimitiveType() {
	override val name: String
		get() = "Bool"
}

object IntType : PrimitiveType(), NumberType {
	override val name: String
		get() = "Int"
}

object DecimalType : PrimitiveType(), NumberType {
	override val name: String
		get() = "Decimal"
}

object UnknownType : Type {
	override val name: String
		get() = "Unknown"

	override fun toString(): String = name
}
