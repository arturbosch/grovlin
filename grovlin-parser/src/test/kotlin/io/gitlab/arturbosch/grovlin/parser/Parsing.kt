package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.GrovlinLexer
import io.gitlab.arturbosch.grovlin.GrovlinParser
import io.gitlab.arturbosch.grovlin.parser.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.parser.ast.toAsT
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.StringReader

/**
 * @author Artur Bosch
 */
object Parsing

fun lexer(code: String): GrovlinLexer = GrovlinLexer(ANTLRInputStream(StringReader(code)))

fun lexerFromResource(resourceName: String) =
		GrovlinLexer(ANTLRInputStream(Parsing.javaClass.getResourceAsStream("/$resourceName")))

fun parse(resourceName: String): GrovlinFile {
	return GrovlinParser(CommonTokenStream(lexerFromResource(resourceName))).grovlinFile().toAsT()
}

fun tokens(code: String): List<String> {
	val lexer = lexer(code)
	val vocabulary = lexer.vocabulary
	return lexer.allTokens.map { vocabulary.getSymbolicName(it.type) }
}
