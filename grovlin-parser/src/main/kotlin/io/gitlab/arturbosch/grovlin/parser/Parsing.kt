package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.GrovlinLexer
import io.gitlab.arturbosch.grovlin.GrovlinParser
import io.gitlab.arturbosch.grovlin.parser.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.parser.ast.toAsT
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import java.io.StringReader


/**
 * @author Artur Bosch
 */
object Parsing

fun lexer(code: String): GrovlinLexer = GrovlinLexer(ANTLRInputStream(StringReader(code)))

fun lexerFromResource(resourceName: String) =
		GrovlinLexer(ANTLRInputStream(Parsing.javaClass.getResourceAsStream("/$resourceName")))

fun parse(resourceName: String): GrovlinFile {
	return GrovlinParser(CommonTokenStream(lexerFromResource(resourceName))).apply {
		addErrorListener(errorListener)
	}.grovlinFile().toAsT()
}

fun tokens(code: String): List<String> {
	val lexer = lexer(code)
	val vocabulary = lexer.vocabulary
	return lexer.allTokens.filter { it.type != GrovlinLexer.WS }
			.map { vocabulary.getSymbolicName(it.type) }
}

private val errorListener = object : BaseErrorListener() {
	override fun syntaxError(recognizer: Recognizer<*, *>, offendingSymbol: Any,
							 line: Int, charPositionInLine: Int, msg: String, e: RecognitionException) {
		throw IllegalStateException("failed to parse at L $line, C $charPositionInLine due to $msg", e)
	}
}