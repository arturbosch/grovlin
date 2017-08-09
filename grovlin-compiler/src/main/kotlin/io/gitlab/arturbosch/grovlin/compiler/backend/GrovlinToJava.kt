package io.gitlab.arturbosch.grovlin.compiler.backend

import com.github.javaparser.JavaParser
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
import com.github.javaparser.ast.expr.StringLiteralExpr
import com.github.javaparser.ast.expr.ThisExpr
import com.github.javaparser.ast.expr.UnaryExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.stmt.ForStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.stmt.WhileStmt
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
import io.gitlab.arturbosch.grovlin.ast.ForStatement
import io.gitlab.arturbosch.grovlin.ast.GetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.GreaterEqualExpression
import io.gitlab.arturbosch.grovlin.ast.GreaterExpression
import io.gitlab.arturbosch.grovlin.ast.IfStatement
import io.gitlab.arturbosch.grovlin.ast.IntLit
import io.gitlab.arturbosch.grovlin.ast.IntRangeExpression
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
import io.gitlab.arturbosch.grovlin.ast.ParameterDeclaration
import io.gitlab.arturbosch.grovlin.ast.ParenExpression
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.ReturnStatement
import io.gitlab.arturbosch.grovlin.ast.SetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.Statement
import io.gitlab.arturbosch.grovlin.ast.StringLit
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
import io.gitlab.arturbosch.grovlin.ast.WhileStatement
import io.gitlab.arturbosch.grovlin.ast.XorExpression
import io.gitlab.arturbosch.grovlin.ast.builtins.MainDeclaration
import io.gitlab.arturbosch.grovlin.ast.builtins.Print
import io.gitlab.arturbosch.grovlin.ast.builtins.PrintLn
import io.gitlab.arturbosch.grovlin.ast.builtins.ReadLine
import io.gitlab.arturbosch.grovlin.ast.builtins.StringType
import java.util.ArrayList
import java.util.EnumSet
import com.github.javaparser.ast.body.MethodDeclaration as JavaParserMethod
import com.github.javaparser.ast.expr.Expression as JavaParserExpression
import com.github.javaparser.ast.stmt.Statement as JavaParserStatement

/**
 * @author Artur Bosch
 */

fun TopLevelDeclarable.toJava(): BodyDeclaration<*> = when (this) {
	is MethodDeclaration -> toJava()
	is TypeDeclaration -> transformToInterfaceDeclaration()
	is ObjectDeclaration -> transformToClassDeclaration()
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}

fun MainDeclaration.toJava(): ClassOrInterfaceDeclaration {
	val clazzName = name[0].toUpperCase() + name.substring(1)
	val statementsOfProgram = this@toJava.block?.statements
	return ClassOrInterfaceDeclaration().apply {
		setName(clazzName + "Gv") // #20
		addModifier(Modifier.PUBLIC, Modifier.FINAL)
		val main = addMethod("main", Modifier.PUBLIC, Modifier.STATIC)
		main.addParameter(ArrayType(JavaParser.parseClassOrInterfaceType("String")), "args")
		val statements = statementsOfProgram?.mapTo(NodeList<JavaParserStatement>()) { it.toJava() } ?: NodeList()
		main.setBody(BlockStmt(statements))
	}
}

fun ObjectDeclaration.transformToClassDeclaration(): ClassOrInterfaceDeclaration {
	val extends = extendedTypes.mapTo(ArrayList()) { it.toJava() as ClassOrInterfaceType }
	val superclass = extendedObject?.let { JavaParser.parseClassOrInterfaceType(extendedObject?.name) }
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

fun TypeDeclaration.transformToInterfaceDeclaration(): ClassOrInterfaceDeclaration {
	val extends = extendedTypes.mapTo(ArrayList()) { it.toJava() as ClassOrInterfaceType }
	val members = memberDeclarationsToJava(block, true)
	return ClassOrInterfaceDeclaration(EnumSet.of(Modifier.PUBLIC), true, name)
			.setExtendedTypes(NodeList.nodeList(extends))
			.setMembers(NodeList.nodeList(members))
}

fun memberDeclarationsToJava(declarations: BlockStatement?, isType: Boolean = false): MutableList<BodyDeclaration<*>> {
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

fun PropertyDeclaration.toJava(): BodyDeclaration<*> = FieldDeclaration(EnumSet.of(Modifier.PRIVATE),
		VariableDeclarator(type.toJava(), name).setInitializer(value?.toJava()))

fun PropertyDeclaration.typePropertyToJava(members: MutableList<BodyDeclaration<*>>) {
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

fun MethodDeclaration.toJava(isType: Boolean = false): BodyDeclaration<*> {
	val formalParameters = NodeList.nodeList(parameters.map { it.toJava() })
	val returnType = evaluationType?.toJava() ?: VoidType()
	val isStatic = isTopLevelDeclaration()
	return if (mustBeOverridden()) {
		val modifiers =
				if (isStatic) EnumSet.of(Modifier.ABSTRACT, Modifier.PUBLIC, Modifier.STATIC)
				else EnumSet.of(Modifier.ABSTRACT, Modifier.PUBLIC)
		JavaParserMethod().setName(name)
				.setModifiers(modifiers)
				.setBody(null)
				.setParameters(formalParameters)
				.setType(returnType)
	} else {
		val modifiers =
				if (isStatic) EnumSet.of(Modifier.PUBLIC, Modifier.STATIC)
				else EnumSet.of(Modifier.PUBLIC)
		JavaParserMethod().setName(name)
				.setModifiers(modifiers)
				.setBody(block!!.toJava() as BlockStmt)
				.setType(returnType)
				.setParameters(formalParameters)
				.setDefault(isType)
	}
}

fun ParameterDeclaration.toJava(): Parameter {
	val realType = promotionType ?: evaluationType
	val javaType = realType?.toJava() ?: VoidType()
	return Parameter(javaType, name)
}

fun Statement.toJava(): JavaParserStatement = when (this) {
	is VarDeclaration -> ExpressionStmt(VariableDeclarationExpr(VariableDeclarator(type.toJava(), name, value?.toJava())))
	is Assignment -> ExpressionStmt(AssignExpr(varReference.toJava(), value.toJava(), AssignExpr.Operator.ASSIGN))
	is ExpressionStatement -> ExpressionStmt(expression.toJava())
	is IfStatement -> IfStmt(condition.toJava(), thenStatement.toJava(), transformElifsToElseIfConstructs(elifs, elseStatement))
	is BlockStatement -> BlockStmt(NodeList.nodeList(statements.map { it.toJava() }))
	is ForStatement -> toJava()
	is WhileStatement -> toJava()
	is ReturnStatement -> ReturnStmt(expression.toJava())
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}

fun WhileStatement.toJava(): WhileStmt {
	val condition = condition.toJava()
	val body = thenStatement.toJava()
	return WhileStmt(condition, body)
}

fun ForStatement.toJava(): JavaParserStatement {
	val expr = expression
	if (expr is IntRangeExpression) {
		return forLoopFromIntRange(expr)
	} else {
		throw UnsupportedOperationException("No foreach for now!")
	}
}

private fun ForStatement.forLoopFromIntRange(expr: IntRangeExpression): ForStmt {
	val forStmt = ForStmt()
	val start = expr.start.toJava()
	val endExclusive = expr.endExclusive.toJava()
	val varReference = NameExpr(varDeclaration.name)
	forStmt.initialization = NodeList.nodeList(VariableDeclarationExpr(
			VariableDeclarator(varDeclaration.type.toJava(), varDeclaration.name, start)))
	forStmt.setCompare(BinaryExpr(varReference, endExclusive, BinaryExpr.Operator.LESS))
	forStmt.update = NodeList.nodeList(UnaryExpr(varReference, UnaryExpr.Operator.POSTFIX_INCREMENT))
	forStmt.body = block.toJava()
	return forStmt
}

fun VarReference.toJava(): JavaParserExpression = when (this.symbol?.def) {
	is PropertyDeclaration -> MethodCallExpr(null, varName.toGetter())
	is VarDeclaration -> NameExpr(varName)
	is ParameterDeclaration -> NameExpr(varName)
	else -> throw UnsupportedOperationException("Could not resolve '$varName' at '$position'"
			+ " because no definition found!")
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

fun Expression.toJava(): JavaParserExpression = when (this) {
	is ParenExpression -> EnclosedExpr(expression.toJava())
	is ObjectCreation -> ObjectCreationExpr(null, JavaParser.parseClassOrInterfaceType(type.name), NodeList())
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
	is StringLit -> StringLiteralExpr(value)
	is VarReference -> toJava()
	is ThisReference -> ThisExpr()
	is CallExpression -> toJava()
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

fun CallExpression.toJava(): MethodCallExpr = when (this) {
	is Print -> toJava()
	is PrintLn -> toJava()
	is ReadLine -> MethodCallExpr(
			ObjectCreationExpr(null, JavaParser.parseClassOrInterfaceType("java.util.Scanner"),
					NodeList.nodeList(FieldAccessExpr(NameExpr("System"), "in"))),
			SimpleName("next"), NodeList.nodeList(arguments[0].toJava()))
	else -> callToJava()
}

private fun CallExpression.callToJava(): MethodCallExpr {
	val callName = this.name
	val scopeExpr = scope?.toJava()
	val arguments =
			if (arguments.isEmpty()) NodeList()
			else NodeList.nodeList(arguments.map { it.toJava() })
	return MethodCallExpr(scopeExpr, callName, arguments)
}

private fun PrintLn.toJava(): MethodCallExpr {
	val fieldAccess = FieldAccessExpr(NameExpr("System"), "out")
	val arguments =
			if (arguments.isEmpty()) NodeList()
			else NodeList.nodeList(arguments[0].toJava())
	return MethodCallExpr(fieldAccess, SimpleName("println"), arguments)
}

private fun Print.toJava(): MethodCallExpr {
	val fieldAccess = FieldAccessExpr(NameExpr("System"), "out")
	val arguments = NodeList.nodeList(arguments[0].toJava())
	return MethodCallExpr(fieldAccess, SimpleName("print"), arguments)
}

fun Type.toJava(): com.github.javaparser.ast.type.Type = when (this) {
	is IntType -> PrimitiveType.intType()
	is DecimalType -> PrimitiveType.doubleType()
	is BoolType -> PrimitiveType.booleanType()
	is io.gitlab.arturbosch.grovlin.ast.VoidType -> VoidType()
	is ObjectOrTypeType, StringType -> JavaParser.parseClassOrInterfaceType(name)
	else -> throw UnsupportedOperationException(javaClass.canonicalName)
}
