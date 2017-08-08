package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.Declaration
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.VoidType
import io.gitlab.arturbosch.grovlin.ast.builtins.StringType
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
}

data class VariableSymbol(override val name: String,
						  override var type: SymbolType? = null) : Symbol()

data class BuiltinTypeSymbol(override val name: String,
							 override var type: SymbolType?) : SymbolType, Symbol() {

	override val isBuiltin: Boolean = true
}

abstract class ScopedSymbol : Symbol(), Scope

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
				  override var enclosingScope: Scope) : SymbolType, ScopedSymbol() {

	private val members: MutableMap<String, Symbol> = HashMap()
	val parentScope: ClassSymbol? = null
	override var scope: Scope? = LocalScope("<$name:body", enclosingScope)

	override fun define(symbol: Symbol) {
		members[symbol.name] = symbol
	}

	override fun resolve(name: String): Symbol? {
		return members[name]
				?: parentScope?.resolve(name)
				?: enclosingScope.resolve(name)
	}

	fun resolveMember(name: String): Symbol? {
		return members[name] ?: parentScope?.resolveMember(name)
	}

	override fun toString(): String {
		return "${javaClass.simpleName}(name=$name, memberSymbols=$members)"
	}
}

interface Scope {
	val name: String
	val enclosingScope: Scope?
	fun define(symbol: Symbol)
	fun resolve(name: String): Symbol?
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
