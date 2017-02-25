package io.gitlab.arturbosch.grovlin.compiler.java

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.BooleanLiteralExpr
import com.github.javaparser.ast.expr.CastExpr
import com.github.javaparser.ast.expr.DoubleLiteralExpr
import com.github.javaparser.ast.expr.EnclosedExpr
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.IntegerLiteralExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.expr.ThisExpr
import com.github.javaparser.ast.expr.UnaryExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.type.ArrayType
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.PrimitiveType
import com.github.javaparser.ast.type.VoidType
import io.gitlab.arturbosch.grovlin.ast.AndExpression
import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.BlockStatement
import io.gitlab.arturbosch.grovlin.ast.BoolLit
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.DecLit
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.DivisionExpression
import io.gitlab.arturbosch.grovlin.ast.ElifStatement
import io.gitlab.arturbosch.grovlin.ast.EqualExpression
import io.gitlab.arturbosch.grovlin.ast.Expression
import io.gitlab.arturbosch.grovlin.ast.ExpressionStatement
import io.gitlab.arturbosch.grovlin.ast.GreaterEqualExpression
import io.gitlab.arturbosch.grovlin.ast.GreaterExpression
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.IfStatement
import io.gitlab.arturbosch.grovlin.ast.IntLit
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.LessEqualExpression
import io.gitlab.arturbosch.grovlin.ast.LessExpression
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.MinusExpression
import io.gitlab.arturbosch.grovlin.ast.MultiplicationExpression
import io.gitlab.arturbosch.grovlin.ast.NotExpression
import io.gitlab.arturbosch.grovlin.ast.ObjectCreation
import io.gitlab.arturbosch.grovlin.ast.ObjectDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType
import io.gitlab.arturbosch.grovlin.ast.OrExpression
import io.gitlab.arturbosch.grovlin.ast.ParenExpression
import io.gitlab.arturbosch.grovlin.ast.Print
import io.gitlab.arturbosch.grovlin.ast.Program
import io.gitlab.arturbosch.grovlin.ast.Statement
import io.gitlab.arturbosch.grovlin.ast.SubtractionExpression
import io.gitlab.arturbosch.grovlin.ast.SumExpression
import io.gitlab.arturbosch.grovlin.ast.ThisReference
import io.gitlab.arturbosch.grovlin.ast.TopLevelDeclarable
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.TypeConversion
import io.gitlab.arturbosch.grovlin.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.ast.UnequalExpression
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.XorExpression
import java.util.ArrayList
import java.util.EnumSet
import com.github.javaparser.ast.body.MethodDeclaration as JavaParserMethod
import com.github.javaparser.ast.stmt.Statement as JavaParserStatement

/**
 * @author Artur Bosch
 */

fun GrovlinFile.toJava(): CUnit {
	if (name.isNullOrBlank()) throw IllegalStateException("You cannot convert a grovlin file with no file name to java!")

	val unit = CompilationUnit()

	val program = (statements.find { it is Program } ?: throw IllegalStateException("No program statement found!")) as Program

	val topLevelDeclarations = statements.filterIsInstance(TopLevelDeclarable::class.java)
			.filterNot { it is Program }
			.filter { it.isTopLevelDeclaration() }
			.map { it.toJava() }

	val additionalUnits = topLevelDeclarations.filter { it is ClassOrInterfaceDeclaration }
			.map { CompilationUnit().apply { addType(it as ClassOrInterfaceDeclaration) } }

	val membersOfProgram = topLevelDeclarations.filterNot { it is ClassOrInterfaceDeclaration }

	val clazz = program.toJava()
	membersOfProgram.forEach { clazz.addMember(it) }
	unit.addType(clazz)

	return CUnit(clazz.nameAsString, clazz, unit, additionalUnits)
}

private fun TopLevelDeclarable.toJava(): BodyDeclaration<*> = when (this) {
	is MethodDeclaration -> JavaParserMethod(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), VoidType(), name).apply {
		setBody(BlockStmt(NodeList.nodeList(this@toJava.statements.map { it.toJava() })))
	}
	is TypeDeclaration -> transformToInterfaceDeclaration()
	is ObjectDeclaration -> transformToClassDeclaration()
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}

private fun Program.toJava(): ClassOrInterfaceDeclaration {
	val clazzName = name[0].toUpperCase() + name.substring(1)
	val statementsOfProgram = this@toJava.statements
	return ClassOrInterfaceDeclaration().apply {
		setName(clazzName + "Gv") // #20
		addModifier(Modifier.PUBLIC)
		val main = addMethod("main", Modifier.PUBLIC, Modifier.STATIC)
		main.addParameter(ArrayType(ClassOrInterfaceType("String")), "args")
		val statements = statementsOfProgram.mapTo(NodeList<JavaParserStatement>()) { it.toJava() }
		main.setBody(BlockStmt(statements))
	}
}

private fun ObjectDeclaration.transformToClassDeclaration(): ClassOrInterfaceDeclaration {
	val extends = extendedTypes.mapTo(ArrayList()) { it.toJava() as ClassOrInterfaceType }
	val superclass = extendedObject?.let { ClassOrInterfaceType(extendedObject?.name) }
	return ClassOrInterfaceDeclaration(EnumSet.of(Modifier.PUBLIC), false, name)
			.setImplementedTypes(NodeList.nodeList(extends)).apply {
		superclass?.let { addExtendedType(superclass) }
	}

}

private fun TypeDeclaration.transformToInterfaceDeclaration(): ClassOrInterfaceDeclaration {
	val extends = extendedTypes.mapTo(ArrayList()) { it.toJava() as ClassOrInterfaceType }
	return ClassOrInterfaceDeclaration(EnumSet.of(Modifier.PUBLIC), true, name)
			.setExtendedTypes(NodeList.nodeList(extends))
}

private fun Statement.toJava(): com.github.javaparser.ast.stmt.Statement = when (this) {
	is VarDeclaration -> ExpressionStmt(VariableDeclarationExpr(VariableDeclarator(type.toJava(), name, value.toJava())))
	is Print -> ExpressionStmt(MethodCallExpr(FieldAccessExpr(NameExpr("System"), "out"),
			SimpleName("println"), NodeList.nodeList(value.toJava())))
	is Assignment -> ExpressionStmt(AssignExpr(NameExpr(reference.name), value.toJava(), AssignExpr.Operator.ASSIGN))
	is ExpressionStatement -> ExpressionStmt(expression.toJava())
	is IfStatement -> IfStmt(condition.toJava(), thenStatement.toJava(), transformElifsToElseIfConstructs(elifs, elseStatement))
	is BlockStatement -> BlockStmt(NodeList.nodeList(statements.map { it.toJava() }))
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}

fun transformElifsToElseIfConstructs(elifs: MutableList<ElifStatement>, elseStatement: BlockStatement?): JavaParserStatement? {
	return if (elifs.isNotEmpty()) {
		val elif = elifs[0]
		IfStmt(elif.condition.toJava(), elif.thenStatement.toJava(),
				transformElifsToElseIfConstructs(elifs.subList(1, elifs.size), elseStatement))
	} else {
		elseStatement?.toJava()
	}
}

private fun Expression.toJava(): com.github.javaparser.ast.expr.Expression = when (this) {
	is ParenExpression -> EnclosedExpr(expression.toJava())
	is ObjectCreation -> ObjectCreationExpr(null, ClassOrInterfaceType(type.name), NodeList())
	is SumExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.PLUS)
	is SubtractionExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.MINUS)
	is MultiplicationExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.MULTIPLY)
	is DivisionExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.DIVIDE)
	is MinusExpression -> UnaryExpr(value.toJava(), UnaryExpr.Operator.MINUS)
	is AndExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.AND)
	is OrExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.OR)
	is XorExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.XOR)
	is EqualExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.EQUALS)
	is UnequalExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.NOT_EQUALS)
	is LessEqualExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.LESS_EQUALS)
	is GreaterEqualExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.GREATER_EQUALS)
	is LessExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.LESS)
	is GreaterExpression -> BinaryExpr(left.toJava(), right.toJava(), BinaryExpr.Operator.GREATER)
	is NotExpression -> UnaryExpr(value.toJava(), UnaryExpr.Operator.LOGICAL_COMPLEMENT)
	is TypeConversion -> CastExpr(targetType.toJava(), value.toJava())
	is IntLit -> IntegerLiteralExpr(value)
	is DecLit -> DoubleLiteralExpr(value)
	is BoolLit -> BooleanLiteralExpr(value)
	is VarReference -> NameExpr(reference.name)
	is ThisReference -> ThisExpr()
	is CallExpression -> MethodCallExpr().apply {
		setName(this@toJava.name)
		if (this@toJava.scope != null) setScope(this@toJava.scope!!.toJava())
	}
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}

private fun Type.toJava(): com.github.javaparser.ast.type.Type = when (this) {
	is IntType -> PrimitiveType.intType()
	is DecimalType -> PrimitiveType.doubleType()
	is BoolType -> PrimitiveType.booleanType()
	is ObjectOrTypeType -> ClassOrInterfaceType(name)
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}
