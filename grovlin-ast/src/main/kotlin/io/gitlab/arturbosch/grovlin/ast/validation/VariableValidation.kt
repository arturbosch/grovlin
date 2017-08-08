package io.gitlab.arturbosch.grovlin.ast.validation

import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.Program
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.isBefore
import io.gitlab.arturbosch.grovlin.ast.operations.processNodesOfType

/**
 * @author Artur Bosch
 */
fun Program.validateVariables(errors: MutableList<SemanticError>) {
	val varsByName = mutableMapOf<String, VarDeclaration>()
	noDuplicateVariables(errors, varsByName)
	noReferencesBeforeVariableDeclarations(errors, varsByName)
	noAssignmentsBeforeVariableDeclaration(errors, varsByName)
}

private fun Program.noAssignmentsBeforeVariableDeclaration(errors: MutableList<SemanticError>, varsByName: MutableMap<String, VarDeclaration>) {
	processNodesOfType<Assignment> {
		if (!varsByName.containsKey(it.varName)) {
			errors.add(SemanticError("There is no variable named '${it.varName}'", it.position!!.start))
		} else if (it.isBefore(varsByName[it.varName]!!)) {
			errors.add(SemanticError("You cannot refer to variable '${it.varName}' before its declaration", it.position!!.start))
		}
	}
}

private fun Program.noReferencesBeforeVariableDeclarations(errors: MutableList<SemanticError>, varsByName: MutableMap<String, VarDeclaration>) {
	processNodesOfType<VarReference> {
		if (!varsByName.containsKey(it.reference.name)) {
			errors.add(SemanticError("There is no variable named '${it.reference.name}'", it.position!!.start))
		} else if (it.isBefore(varsByName[it.reference.name]!!)) {
			errors.add(SemanticError("You cannot refer to variable '${it.reference.name}' before its declaration", it.position!!.start))
		}
	}
}

private fun Program.noDuplicateVariables(errors: MutableList<SemanticError>, varsByName: MutableMap<String, VarDeclaration>) {
	processNodesOfType<VarDeclaration> {
		if (varsByName.containsKey(it.name)) {
			errors.add(SemanticError("A variable named '${it.name}' has been already declared at ${varsByName[it.name]!!.position!!.start}",
					it.position!!.start))
		} else {
			varsByName[it.name] = it
		}
	}
}
