package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.Declaration
import java.util.HashMap
import java.util.HashSet

/**
 * @author Artur Bosch
 */
abstract class Symbol {
	abstract val name: String
	open var type: SymbolType? = null
	open var scope: Scope? = null
	open var def: Declaration? = null
	open val isBuiltin: Boolean = false
	open val needOverride: Boolean = false
}

interface SymbolType {
	val name: String
	val typeIndex: Int
}

data class VariableSymbol(override val name: String) : Symbol()

data class PropertySymbol(override val name: String,
						  override val needOverride: Boolean = false) : Symbol()

data class BuiltinTypeSymbol(override val name: String,
							 override var type: SymbolType?) : Symbol() {

	override val isBuiltin: Boolean = true
}

abstract class ScopedSymbol : Symbol(), Scope {

	override val declarations: MutableSet<String> = HashSet()
	override val declarationsMap: MutableMap<String, Declaration> = HashMap()
}
