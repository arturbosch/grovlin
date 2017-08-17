package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.Declaration
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration

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
