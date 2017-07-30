package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */

class GrovlinFile(override val name: String,
				  override val block: BlockStatement?)
	: Node(), NodeWithBlock, NodeWithName

class Program(override val name: String,
			  override val block: BlockStatement?)
	: Statement(), NodeWithBlock, TopLevelDeclarable, NodeWithName

fun GrovlinFile.findTypeByName(name: String): TypeDeclaration? = block?.statements
		?.filterIsInstance<TypeDeclaration>()
		?.find { it.name == name }

fun GrovlinFile.findObjectByName(name: String): ObjectDeclaration? = block?.statements
		?.filterIsInstance<ObjectDeclaration>()?.find { it.name == name }
