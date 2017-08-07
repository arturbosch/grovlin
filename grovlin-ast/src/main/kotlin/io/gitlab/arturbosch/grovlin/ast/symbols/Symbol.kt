package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.Declaration
import java.util.HashMap

/**
 * @author Artur Bosch
 */
abstract class Symbol {
	abstract val name: String
	open var type: SymbolType? = null
	open var scope: Scope? = null
	open var def: Declaration? = null
}

interface SymbolType {
	val name: String
}

class VariableSymbol(override val name: String,
					 override var type: SymbolType? = null) : Symbol()

class BuiltinTypeSymbol(override val name: String) : SymbolType, Symbol()

abstract class ScopedSymbol : Symbol(), Scope

class MethodSymbol(override val name: String,
				   override var type: SymbolType?,
				   override var enclosingScope: Scope) : ScopedSymbol() {

	private val symbols: MutableMap<String, Symbol> = HashMap()
	override var scope: Scope? = LocalScope("<$name-parameters>", this)
	val bodyScope: Scope = LocalScope("<$name-body>", this)

	override fun define(symbol: Symbol) {
		symbols[symbol.name] = symbol
	}

	override fun resolve(name: String): Symbol? =
			bodyScope.resolve(name)
					?: scope?.resolve(name)
					?: symbols[name]
}

class ClassSymbol(override val name: String) : SymbolType, ScopedSymbol() {

	private val symbols: MutableMap<String, Symbol> = HashMap()
	override val enclosingScope: Scope? = null
	val parentScope: Scope? = null

	override fun define(symbol: Symbol) {
		symbols[symbol.name] = symbol
	}

	override fun resolve(name: String): Symbol? {
		return symbols[name]
				?: parentScope?.resolve(name)
				?: enclosingScope?.resolve(name)
	}
}

interface Scope {
	val name: String
	val enclosingScope: Scope?
	fun define(symbol: Symbol)
	fun resolve(name: String): Symbol?
}

abstract class BaseScope : Scope {

	private val symbols: MutableMap<String, Symbol> = HashMap()

	override val enclosingScope: Scope? = null

	override fun define(symbol: Symbol) {
		symbols[symbol.name] = symbol
	}

	override fun resolve(name: String): Symbol? {
		return symbols[name] ?: enclosingScope?.resolve(name)
	}

	override fun toString(): String {
		return "<scope:$name>"
	}
}

class FileScope(fileName: String) : BaseScope() {
	override val name: String = "<file:$fileName>"
}

class LocalScope(override val name: String, override val enclosingScope: Scope) : BaseScope()
