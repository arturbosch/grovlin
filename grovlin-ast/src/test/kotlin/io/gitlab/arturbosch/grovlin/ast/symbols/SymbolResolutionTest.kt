package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.DEFAULT_GROVLIN_FILE_NAME
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.identify
import io.gitlab.arturbosch.grovlin.ast.resolve
import org.assertj.core.api.Assertions
import org.junit.Test

/**
 * @author Artur Bosch
 */
class SymbolResolutionTest {

	@Test
	fun resolveInFileScopeOnly() {
		val grovlinFile = """
			var x = 1
			var y = 2
			println(x + y)
		"""
				.asGrovlinFile()
				.identify()
				.resolve()

		val scope = grovlinFile.resolutionScope as FileScope
		val xSym = scope.resolve("x")
		val ySym = scope.resolve("y")

		Assertions.assertThat(scope.name).isEqualTo("<file:$DEFAULT_GROVLIN_FILE_NAME>")
		Assertions.assertThat(xSym?.scope).isEqualTo(scope)
		Assertions.assertThat((xSym?.def as VarDeclaration).name).isEqualTo("x")
		Assertions.assertThat(ySym?.scope).isEqualTo(scope)
		Assertions.assertThat((ySym?.def as VarDeclaration).name).isEqualTo("y")
	}

	@Test
	fun resolveNestedMethods() {
		// given
		val grovlinFile = """
			var a = 5
			def main() {
				var b = true
				if (b) {
					println(b)
				}
				println(a)
			}
			def abc() {
				println(a)
			}
		""".asGrovlinFile().identify().resolve()

		// a is in file scope, b not
		val scope = grovlinFile.resolutionScope as FileScope
		val aSym = scope.resolve("a")
		val bInFileScope = scope.resolve("b")

		Assertions.assertThat(aSym?.scope).isEqualTo(scope)
		Assertions.assertThat(bInFileScope?.scope).isNull()

		// used b found in var decl, a in print resolves to file scope
		val bVars = grovlinFile.findVariableReferencesByName("b")
		val aVar = grovlinFile.findVariableReferencesByName("a")[0]
		Assertions.assertThat(bVars).hasSize(2)
		Assertions.assertThat(bVars[0].resolutionScope?.name).isEqualTo("<block>")
		Assertions.assertThat(bVars[1].resolutionScope?.name).isEqualTo("<block>")

		val aVarSym = aVar.resolutionScope?.resolve(aVar.varName)
		Assertions.assertThat(aVarSym?.def).isInstanceOf(VarDeclaration::class.java)
		Assertions.assertThat(aVarSym?.def?.resolutionScope).isInstanceOf(FileScope::class.java)
	}
}
