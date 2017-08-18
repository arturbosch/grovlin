package io.gitlab.arturbosch.grovlin.compiler

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.present
import io.gitlab.arturbosch.grovlin.ast.operations.asString
import io.gitlab.arturbosch.grovlin.compiler.backend.asJavaFile
import io.gitlab.arturbosch.grovlin.compiler.java.interpret
import org.junit.Test

/**
 * @author Artur Bosch
 */
class CompileProgramWithTypesAndObjectsTest {

	@Test
	fun threeTypesOneObject() {
		val grovlinFile = parseFromTestResource("TypesAndObjects.grovlin")
		println(grovlinFile.asString())
		val cPackage = grovlinFile.asJavaFile()
		val cUnit = cPackage.main
		val clazz = cUnit.mainClass
		println(clazz.toString())
		cPackage.cus.forEach { print(it.unit) }
		assertThat(clazz, present())
		assertThat(cPackage.cus, hasSize(equalTo(5)))
	}


	@Test
	fun correctResolutionOfPropertyGetter() {
		val grovlinFile = """
			type Box { Int data }
			object BoxImpl as Box { override Int data }
			def main(String args) { print(BoxImpl().data) }
		""".asGrovlinFile()
		println(grovlinFile.asString())
		val java = grovlinFile.asJavaFile()
		java.cus.forEach(::println)
		println(java.main)
	}

	@Test
	fun memberAccessChains() {
		"""
		trait T {
			def sayHello(): String {
				return "Hello"
			}
		}
		object A as T
		object B {
			A a = A()
		}
		object O {
			B b = B()
		}
		def main(String args) {
			val x = O().b.a.sayHello()
			println(x)
		}
	""".asGrovlinFile().asJavaFile().interpret()
	}
}
