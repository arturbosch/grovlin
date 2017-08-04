package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.builtins.StringType

/**
 * @author Artur Bosch
 */

abstract class Type : Node(), NodeWithName {
	companion object {
		fun of(type: String) = when (type) {
			"Bool" -> BoolType
			"Int" -> IntType
			"Decimal" -> DecimalType
			"String" -> StringType
			"Void" -> VoidType
			else -> ObjectOrTypeType(type)
		}
	}
}

abstract class NumberType : PrimitiveType()

abstract class PrimitiveType : Type() {
	override fun toString(): String = name
}

open class ObjectOrTypeType(override val name: String) : Type() {
	override fun toString(): String = name
}

object BoolType : PrimitiveType() {
	override val name: String = "Bool"
}

object IntType : NumberType() {
	override val name: String = "Int"
}

object DecimalType : NumberType() {
	override val name: String = "Decimal"
}

object UnknownType : Type() {
	override val name: String = "Unknown"
	override fun toString(): String = name
}

object VoidType : Type() {
	override val name: String = "Void"
	override fun toString(): String = name
}
