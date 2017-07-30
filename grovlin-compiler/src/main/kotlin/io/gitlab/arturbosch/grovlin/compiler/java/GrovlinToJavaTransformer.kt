package io.gitlab.arturbosch.grovlin.compiler.java

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.Parameter
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
import io.gitlab.arturbosch.grovlin.ast.GetterAccessExpression
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
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.Reference
import io.gitlab.arturbosch.grovlin.ast.SetterAccessExpression
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
import io.gitlab.arturbosch.grovlin.ast.VariableDeclaration
import io.gitlab.arturbosch.grovlin.ast.XorExpression
import java.util.ArrayList
import java.util.EnumSet
import com.github.javaparser.ast.body.MethodDeclaration as JavaParserMethod
import com.github.javaparser.ast.expr.Expression as JavaParserExpression
import com.github.javaparser.ast.stmt.Statement as JavaParserStatement

/**
 * @author Artur Bosch
 */

fun GrovlinFile.toJava(): CPackage {
	if (name.isNullOrBlank()) throw IllegalStateException("You cannot convert a grovlin file with no file name to java!")
	if (block == null) throw IllegalStateException("Empty files are no valid grovlin files!")

	val unit = CompilationUnit()

	val program = (block!!.statements.find { it is Program } ?: throw IllegalStateException("No program statement found!")) as Program

	val topLevelDeclarations = block!!.statements.filterIsInstance(TopLevelDeclarable::class.java)
			.filterNot { it is Program }
			.filter { it.isTopLevelDeclaration() }
			.map { it.toJava() }

	val additionalUnits = topLevelDeclarations.filterIsInstance<ClassOrInterfaceDeclaration>()
			.map { CUnit(it.nameAsString, it, CompilationUnit().apply { addType(it) }) }

	val membersOfProgram = topLevelDeclarations.filterNot { it is ClassOrInterfaceDeclaration }

	val clazz = program.toJava()
	membersOfProgram.forEach { clazz.addMember(it) }
	unit.addType(clazz)

	return CPackage(CUnit(clazz.nameAsString, clazz, unit), additionalUnits)
}

private fun TopLevelDeclarable.toJava(): BodyDeclaration<*> = when (this) {
	is MethodDeclaration -> {
		val statements = block?.statements?.map { it.toJava() } ?: emptyList()
		JavaParserMethod(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), VoidType(), name).apply {
			setBody(BlockStmt(NodeList.nodeList(statements)))
		}
	}
	is TypeDeclaration -> transformToInterfaceDeclaration()
	is ObjectDeclaration -> transformToClassDeclaration()
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}

private fun Program.toJava(): ClassOrInterfaceDeclaration {
	val clazzName = name[0].toUpperCase() + name.substring(1)
	val statementsOfProgram = this@toJava.block?.statements
	return ClassOrInterfaceDeclaration().apply {
		setName(clazzName + "Gv") // #20
		addModifier(Modifier.PUBLIC, Modifier.FINAL)
		val main = addMethod("main", Modifier.PUBLIC, Modifier.STATIC)
		main.addParameter(ArrayType(ClassOrInterfaceType("String")), "args")
		val statements = statementsOfProgram?.mapTo(NodeList<JavaParserStatement>()) { it.toJava() } ?: NodeList()
		main.setBody(BlockStmt(statements))
	}
}

private fun ObjectDeclaration.transformToClassDeclaration(): ClassOrInterfaceDeclaration {
	val extends = extendedTypes.mapTo(ArrayList()) { it.toJava() as ClassOrInterfaceType }
	val superclass = extendedObject?.let { ClassOrInterfaceType(extendedObject?.name) }
	val members = memberDeclarationsToJava(block, false)
	return ClassOrInterfaceDeclaration(EnumSet.of(Modifier.PUBLIC), false, name)
			.setImplementedTypes(NodeList.nodeList(extends))
			.setMembers(NodeList.nodeList(members)).apply {
		superclass?.let { addExtendedType(superclass) }
		fields.forEach {
			it.createGetter()
			it.createSetter()
		}
	}

}

private fun TypeDeclaration.transformToInterfaceDeclaration(): ClassOrInterfaceDeclaration {
	val extends = extendedTypes.mapTo(ArrayList()) { it.toJava() as ClassOrInterfaceType }
	val members = memberDeclarationsToJava(block, true)
	return ClassOrInterfaceDeclaration(EnumSet.of(Modifier.PUBLIC), true, name)
			.setExtendedTypes(NodeList.nodeList(extends))
			.setMembers(NodeList.nodeList(members))
}

private fun memberDeclarationsToJava(declarations: BlockStatement?, isType: Boolean = false): MutableList<BodyDeclaration<*>> {
	val members = mutableListOf<BodyDeclaration<*>>()
	declarations?.statements?.forEach {
		when (it) {
			is MethodDeclaration -> members.add(it.toJava(isType))
			is PropertyDeclaration -> if (isType) {
				it.typePropertyToJava(members)
			} else {
				members.add(it.toJava())
			}
		}
	}
	return members
}

private fun PropertyDeclaration.toJava(): BodyDeclaration<*> = FieldDeclaration(EnumSet.of(Modifier.PRIVATE),
		VariableDeclarator(type.toJava(), name).setInitializer(value?.toJava()))

private fun PropertyDeclaration.typePropertyToJava(members: MutableList<BodyDeclaration<*>>) {
	val fieldType = type.toJava()
	members.add(JavaParserMethod().setName("get" + name[0].toUpperCase() + name.substring(1))
			.setModifiers(EnumSet.of(Modifier.ABSTRACT, Modifier.PUBLIC))
			.setBody(null)
			.setType(fieldType))
	members.add(JavaParserMethod().setName("set" + name[0].toUpperCase() + name.substring(1))
			.setModifiers(EnumSet.of(Modifier.ABSTRACT, Modifier.PUBLIC))
			.setBody(null)
			.setParameters(NodeList.nodeList(Parameter(fieldType, name)))
			.setType(VoidType()))
}

private fun MethodDeclaration.toJava(isType: Boolean = false): BodyDeclaration<*> = if (mustBeOverridden()) {
	JavaParserMethod().setName(name)
			.setModifiers(EnumSet.of(Modifier.ABSTRACT, Modifier.PUBLIC))
			.setBody(null)
			.setType(VoidType())
} else {
	JavaParserMethod().setName(name)
			.setModifiers(EnumSet.of(Modifier.PUBLIC))
			.setBody(block!!.toJava() as BlockStmt)
			.setType(VoidType())
			.setDefault(isType)
}

private fun Statement.toJava(): com.github.javaparser.ast.stmt.Statement = when (this) {
	is VarDeclaration -> ExpressionStmt(VariableDeclarationExpr(VariableDeclarator(type.toJava(), name, value.toJava())))
	is Print -> ExpressionStmt(MethodCallExpr(FieldAccessExpr(NameExpr("System"), "out"),
			SimpleName("println"), NodeList.nodeList(value.toJava())))
	is Assignment -> ExpressionStmt(AssignExpr(reference.toJava(), value.toJava(), AssignExpr.Operator.ASSIGN))
	is ExpressionStatement -> ExpressionStmt(expression.toJava())
	is IfStatement -> IfStmt(condition.toJava(), thenStatement.toJava(), transformElifsToElseIfConstructs(elifs, elseStatement))
	is BlockStatement -> BlockStmt(NodeList.nodeList(statements.map { it.toJava() }))
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}

private fun Reference<VariableDeclaration>.toJava(): JavaParserExpression = when (this.source) {
	is PropertyDeclaration -> MethodCallExpr(null, name.toGetter())
	is VarDeclaration -> NameExpr(name)
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}

private fun String.toGetter() = "get" + get(0).toUpperCase() + substring(1)
private fun String.toSetter() = "set" + get(0).toUpperCase() + substring(1)

fun transformElifsToElseIfConstructs(elifs: MutableList<ElifStatement>, elseStatement: BlockStatement?): JavaParserStatement? {
	return if (elifs.isNotEmpty()) {
		val elif = elifs[0]
		IfStmt(elif.condition.toJava(), elif.thenStatement.toJava(),
				transformElifsToElseIfConstructs(elifs.subList(1, elifs.size), elseStatement))
	} else {
		elseStatement?.toJava()
	}
}

private fun Expression.toJava(): JavaParserExpression = when (this) {
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
	is VarReference -> reference.toJava()
	is ThisReference -> ThisExpr()
	is CallExpression -> MethodCallExpr().apply {
		setName(this@toJava.name)
		if (this@toJava.scope != null) setScope(this@toJava.scope!!.toJava())
	}
	is SetterAccessExpression -> MethodCallExpr().apply {
		setName(this@toJava.name.toSetter())
		if (this@toJava.scope != null) setScope(this@toJava.scope!!.toJava())
		arguments = NodeList.nodeList(this@toJava.expression.toJava())
	}
	is GetterAccessExpression -> MethodCallExpr().apply {
		setName(this@toJava.name.toGetter())
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
