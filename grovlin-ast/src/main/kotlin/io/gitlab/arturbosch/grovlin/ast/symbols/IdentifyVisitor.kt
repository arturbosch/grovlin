package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.BlockStatement
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectDeclaration
import io.gitlab.arturbosch.grovlin.ast.ParameterDeclaration
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.ThisReference
import io.gitlab.arturbosch.grovlin.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.VariableDeclaration
import io.gitlab.arturbosch.grovlin.ast.visitors.TreeBaseVisitor
import org.antlr.v4.misc.MutableInt
import java.util.HashMap

/**
 * @author Artur Bosch
 */
class IdentifyVisitor(val grovlinFile: GrovlinFile) : TreeBaseVisitor<Any>() {

	val fileScope = FileScope(grovlinFile.name)
	private var currentScope: Scope = fileScope

	private val methodSignatureCache: MutableMap<Scope, MutableMap<String, MutableInt>> = HashMap()
	private val methodCache: MutableList<MethodDeclaration> = mutableListOf()

	override fun visit(file: GrovlinFile, data: Any) {
		super.visit(file, data)
		// context results
		methodRedeclarationCheck()
	}

	private fun methodRedeclarationCheck() {
		for (scopeToMethods in methodSignatureCache.values) {
			for ((signature, value) in scopeToMethods) {
				if (value.v > 1) {
					val sameName = methodCache.filter { it.parameterSignature == signature }
					if (sameName.size > 1) {
						grovlinFile.addError(createRedeclarationError(signature, sameName))
					}
				}
			}
		}
	}

	private fun createRedeclarationError(signature: String, sameDecls: List<MethodDeclaration>): SemanticError {
		return SemanticError("Method redeclaration with signature '$signature' on " +
				sameDecls.joinToString(", ") { it.position.toString() }, sameDecls[0].position?.start)
	}

	// Identify

	override fun visit(varReference: VarReference, data: Any) {
		varReference.resolutionScope = currentScope
	}

	override fun visit(thisReference: ThisReference, data: Any) {
		thisReference.resolutionScope = currentScope
	}

	override fun visit(varDeclaration: VarDeclaration, data: Any) {
		identifyVariable(varDeclaration)
		super.visit(varDeclaration, data)
	}

	override fun visit(propertyDeclaration: PropertyDeclaration, data: Any) {
		identifyVariable(propertyDeclaration)
		super.visit(propertyDeclaration, data)
	}

	override fun visit(parameterDeclaration: ParameterDeclaration, data: Any) {
		identifyVariable(parameterDeclaration)
		super.visit(parameterDeclaration, data)
	}

	private fun identifyVariable(variableDeclaration: VariableDeclaration) {
		val variableSymbol = VariableSymbol(variableDeclaration.name)
		variableSymbol.def = variableDeclaration
		variableSymbol.scope = currentScope
		currentScope.define(variableSymbol)
		variableDeclaration.resolutionScope = currentScope
		variableDeclaration.symbol = variableSymbol
	}

	override fun visit(methodDeclaration: MethodDeclaration, data: Any) {
		// cache method parameter signature for redeclaration check
		val currentCache = methodSignatureCache.getOrPut(currentScope) { HashMap() }
		currentCache.compute(methodDeclaration.parameterSignature,
				{ _, value -> value?.apply { v += 1 } ?: MutableInt(1) })
		methodCache.add(methodDeclaration)
		// method symbol identify
		val methodSymbol = MethodSymbol(methodDeclaration.name, methodDeclaration.type, currentScope)
		currentScope.define(methodSymbol)
		methodSymbol.def = methodDeclaration
		methodSymbol.scope = currentScope
		methodDeclaration.resolutionScope = currentScope
		methodDeclaration.symbol = methodSymbol
		currentScope = methodSymbol.parameterScope
		super.visit(methodDeclaration, data)
		currentScope = currentScope.enclosingScope ?: assertEnclosingScope()
	}

	override fun visit(objectDeclaration: ObjectDeclaration, data: Any) {
		val classSymbol = ClassSymbol(objectDeclaration.name, objectDeclaration.objectType, currentScope)
		currentScope.define(classSymbol)
		classSymbol.def = objectDeclaration
		classSymbol.scope = currentScope
		objectDeclaration.resolutionScope = currentScope
		objectDeclaration.symbol = classSymbol
		currentScope = classSymbol
		super.visit(objectDeclaration, data)
		currentScope = currentScope.enclosingScope ?: assertEnclosingScope()
	}

	override fun visit(typeDeclaration: TypeDeclaration, data: Any) {
		val classSymbol = ClassSymbol(typeDeclaration.name, typeDeclaration.typeType, currentScope)
		currentScope.define(classSymbol)
		classSymbol.def = typeDeclaration
		classSymbol.scope = currentScope
		typeDeclaration.resolutionScope = currentScope
		typeDeclaration.symbol = classSymbol
		currentScope = classSymbol
		super.visit(typeDeclaration, data)
		currentScope = currentScope.enclosingScope ?: assertEnclosingScope()
	}

	override fun visit(blockStatement: BlockStatement, data: Any) {
		val notClassOrFileScope = currentScope !is FileScope && currentScope !is ClassSymbol
		if (notClassOrFileScope) {
			val localScope = LocalScope("<block>", currentScope)
			currentScope = localScope
		}
		super.visit(blockStatement, data)
		if (notClassOrFileScope) {
			currentScope = currentScope.enclosingScope ?: assertEnclosingScope()
		}
	}

	private fun assertEnclosingScope(): Scope {
		throw AssertionError("Unexpected null enclosing scope of $currentScope")
	}
}
