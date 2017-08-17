package io.gitlab.arturbosch.grovlin.ast.symbols

/**
 * @author Artur Bosch
 */
class LocalScope(override val name: String,
				 override val enclosingScope: Scope) : BaseScope()
