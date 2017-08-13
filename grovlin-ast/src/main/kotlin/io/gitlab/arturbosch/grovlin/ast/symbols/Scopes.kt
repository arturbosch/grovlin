package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.Declaration
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.VoidType
import io.gitlab.arturbosch.grovlin.ast.builtins.StringType
import java.util.HashMap
import java.util.HashSet

/**
 * @author Artur Bosch
 */
interface Scope {

	val name: String
	val enclosingScope: Scope?

	val declarations: MutableSet<String>
	val declarationsMap: MutableMap<String, Declaration>

	fun define(symbol: Symbol)
	fun resolve(name: String): Symbol?
	fun getParentScope(): Scope? = enclosingScope

	fun declare(decl: Declaration, grovlinFile: GrovlinFile) {
		val declName = decl.name
		redeclareInternal(declName, decl)?.let { grovlinFile.addError(it) }
	}

	fun declare(decl: MethodDeclaration, grovlinFile: GrovlinFile) {
		val declName = decl.parameterSignature
		redeclareInternal(declName, decl)?.let { grovlinFile.addError(it) }
	}

	private fun redeclareInternal(declName: String, decl: Declaration): RedeclarationError? {
		if (declarations.contains(declName)) {
			val otherPos = declarationsMap[declName]?.position
			return RedeclarationError(declName, decl.position, otherPos)
		}
		declarations.add(declName)
		declarationsMap.put(declName, decl)
		return null
	}

	fun getEnclosingClass(): Symbol? {
		var current: Scope? = this
		while (current != null) {
			if (current is ClassSymbol) return current
			current = current.getParentScope()
		}
		return null
	}
}

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
