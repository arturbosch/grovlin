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
	open val isBuiltin: Boolean = false
}

interface SymbolType {
	val name: String
	val typeIndex: Int
}

data class VariableSymbol(override val name: String,
						  override var type: SymbolType? = null) : Symbol()

data class BuiltinTypeSymbol(override val name: String,
							 override var type: SymbolType?) : Symbol() {

	override val isBuiltin: Boolean = true
}

abstract class ScopedSymbol : Symbol(), Scope {

	override fun getParentScope(): Scope? = enclosingScope
}

class MethodSymbol(override val name: String,
				   override var type: SymbolType?,
				   override var enclosingScope: Scope) : ScopedSymbol() {

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

class ClassSymbol(override val name: String,
				  override var type: SymbolType?,
				  override var enclosingScope: Scope) : ScopedSymbol() {

	private val members: MutableMap<String, Symbol> = HashMap()
	val parentScope: ClassSymbol? = null
	override var scope: Scope? = LocalScope("<$name:body", enclosingScope)

	override fun define(symbol: Symbol) {
		members[symbol.name] = symbol
	}

	override fun resolve(name: String): Symbol? = members[name]
			?: parentScope?.resolve(name)
			?: enclosingScope.resolve(name)

	fun resolveMember(name: String): Symbol? = members[name] ?: parentScope?.resolveMember(name)

	override fun getParentScope(): Scope? = parentScope ?: enclosingScope

	override fun toString(): String {
		return "${javaClass.simpleName}(name=$name, memberSymbols=$members)"
	}
}
