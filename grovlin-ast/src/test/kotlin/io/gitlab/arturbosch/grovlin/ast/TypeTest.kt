package io.gitlab.arturbosch.grovlin.ast

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.present
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import org.junit.Test

/**
 * @author Artur Bosch
 */
class TypeTest {

	@Test
	fun parseTypeDeclaration() {
		val grovlinFile = "type Node {}".asGrovlinFile()

		assertThat(grovlinFile.findTypeByName("Node"), present())
	}

	@Test
	fun parseTypeCreation() {
		val grovlinFile = parseFromTestResource("TypesAndObjects.grovlin")

		assertThat(grovlinFile.findObjectByName("BinaryTree"), present())
		assertThat(grovlinFile.collectByType<TypeDeclaration>(), hasSize(equalTo(3)))
	}
}