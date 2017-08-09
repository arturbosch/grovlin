package io.gitlab.arturbosch.grovlin.compiler.backend

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration

/**
 * @author Artur Bosch
 */
data class CUnit(val fileName: String,
				 val mainClass: ClassOrInterfaceDeclaration,
				 val unit: CompilationUnit) {
	val javaFileName = fileName + ".java"
}
