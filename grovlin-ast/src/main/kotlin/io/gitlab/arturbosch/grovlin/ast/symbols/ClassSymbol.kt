package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.NodeWithOverride
import java.util.HashMap

/**
 * @author Artur Bosch
 */
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

	private fun getMemberSymbols() = members.values
	private fun getInheritedMemberSymbolsNeedingOverride(): MutableMap<String, Symbol> {
		val symbols = HashMap<String, Symbol>()
		for (traitScope in traitScopes) {
			val inheritedNeedingOverride = traitScope.getInheritedMemberSymbolsNeedingOverride()
			for ((name, symbol) in inheritedNeedingOverride) {
				symbols.put(name, symbol)
			}
			for (symbol in traitScope.getMemberSymbols()) {
				if (!symbol.needOverride && symbol.name in symbols) {
					symbols.remove(symbol.name)
				}
				if (symbol.needOverride) {
					symbols.put(symbol.name, symbol)
				}
			}
		}
		return symbols
	}

	fun checkOverridingTraitMembers(grovlinFile: GrovlinFile) {
		val symbolsNeedingOverride = getInheritedMemberSymbolsNeedingOverride()
		val membersNeedingOverride = getMemberSymbols()
		for (symbol in membersNeedingOverride) {
			val symbolName = symbol.name
			val declaration = symbol.def
			val nodeWithOverride = declaration is NodeWithOverride
			val hasOverride = (declaration as? NodeWithOverride)?.hasOverride
			if (symbolName in symbolsNeedingOverride) {
				val inheritedSymbol = symbolsNeedingOverride.remove(symbolName)
				if (nodeWithOverride && symbol.type != inheritedSymbol?.type) {
					grovlinFile.addError(IncompatibleOverrideType(
							symbol.type, inheritedSymbol?.type, symbol.def?.position))
				}
				if (nodeWithOverride && hasOverride == false) {
					grovlinFile.addError(MissingOverrideKeyword(symbolName, symbol.def?.position))
				}
			} else {
				if (nodeWithOverride && hasOverride == true) {
					grovlinFile.addError(OverridesNothing(symbolName, symbol.def?.position))
				}
			}
		}
	}

	fun checkOverridingObjectMembers(grovlinFile: GrovlinFile) {
		val symbolsNeedingOverride = getInheritedMemberSymbolsNeedingOverride()
		val membersNeedingOverride = getMemberSymbols()

		for (symbol in membersNeedingOverride) {
			val symbolName = symbol.name
			val declaration = symbol.def
			val nodeWithOverride = declaration is NodeWithOverride
			val hasOverride = (declaration as? NodeWithOverride)?.hasOverride
			if (symbolName in symbolsNeedingOverride) {
				val inheritedSymbol = symbolsNeedingOverride.remove(symbolName)
				if (nodeWithOverride && symbol.type != inheritedSymbol?.type) {
					grovlinFile.addError(IncompatibleOverrideType(
							symbol.type, inheritedSymbol?.type, symbol.def?.position))
				}
				if (nodeWithOverride && hasOverride == false) {
					grovlinFile.addError(MissingOverrideKeyword(symbolName, symbol.def?.position))
				}
			} else {
				if (nodeWithOverride && hasOverride == true) {
					grovlinFile.addError(OverridesNothing(symbolName, symbol.def?.position))
				}
			}
		}
		symbolsNeedingOverride.values.forEach {
			grovlinFile.addError(MemberNotOverridden(it.name, name, def?.position))
		}
	}

	override fun getParentScope(): Scope? = parentScope ?: enclosingScope

	override fun toString(): String {
		return "${javaClass.simpleName}(name=$name, memberSymbols=$members)"
	}
}
