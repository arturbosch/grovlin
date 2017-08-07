package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.DEFAULT_GROVLIN_FILE_NAME
import io.gitlab.arturbosch.grovlin.ast.ForStatement
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.ParameterDeclaration
import io.gitlab.arturbosch.grovlin.ast.ReturnStatement
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.identify
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import io.gitlab.arturbosch.grovlin.ast.operations.findByType
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

	@Test
	fun resolveObjects() {
		val grovlinFile = """
		object A {
			Int a = 5
			def stuff() {
				print(a)
			}
		}
		object B {
			def illegal() {
				print(a)
			}
		}
		""".asGrovlinFile().identify().resolve()

		val refA = grovlinFile.findObjectByName("A")
				?.findVariableReferencesByName("a")!![0]
		val refB = grovlinFile.findObjectByName("B")
				?.findVariableReferencesByName("a")!![0]

		Assertions.assertThat(refA.resolutionScope?.resolve("a")).isNotNull()
		Assertions.assertThat(refB.resolutionScope?.resolve("a")).isNull()
	}

	@Test
	fun resolveParametersInMethodsInTypesAndObjects() {
		val grovlinFile = """
			type A { def say() { println(5) } }
			object O as A {
				Int x = -1
				def number(Int z): Int {
					val x = 4
					for i : 1..10 {
						if i > 5 {
							return i + x + z
						}
					}
				}
			}
		""".asGrovlinFile().identify().resolve()

		val scope = grovlinFile.resolutionScope!!
		val typeA = scope.resolve("A")!!
		val typeAMethodSay = typeA.def?.findByType<MethodDeclaration>()
		val objectO = scope.resolve("O")!!
		val methodNumber = objectO.def?.findByType<MethodDeclaration>()
		val numberScope = methodNumber?.findVariableByName("x")?.resolutionScope
		val intZ = numberScope?.resolve("z")
		val returnExpr = methodNumber?.findByType<ReturnStatement>()?.expression
		val vars = returnExpr?.collectByType<VarReference>()
		val propertyI = vars?.find { it.varName == "i" }
		val localX = vars?.find { it.varName == "x" }
		val parameterZ = vars?.find { it.varName == "z" }

		Assertions.assertThat(typeAMethodSay?.resolutionScope).isInstanceOf(ClassSymbol::class.java)
		Assertions.assertThat(intZ?.def).isInstanceOf(ParameterDeclaration::class.java)

		val definitionOfPropertyI = propertyI?.resolutionScope?.resolve("i")?.def
		val definitionOfLocalX = localX?.resolutionScope?.resolve("x")?.def
		val definitionOfParameterZ = parameterZ?.resolutionScope?.resolve("z")?.def

		Assertions.assertThat(definitionOfPropertyI).isInstanceOf(VarDeclaration::class.java)
		Assertions.assertThat(definitionOfPropertyI?.parent).isInstanceOf(ForStatement::class.java)

		Assertions.assertThat(definitionOfLocalX).isInstanceOf(VarDeclaration::class.java)
		Assertions.assertThat(definitionOfLocalX?.parent?.parent).isInstanceOf(MethodDeclaration::class.java)

		Assertions.assertThat(definitionOfParameterZ).isInstanceOf(ParameterDeclaration::class.java)
		Assertions.assertThat(definitionOfParameterZ?.parent).isInstanceOf(MethodDeclaration::class.java)
	}
}
