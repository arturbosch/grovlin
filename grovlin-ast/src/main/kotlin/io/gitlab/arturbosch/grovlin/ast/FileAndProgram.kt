package io.gitlab.arturbosch.grovlin.ast

class Program(override val name: String,
			  override val block: BlockStatement?)
	: Statement(), NodeWithBlock, TopLevelDeclarable, NodeWithName

/**
 * @author Artur Bosch
 */
