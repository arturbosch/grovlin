package io.gitlab.arturbosch.grovlin.compiler.java

import io.gitlab.arturbosch.grovlin.compiler.backend.CPackage
import io.gitlab.arturbosch.grovlin.compiler.java.inmemory.JavaStringCompiler
import java.io.File

/**
 * @author Artur Bosch
 */
private val compiler = JavaStringCompiler()

fun writeToDisk(file: File, cPackage: CPackage) {
	val javaFiles = cPackage.all()
			.map { it.javaFileName to it.unit.toString() }
			.toMap()

	val nameToBytes = compiler.compile(javaFiles)
	for ((fileName, content) in nameToBytes) {
		file.resolve(fileName + ".class").writeBytes(content)
	}
}

fun CPackage.interpret() {
	val javaFiles = all().map { it.javaFileName to it.unit.toString() }.toMap()
	val map = compiler.compile(javaFiles)
	compiler.run(main.fileName, map)
}
