package io.gitlab.arturbosch.grovlin.ast.builtins

import io.gitlab.arturbosch.grovlin.ast.BlockStatement
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.ParameterDeclaration
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.VoidType

/**
 * @author Artur Bosch
 */
class MainDeclaration(blockStatement: BlockStatement?,
					  type: Type = VoidType,
					  parameters: MutableList<ParameterDeclaration>)
	: MethodDeclaration("main", blockStatement, type, parameters)

const val MAIN_METHOD_NAME = "main"
