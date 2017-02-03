package io.gitlab.arturbosch.grovlin.compiler

import com.github.javaparser.ast.CompilationUnit
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

fun CompilationUnit.toFile(file: File, debugJavaFile: File = tempJavaFile()) {
	if (!debugJavaFile.exists()) debugJavaFile.createNewFile()
	debugJavaFile.writeText(this.toString())
	JavaParserCompiler.compile(file, debugJavaFile)
}

private fun tempJavaFile(): File = Files.createTempDirectory("grovlin").let {
	Files.createFile(it.resolve("ProgramGrovlin.java"))
}.toFile()
