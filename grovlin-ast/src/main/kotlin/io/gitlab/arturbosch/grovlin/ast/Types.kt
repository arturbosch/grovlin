package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

interface Type : Named {
	companion object {
		fun of(type: String) = when (type) {
			"Bool" -> BoolType
			"Int" -> IntType
			"Decimal" -> DecimalType
			else -> ObjectOrTypeType(type)
		}
	}
}

interface NumberType : Type

abstract class PrimitiveType : Type {
	override fun toString(): String = name
}

class ObjectOrTypeType(override val name: String) : Type {
	override fun toString(): String = name
}

object BoolType : PrimitiveType() {
	override val name: String = "Bool"
}

object IntType : PrimitiveType(), NumberType {
	override val name: String = "Int"
}

object DecimalType : PrimitiveType(), NumberType {
	override val name: String = "Decimal"
}

object UnknownType : Type {
	override val name: String = "Unknown"
	override fun toString(): String = name
}

object VoidType : Type {
	override val name: String = "Void"
	override fun toString(): String = name
}
