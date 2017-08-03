package io.gitlab.arturbosch.grovlin.ast.visitors

import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.Print
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.findByType
import io.gitlab.arturbosch.grovlin.parser.parse
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class AntlrStatementVisitorTest {

	@Test
	fun callExpressions() {
		val grovlinFile = """
			finish(1)
			print(returny())
		""".asGrovlinFile()

		val statements = grovlinFile.topLevelStatements()
		Assertions.assertThat(statements).hasSize(2)
		Assertions.assertThat(grovlinFile.findByType<CallExpression>()).isNotNull()
		Assertions.assertThat(grovlinFile.findByType<Print>()).isNotNull()
	}

	@Test
	fun statements() {
		val root = """
		def main(String args) {
			var x = 5
			var y = 2
			if 5 < 10 && 10 >= x {
				while x > 0 {
					y = y * x
					x = x - 1
				}
			} else {
				finish(1)
				print(returny())
			}
			for c : args {
				print(i)
			}
			for i : 0..10 {
				print(i)
			}
			finish(y)
		}
		def returny(): Int {
			return 5
		}
		def finish(Int number) {
			print(number)
		}
		""".parse().root

		val grovlinFile = root?.asGrovlinFile()

		Assertions.assertThat(grovlinFile?.topLevelStatements()).hasSize(3)
		Assertions.assertThat(grovlinFile?.findMethodByName("returny")?.type).isEqualTo(IntType)
	}
}
