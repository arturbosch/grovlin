package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.GrovlinLexer
import io.gitlab.arturbosch.grovlin.GrovlinParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import java.io.StringReader
import java.nio.file.Files
import java.nio.file.Path


/**
 * @author Artur Bosch
 */
object Parsing

fun lexer(code: String): GrovlinLexer = GrovlinLexer(ANTLRInputStream(StringReader(code)))

fun Path.parse(): RawParsingResult {
	val code = String(Files.readAllBytes(this))
	val errors = mutableListOf<Error>()

	val grovlinParser = GrovlinParser(CommonTokenStream(lexer(code)))
	grovlinParser.addErrorListener(SyntaxErrorListener(errors))
	val grovlinFile = grovlinParser.grovlinFile()

	return RawParsingResult(grovlinFile, this, errors)
}

fun parseFromResource(resourceName: String): GrovlinParser.GrovlinFileContext {
	val errors = mutableListOf<Error>()

	val grovlinParser = GrovlinParser(CommonTokenStream(lexerFromResource(resourceName)))
	grovlinParser.addErrorListener(SyntaxErrorListener(errors))
	val grovlinFile = grovlinParser.grovlinFile()

	errors.forEach(::println)
	return grovlinFile
}

fun lexerFromResource(resourceName: String) =
		GrovlinLexer(ANTLRInputStream(Parsing.javaClass.getResourceAsStream("/$resourceName")))

fun tokens(code: String): List<String> {
	val lexer = lexer(code)
	val vocabulary = lexer.vocabulary
	return lexer.allTokens.filter { it.type != GrovlinLexer.WS }
			.map { vocabulary.getSymbolicName(it.type) }
}

class SyntaxErrorListener(val errors: MutableList<Error>) : BaseErrorListener() {
	override fun syntaxError(recognizer: Recognizer<*, *>, offendingSymbol: Any,
							 line: Int, charPositionInLine: Int, msg: String, e: RecognitionException) {
		errors.add(SyntaxError(msg, CodePoint(line, charPositionInLine)))
	}
}
