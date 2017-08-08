package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.VoidType
import io.gitlab.arturbosch.grovlin.ast.builtins.StringType
import java.util.HashMap

/**
 * @author Artur Bosch
 */
interface Scope {
	val name: String
	val enclosingScope: Scope?
	fun define(symbol: Symbol)
	fun resolve(name: String): Symbol?
	fun getParentScope(): Scope?

	fun getEnclosingClass(): Symbol? {
		var current: Scope? = this
		while (current != null) {
			if (current is ClassSymbol) return current
			current = getParentScope()
		}
		return null
	}
}

abstract class BaseScope : Scope {

	protected val symbols: MutableMap<String, Symbol> = HashMap()

	override val enclosingScope: Scope? = null

	override fun define(symbol: Symbol) {
		symbols[symbol.name] = symbol
	}

	override fun resolve(name: String): Symbol? {
		return symbols[name] ?: enclosingScope?.resolve(name)
	}

	override fun toString(): String {
		return "${javaClass.simpleName}(name=$name, symbols=$symbols)"
	}

	override fun getParentScope() = enclosingScope
}

class FileScope(fileName: String) : BaseScope() {

	init {
		define(BuiltinTypeSymbol(IntType.name, IntType))
		define(BuiltinTypeSymbol(BoolType.name, BoolType))
		define(BuiltinTypeSymbol(VoidType.name, VoidType))
		define(BuiltinTypeSymbol(DecimalType.name, DecimalType))
		define(BuiltinTypeSymbol(StringType.name, StringType))
	}

	override val name: String = "<file:$fileName>"
}

class LocalScope(override val name: String,
				 override val enclosingScope: Scope) : BaseScope()
