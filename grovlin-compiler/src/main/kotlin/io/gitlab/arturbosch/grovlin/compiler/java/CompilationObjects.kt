package io.gitlab.arturbosch.grovlin.compiler.java

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

data class CPackage(val main: CUnit, val cus: List<CUnit>) {
	fun all() = cus + main
}
