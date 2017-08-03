package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.GrovlinParser
import java.util.ArrayList

/**
 * @author Artur Bosch
 */
class GrovlinFile(override var name: String,
				  override var block: BlockStatement?)
	: Node(), NodeWithBlock, NodeWithName

const val DEFAULT_GROVLIN_FILE_NAME = "Program"

fun GrovlinParser.GrovlinFileContext.toAsT(fileName: String = DEFAULT_GROVLIN_FILE_NAME): GrovlinFile {
	val statements = statements().statement().mapTo(ArrayList()) { it.toAst(fileName) }
	val blockStatement = if (statements.isNotEmpty()) {
		BlockStatement(statements).apply { position = toPosition() }
	} else null
	return GrovlinFile(fileName, blockStatement).apply { position = toPosition() }
}
