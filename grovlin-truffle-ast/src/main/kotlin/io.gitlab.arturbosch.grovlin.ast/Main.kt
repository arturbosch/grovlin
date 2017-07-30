package io.gitlab.arturbosch.grovlin.ast

import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.dsl.UnsupportedSpecializationException
import com.oracle.truffle.api.source.Source
import com.oracle.truffle.api.vm.PolyglotEngine
import java.io.File
import java.io.InputStream
import java.io.PrintStream


/**
 * @author Artur Bosch
 */
fun main(args: Array<String>) {
	val source = if (args.isEmpty()) {
		throw IllegalStateException("No parameters supplied!")
	} else {
		Source.newBuilder(File(args[0])).build()
	}

	executeSource(source, System.`in`, System.out)
}

fun executeSource(source: Source, `in`: InputStream, out: PrintStream) {
	out.println("== running on " + Truffle.getRuntime().name)

	val engine = PolyglotEngine.newBuilder().setIn(`in`).setOut(out).build()
	assert(engine.languages.containsKey(GrovlinLanguage.MIME_TYPE))

	try {
		val result = engine.eval(source)

		if (result == null) {
			throw GrovlinError("No program block defined in source file.")
		} else if (result.get() !== GrovlinNull) {
			out.println(result.get())
		}

	} catch (ex: Throwable) {
		/*
             * PolyglotEngine.eval wraps the actual exception in an IOException, so we have to
             * unwrap here.
             */
		val cause = ex.cause
		if (cause is UnsupportedSpecializationException) {
			out.println(cause)
		} else if (cause is GrovlinError) {
			out.println(cause.message)
		} else {
			/* Unexpected error, just print out the full stack trace for debugging purposes. */
			ex.printStackTrace(out)
		}
	}

	engine.dispose()
}
