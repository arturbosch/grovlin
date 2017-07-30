package io.gitlab.arturbosch.grovlin.ast

import com.oracle.truffle.api.CallTarget
import com.oracle.truffle.api.CompilerAsserts
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.TruffleLanguage
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter


/**
 * @author Artur Bosch
 */
@TruffleLanguage.Registration(name = "grovlin", version = "0.1.0", mimeType = arrayOf(GrovlinLanguage.MIME_TYPE))
object GrovlinLanguage : TruffleLanguage<GrovlinContext>() {

	const val MIME_TYPE = "application/x-gv"

	override fun parse(request: ParsingRequest): CallTarget {
		val grovlinFile = Parser.grovlin(request.source)
		return Truffle.getRuntime().createCallTarget(grovlinFile)
	}

	override fun isObjectOfLanguage(`object`: Any): Boolean {
		return when (`object`) {
			is GrovlinFunction, is GrovlinNull -> true
			else -> false
		}
	}

	override fun getLanguageGlobal(context: GrovlinContext) = context

	override fun createContext(env: Env): GrovlinContext {
		val `in` = BufferedReader(InputStreamReader(env.`in`()))
		val out = PrintWriter(env.out(), true)
		return GrovlinContext(env, `in`, out)
	}

	override fun findExportedSymbol(context: GrovlinContext, globalName: String, onlyExplicit: Boolean): Any {
		return context.lookup(globalName, true)
	}

	fun findContext(): GrovlinContext? {
		CompilerAsserts.neverPartOfCompilation()
		return super.findContext(super.createFindContextNode())
	}
}

object GrovlinNull
