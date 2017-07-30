package io.gitlab.arturbosch.grovlin.ast

import com.oracle.truffle.api.frame.FrameDescriptor
import com.oracle.truffle.api.source.Source
import io.gitlab.arturbosch.grovlin.parser.parse

/**
 * @author Artur Bosch
 */
object Parser {

	fun grovlin(source: Source): GrovlinFile {
		val root = source.code.parse()

		if (root.isValid()) {
			val grovlinFileContext = root.root!!
			val (name, block, position) = grovlinFileContext.toAsT()
			val program = block?.statements?.find { it is Program }
					?: throw ProgramDeclarationMissing()
			return GrovlinFile(source.createSection(1, 1, source.code.length),
					FrameDescriptor(),
					GrovlinProgram(GrovlinBodyStatement(emptyList())))
		} else {
			throw InvalidGrovlinFile(root.errors)
		}
	}
}
