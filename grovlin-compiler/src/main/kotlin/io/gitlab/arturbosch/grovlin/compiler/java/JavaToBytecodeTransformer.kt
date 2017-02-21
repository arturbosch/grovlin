package io.gitlab.arturbosch.grovlin.compiler.java

import java.io.File
import java.nio.file.Files
import javax.tools.JavaCompiler
import javax.tools.StandardJavaFileManager
import javax.tools.StandardLocation
import javax.tools.ToolProvider

/**
 * @author Artur Bosch
 */
object JavaParserCompiler {

	val compiler: JavaCompiler = ToolProvider.getSystemJavaCompiler()
	val fileManager: StandardJavaFileManager = compiler.getStandardFileManager(null, null, null)

	fun compile(classOutput: File, vararg javaFiles: File) {
		val objects = fileManager.getJavaFileObjects(*javaFiles)
		fileManager.setLocation(StandardLocation.CLASS_OUTPUT, listOf(classOutput))
		compiler.getTask(null, fileManager, null, null, null, objects).call()
	}
}

fun CUnit.toFile(file: File, debugJavaFile: File = tempJavaFile(fileName)) {
	if (!debugJavaFile.exists()) debugJavaFile.createNewFile()
	debugJavaFile.writeText(unit.toString())
	JavaParserCompiler.compile(file, debugJavaFile)
}

private fun tempJavaFile(fileName: String): File = Files.createTempDirectory("grovlin").let {
	Files.createFile(it.resolve(fileName + ".java"))
}.toFile()
