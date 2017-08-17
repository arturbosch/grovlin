package io.gitlab.arturbosch.grovlin.ast.symbols

import java.util.HashMap

/**
 * @author Artur Bosch
 */
class MethodSymbol(override val name: String,
				   override var type: SymbolType?,
				   override var enclosingScope: Scope,
				   override val needOverride: Boolean = false) : ScopedSymbol() {

	private val symbols: MutableMap<String, Symbol> = HashMap()
	val parameterScope: Scope = LocalScope("<$name-parameters>", enclosingScope)

	override fun define(symbol: Symbol) {
		symbols[symbol.name] = symbol
	}

	override fun resolve(name: String): Symbol? = parameterScope.resolve(name)
			?: enclosingScope.resolve(name)

	override fun toString(): String {
		return "${javaClass.simpleName}(name=$name, symbols=$symbols)"
	}
}
