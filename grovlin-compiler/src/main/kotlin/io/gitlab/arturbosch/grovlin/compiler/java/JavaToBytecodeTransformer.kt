package io.gitlab.arturbosch.grovlin.compiler.java

import io.gitlab.arturbosch.grovlin.compiler.java.inmemory.JavaStringCompiler
import java.io.File
import java.nio.file.Files

/**
 * @author Artur Bosch
 */
private val compiler = JavaStringCompiler()

fun CPackage.toFile(file: File) {
	val javaFiles = all().map { it.javaFileName to it.unit.toString() }.toMap()
	val map = compiler.compile(javaFiles)
	val path = file.toPath()
	map.forEach { Files.write(path.resolve(it.key + ".class"), it.value) }
}

fun CPackage.run() {
	val javaFiles = all().map { it.javaFileName to it.unit.toString() }.toMap()
	val map = compiler.compile(javaFiles)
	compiler.run(main.fileName, map)
}
