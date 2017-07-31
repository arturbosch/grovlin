package io.gitlab.arturbosch.grovlin.ast.visitors

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.toAsT
import io.gitlab.arturbosch.grovlin.parser.parse
import org.junit.Test

/**
 * @author Artur Bosch
 */
class VoidTreeVisitorTest {

	private val code =
			"""program {
				var x = 5
				var y = 4
				var z = 5 * 4
				print(z)
			}
			"""

	private val file = code.parse().root!!.toAsT()

	private val variableCollector = object : TreeVisitor() {

		val names = mutableListOf<String>()

		override fun visit(varDeclaration: VarDeclaration, data: Any) {
			names.add(varDeclaration.name)
		}
	}

	@Test
	fun findAllVariableNames() {
		variableCollector.visit(file, Unit)
		val names = variableCollector.names

		assertThat(names, hasSize(equalTo(3)))
		assertThat(names, hasElement("x"))
		assertThat(names, hasElement("y"))
		assertThat(names, hasElement("z"))
	}
}
