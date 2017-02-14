package io.gitlab.arturbosch.grovlin.compiler.java

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.CastExpr
import com.github.javaparser.ast.expr.DoubleLiteralExpr
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.IntegerLiteralExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.expr.UnaryExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.type.ArrayType
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.PrimitiveType
import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.DecLit
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.DivisionExpression
import io.gitlab.arturbosch.grovlin.ast.Expression
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.IntLit
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.MultiplicationExpression
import io.gitlab.arturbosch.grovlin.ast.Print
import io.gitlab.arturbosch.grovlin.ast.Program
import io.gitlab.arturbosch.grovlin.ast.Statement
import io.gitlab.arturbosch.grovlin.ast.SubtractionExpression
import io.gitlab.arturbosch.grovlin.ast.SumExpression
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.TypeConversion
import io.gitlab.arturbosch.grovlin.ast.UnaryMinusExpression
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import com.github.javaparser.ast.stmt.Statement as JavaParserStatement
import com.github.javaparser.ast.type.Type as JavaParserType

/**
 * @author Artur Bosch
 */

fun GrovlinFile.toJava(): CompilationUnit {
	val unit = CompilationUnit()

	val program = (statements.find { it is Program } ?: throw IllegalStateException("No program statement found!")) as Program

	val clazz = ClassOrInterfaceDeclaration()
	clazz.setName("ProgramGrovlin")
	clazz.addModifier(Modifier.PUBLIC)
	unit.addType(clazz)

	val main = clazz.addMethod("main", Modifier.PUBLIC, Modifier.STATIC)
	main.addParameter(ArrayType(ClassOrInterfaceType("String")), "args")
	val statements = program.statements.mapTo(NodeList<com.github.javaparser.ast.stmt.Statement>()) { it.toJava() }
	main.setBody(BlockStmt(statements))

	return unit
}

private fun Statement.toJava(): com.github.javaparser.ast.stmt.Statement = when (this) {
	is VarDeclaration -> ExpressionStmt(VariableDeclarationExpr(VariableDeclarator(PrimitiveType.intType(), name, value.toJava())))
	is Print -> ExpressionStmt(MethodCallExpr(FieldAccessExpr(NameExpr("System"), "out"),
			SimpleName("println"), NodeList.nodeList(value.toJava())))
	is Assignment -> ExpressionStmt(AssignExpr(NameExpr(reference.name), value.toJava(), AssignExpr.Operator.ASSIGN))
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}

private fun Expression.toJava(): com.github.javaparser.ast.expr.Expression = when (this) {
	is SumExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.PLUS)
	is SubtractionExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.MINUS)
	is MultiplicationExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.MULTIPLY)
	is DivisionExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.DIVIDE)
	is UnaryMinusExpression -> UnaryExpr(value.toJava(), UnaryExpr.Operator.MINUS)
	is TypeConversion -> CastExpr(targetType.toJava(), value.toJava())
	is IntLit -> IntegerLiteralExpr(value)
	is DecLit -> DoubleLiteralExpr(value)
	is VarReference -> NameExpr(reference.name)
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}

private fun Type.toJava(): com.github.javaparser.ast.type.Type = when (this) {
	is IntType -> PrimitiveType.intType()
	is DecimalType -> PrimitiveType.doubleType()
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}
