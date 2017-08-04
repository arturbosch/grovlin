package io.gitlab.arturbosch.grovlin.ast

import org.junit.Test

/**
 * @author Artur Bosch
 */
class PositionsTest {

	@Test
	fun `positions are parsed`() {
		val grovlinFile = parseFromTestResource("example.grovlin")
		grovlinFile.processChildren {
			assert(it.position != null) {
				"${it.javaClass.simpleName} has no positions, parent was ${it.parent?.javaClass?.simpleName}"
			}
			println(it.javaClass.simpleName)
		}
	}

	fun AstNode.processChildren(operation: (AstNode) -> Unit) {
		operation(this)
		children.forEach { it.processChildren(operation) }
	}

}
