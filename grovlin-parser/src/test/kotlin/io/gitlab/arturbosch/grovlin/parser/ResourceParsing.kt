package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.GrovlinLexer
import io.gitlab.arturbosch.grovlin.GrovlinParser
import io.gitlab.arturbosch.grovlin.parser.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.parser.ast.toAsT
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

/**
 * @author Artur Bosch
 */
fun parse(resourceName: String): GrovlinFile {
	return GrovlinParser(CommonTokenStream(lexerFromResource(resourceName))).apply {
		addErrorListener(errorListener)
	}.grovlinFile().toAsT()
}

fun lexerFromResource(resourceName: String) =
		GrovlinLexer(ANTLRInputStream(Parsing.javaClass.getResourceAsStream("/$resourceName")))
