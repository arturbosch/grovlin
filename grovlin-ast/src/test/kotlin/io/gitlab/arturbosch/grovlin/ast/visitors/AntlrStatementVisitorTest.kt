package io.gitlab.arturbosch.grovlin.ast.visitors

import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.asString
import io.gitlab.arturbosch.grovlin.parser.parse
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

		println(grovlinFile.asString())
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
//		val grovlinFile = root?.toAsT()
		println(grovlinFile?.asString())
	}
}
