package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.ast.builtins.StringType
import io.gitlab.arturbosch.grovlin.ast.symbols.SymbolType
import io.gitlab.arturbosch.grovlin.ast.symbols.T_BOOL_INDEX
import io.gitlab.arturbosch.grovlin.ast.symbols.T_DECIMAL_INDEX
import io.gitlab.arturbosch.grovlin.ast.symbols.T_INT_INDEX
import io.gitlab.arturbosch.grovlin.ast.symbols.T_UNKNOWN_INDEX
import io.gitlab.arturbosch.grovlin.ast.symbols.T_USER_INDEX
import io.gitlab.arturbosch.grovlin.ast.symbols.T_VOID_INDEX

/**
 * @author Artur Bosch
 */
abstract class Type : Node(), NodeWithName, SymbolType {

	companion object {
		fun of(type: String) = when (type) {
			"Bool", "Boolean" -> BoolType
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
	override val typeIndex: Int = T_USER_INDEX
	override fun toString(): String = name
}

object BoolType : PrimitiveType() {
	override val typeIndex: Int = T_BOOL_INDEX
	override val name: String = "Bool"
}

object IntType : NumberType() {
	override val typeIndex: Int = T_INT_INDEX
	override val name: String = "Int"
}

object DecimalType : NumberType() {
	override val typeIndex: Int = T_DECIMAL_INDEX
	override val name: String = "Decimal"
}

object VoidType : Type() {
	override val typeIndex: Int = T_VOID_INDEX
	override val name: String = "Void"
	override fun toString(): String = name
}

object UnknownType : Type() {
	override val typeIndex: Int = T_UNKNOWN_INDEX
	override val name: String = "Unknown"
	override fun toString(): String = name
}
