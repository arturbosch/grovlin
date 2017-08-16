package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.ReturnStatement
import io.gitlab.arturbosch.grovlin.ast.VoidType
import io.gitlab.arturbosch.grovlin.ast.visitors.TreeBaseVisitor

/**
 * @author Artur Bosch
 */
class ReturnEvaluationVisitor(methodDeclaration: MethodDeclaration) : TreeBaseVisitor<Any>() {

	val errors: MutableList<SemanticError> = mutableListOf()

	private val returnType = methodDeclaration.type
	private var returnStmt: ReturnStatement? = null
	private var counter: Int = 0

	init {
		visit(methodDeclaration, Unit)
	}

	override fun visit(methodDeclaration: MethodDeclaration, data: Any) {
		super.visit(methodDeclaration, data)
		if (counter > 1) {
			errors.add(SingleReturnAllowed(methodDeclaration))
		}
		if (methodDeclaration.type != VoidType) {
			methodDeclaration.block?.statements?.lastOrNull()?.let {
				if (it !is ReturnStatement) {
					errors.add(ReturnMustBeLast(methodDeclaration, it.position))
				}
			}
		}
	}

	override fun visit(returnStatement: ReturnStatement, data: Any) {
		returnStmt = returnStatement
		counter++
		val evaluationType = returnStatement.evaluationType
		if (returnType != evaluationType) {
			errors.add(IncompatibleReturnType(evaluationType, returnType, returnStatement.position))
		}
	}
}

