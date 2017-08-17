package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.Declaration
import java.util.HashMap
import java.util.HashSet

/**
 * @author Artur Bosch
 */
abstract class BaseScope : Scope {

	override val declarations: MutableSet<String> = HashSet()

	override val declarationsMap: MutableMap<String, Declaration> = HashMap()
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
}
