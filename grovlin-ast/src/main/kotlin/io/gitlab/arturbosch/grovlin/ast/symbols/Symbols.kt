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
}

interface SymbolType {
	val name: String
	val typeIndex: Int
}

data class VariableSymbol(override val name: String,
						  override var type: SymbolType? = null) : Symbol()

data class PropertySymbol(override val name: String,
						  override var type: SymbolType? = null) : Symbol()

data class BuiltinTypeSymbol(override val name: String,
							 override var type: SymbolType?) : Symbol() {

	override val isBuiltin: Boolean = true
}

abstract class ScopedSymbol : Symbol(), Scope {

	override val declarations: MutableSet<String> = HashSet()
	override val declarationsMap: MutableMap<String, Declaration> = HashMap()
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
	var parentScope: ClassSymbol? = null
	val traitScopes: MutableList<ClassSymbol> = mutableListOf()
	override var scope: Scope? = LocalScope("<$name:body", enclosingScope)

	override fun define(symbol: Symbol) {
		members[symbol.name] = symbol
	}

	override fun resolve(name: String): Symbol? = members[name]
			?: getParentScope()?.resolve(name)

	fun resolveMember(name: String): Symbol? = members[name]
			?: parentScope?.resolveMember(name)
			?: traitScopes.find { it.resolveMember(name) != null }?.resolveMember(name)

	fun addTraitScope(trait: ClassSymbol?) {
		if (trait != null) {
			traitScopes.add(trait)
		}
	}

	fun getMemberSymbols() = members.values.toList()

	override fun getParentScope(): Scope? = parentScope ?: enclosingScope

	override fun toString(): String {
		return "${javaClass.simpleName}(name=$name, memberSymbols=$members)"
	}
}
