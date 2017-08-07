package io.gitlab.arturbosch.grovlin.ast.visitors

import io.gitlab.arturbosch.grovlin.GrovlinParser
import io.gitlab.arturbosch.grovlin.ast.DEFAULT_GROVLIN_FILE_NAME
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.toPosition
import io.gitlab.arturbosch.grovlin.ast.visitors.antlr.AntlrStatementVisitor

/**
 * @author Artur Bosch
 */
fun GrovlinParser.GrovlinFileContext?.asGrovlinFile(): GrovlinFile {
	this ?: throw AssertionError("Grovlin file context must not be null!")
	val visitor = AntlrStatementVisitor()
	val block = visitor.visitStatements(this.statements(), start, stop)
	val grovlinFile = GrovlinFile(DEFAULT_GROVLIN_FILE_NAME, block)
	grovlinFile.position = this.toPosition()
	block?.let { grovlinFile.children = listOf(it) }
	block?.parent = grovlinFile
	return grovlinFile
}
