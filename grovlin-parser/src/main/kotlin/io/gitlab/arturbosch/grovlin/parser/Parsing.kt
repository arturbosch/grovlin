package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.GrovlinLexer
import io.gitlab.arturbosch.grovlin.GrovlinParser
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import java.io.Reader
import java.io.StringReader
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
object Parsing

private fun lexer(code: String): GrovlinLexer = GrovlinLexer(ANTLRInputStream(StringReader(code)))

private fun lexer(reader: Reader): GrovlinLexer = GrovlinLexer(ANTLRInputStream(reader))

fun String.parse(): RawParsingResult {

	val grovlinParser = GrovlinParser(CommonTokenStream(lexer(this)))
	val syntaxErrorListener = SyntaxErrorListener()
	grovlinParser.addErrorListener(syntaxErrorListener)
	val grovlinFile = grovlinParser.grovlinFile()

	return RawParsingResult(grovlinFile, null, syntaxErrorListener.errors)
}

fun Path.parse(): RawParsingResult {
	val byteChannel = FileChannel.open(this)
	val reader = Channels.newReader(byteChannel, Charsets.UTF_8.displayName())

	val grovlinParser = GrovlinParser(CommonTokenStream(lexer(reader)))
	val syntaxErrorListener = SyntaxErrorListener()
	grovlinParser.addErrorListener(SyntaxErrorListener())
	val grovlinFile = grovlinParser.grovlinFile()

	return RawParsingResult(grovlinFile, this, syntaxErrorListener.errors)
}

fun parseFromResource(resourceName: String): GrovlinParser.GrovlinFileContext {
	val errors = mutableListOf<SyntaxError>()

	val grovlinParser = GrovlinParser(CommonTokenStream(lexerFromResource(resourceName)))
	grovlinParser.addErrorListener(SyntaxErrorListener())
	val grovlinFile = grovlinParser.grovlinFile()

	errors.forEach(::println)
	return grovlinFile
}

fun lexerFromResource(resourceName: String) =
		GrovlinLexer(ANTLRInputStream(Parsing.javaClass.getResourceAsStream("/$resourceName")))

fun String.tokenize(): List<String> = tokens(this)

fun tokens(code: String): List<String> {
	val lexer = lexer(code)
	val vocabulary = lexer.vocabulary
	return lexer.allTokens.filter { it.type != GrovlinLexer.WS }
			.map { vocabulary.getSymbolicName(it.type) }
}

class SyntaxErrorListener : BaseErrorListener() {

	val errors: MutableList<SyntaxError> = mutableListOf()

	override fun syntaxError(recognizer: Recognizer<*, *>, offendingSymbol: Any?,
							 line: Int, charPositionInLine: Int, msg: String, e: RecognitionException?) {
		errors.add(SyntaxError(msg, CodePoint(line, charPositionInLine)))
	}
}
