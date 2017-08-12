package io.gitlab.arturbosch.grovlin.compiler.backend

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.TopLevelDeclarableNode
import io.gitlab.arturbosch.grovlin.ast.builtins.MainDeclaration

/**
 * @author Artur Bosch
 */
fun GrovlinFile.asJavaFile(): CPackage {
	if (name.isNullOrBlank()) throw UnnamedGrovlinFile()

	val fileBody = block ?: throw EmptyGrovlinFile()

	val main = fileBody.statements.filterIsInstance<MainDeclaration>().firstOrNull()
	main ?: throw MainMethodNotFound(name)

	val topLevelDeclarations = fileBody.statements.asSequence()
			.filterIsInstance(TopLevelDeclarableNode::class.java)
			.filterNot { it is MainDeclaration }
			.filter { it.isTopLevelDeclaration() }
			.map { it.toJava() }
			.toList()

	val additionalUnits = topLevelDeclarations
			.filterIsInstance<ClassOrInterfaceDeclaration>()
			.map { CUnit(it.nameAsString, it, CompilationUnit().apply { addType(it) }) }

	val members = topLevelDeclarations.filterNot { it is ClassOrInterfaceDeclaration }

	val unit = CompilationUnit()
	val clazz = main.toJava()
	members.forEach { clazz.addMember(it) }
	unit.addType(clazz)

	return CPackage(CUnit(clazz.nameAsString, clazz, unit), additionalUnits)
}

class UnnamedGrovlinFile : IllegalStateException("You cannot convert a grovlin file with no file name to java!")
class EmptyGrovlinFile : IllegalStateException("Empty files are no valid grovlin files!")
class MainMethodNotFound(fileName: String) : IllegalStateException("No main statement found in $fileName!")
