package io.gitlab.arturbosch.grovlin.ast.visitors

import io.gitlab.arturbosch.grovlin.GrovlinParser
import io.gitlab.arturbosch.grovlin.GrovlinParserBaseVisitor
import io.gitlab.arturbosch.grovlin.ast.AndExpression
import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.AstNode
import io.gitlab.arturbosch.grovlin.ast.BlockStatement
import io.gitlab.arturbosch.grovlin.ast.BoolLit
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.DEFAULT_GROVLIN_FILE_NAME
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
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.INVALID_POSITION
import io.gitlab.arturbosch.grovlin.ast.IfStatement
import io.gitlab.arturbosch.grovlin.ast.IntLit
import io.gitlab.arturbosch.grovlin.ast.IntRangeExpression
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.LambdaDeclaration
import io.gitlab.arturbosch.grovlin.ast.LessEqualExpression
import io.gitlab.arturbosch.grovlin.ast.LessExpression
import io.gitlab.arturbosch.grovlin.ast.MemberDeclaration
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.MinusExpression
import io.gitlab.arturbosch.grovlin.ast.MultiplicationExpression
import io.gitlab.arturbosch.grovlin.ast.NotExpression
import io.gitlab.arturbosch.grovlin.ast.ObjectCreation
import io.gitlab.arturbosch.grovlin.ast.ObjectDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType
import io.gitlab.arturbosch.grovlin.ast.OrExpression
import io.gitlab.arturbosch.grovlin.ast.ParenExpression
import io.gitlab.arturbosch.grovlin.ast.Position
import io.gitlab.arturbosch.grovlin.ast.Program
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.Reference
import io.gitlab.arturbosch.grovlin.ast.ReturnStatement
import io.gitlab.arturbosch.grovlin.ast.SetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.Statement
import io.gitlab.arturbosch.grovlin.ast.StringLit
import io.gitlab.arturbosch.grovlin.ast.SubtractionExpression
import io.gitlab.arturbosch.grovlin.ast.SumExpression
import io.gitlab.arturbosch.grovlin.ast.ThisReference
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.TypeConversion
import io.gitlab.arturbosch.grovlin.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.ast.UnequalExpression
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.WhileStatement
import io.gitlab.arturbosch.grovlin.ast.XorExpression
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_PRINTLN_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_PRINT_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_READLINE_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.Print
import io.gitlab.arturbosch.grovlin.ast.builtins.PrintLn
import io.gitlab.arturbosch.grovlin.ast.builtins.ReadLine
import io.gitlab.arturbosch.grovlin.ast.builtins.StringType
import io.gitlab.arturbosch.grovlin.ast.endPoint
import io.gitlab.arturbosch.grovlin.ast.startPoint
import io.gitlab.arturbosch.grovlin.ast.toPosition
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.TerminalNode
import java.util.ArrayList

/**
 * @author Artur Bosch
 */
fun GrovlinParser.GrovlinFileContext?.asGrovlinFile(): GrovlinFile {
	this ?: throw AssertionError("Grovlin file context must not be null!")
	val visitor = AntlrStatementVisitor()
	val block = visitor.visitStatements(this.statements(), start, stop)
	val grovlinFile = GrovlinFile(DEFAULT_GROVLIN_FILE_NAME, block)
	grovlinFile.position = this.toPosition()
	block?.let { grovlinFile.children = listOf(it) }
	block?.parent = grovlinFile
	return grovlinFile
}

class AntlrStatementVisitor : GrovlinParserBaseVisitor<AstNode>() {

	override fun visitLambdaDeclaration(ctx: GrovlinParser.LambdaDeclarationContext): MemberDeclaration {
		val block = visitStatements(ctx.statements(), ctx.LBRACE(), ctx.RBRACE())
				?: throw AssertionError("Body is missing! ($ctx)")
		return LambdaDeclaration(ctx.ID().text, block).apply {
			position = ctx.toPosition()
			children = listOf(block)
			block.parent = this
		}
	}

	override fun visitPropertyDeclaration(ctx: GrovlinParser.PropertyDeclarationContext): PropertyDeclaration {
		val name = ctx.assignment()?.ID()?.text ?: ctx.ID().text
		val initializer = ctx.assignment()?.expression()?.let { visit(it) as Expression }
		val type = Type.of(ctx.TYPEID().text).setPositions(ctx.TYPEID())
		return PropertyDeclaration(type, name, initializer).apply {
			position = ctx.toPosition()
			type.parent = this
			if (initializer != null) {
				children = listOf(initializer, type)
				initializer.parent = this
			} else {
				children = listOf(type)
			}
		}
	}

	override fun visitMethodDeclaration(ctx: GrovlinParser.MethodDeclarationContext): MethodDeclaration {
		val block = visitStatements(ctx.statements(), ctx.LBRACE(), ctx.RBRACE())
		val returnType = Type.of(ctx.TYPEID()?.text ?: "Void").setPositions(ctx.TYPEID()).apply {
			if (position == null) position = INVALID_POSITION
		}
		return MethodDeclaration(ctx.ID().text, block, returnType).apply {
			position = ctx.toPosition()
			returnType.parent = this
			if (block != null) {
				block.parent = this
				children = listOf(returnType, block)
			} else {
				children = listOf(returnType)
			}
		}
	}

	override fun visitTypeDeclaration(ctx: GrovlinParser.TypeDeclarationContext): TypeDeclaration {
		val extended = ctx.extendTypes.mapTo(ArrayList()) { ObjectOrTypeType(it.text).setPositions(it) }
		val memberDecls = ctx.memberDeclaration().mapTo(ArrayList()) { visitMemberDeclaration(it) }
		val block = if (memberDecls.isNotEmpty()) BlockStatement(memberDecls).apply {
			position = Position(ctx.LBRACE().symbol.startPoint(), ctx.RBRACE().symbol.endPoint())
			children = memberDecls
			memberDecls.forEach { it.parent = this }
		} else null
		val type = ObjectOrTypeType(ctx.typeName.text).setPositions(ctx.typeName)
		return TypeDeclaration(type, extended, block).apply {
			position = ctx.toPosition()
			val nodes = mutableListOf<AstNode>()
			nodes.add(type)
			type.parent = this
			for (objectOrTypeType in extended) {
				nodes.add(objectOrTypeType)
				objectOrTypeType.parent = this
			}
			if (block != null) {
				nodes.add(block)
				block.parent = this
			}
			children = nodes
		}
	}


	override fun visitObjectDeclaration(ctx: GrovlinParser.ObjectDeclarationContext): ObjectDeclaration {
		val extendedObject = ctx.extendObject?.let { ObjectOrTypeType(it.text) }
		val extended = ctx.extendTypes.mapTo(ArrayList()) { ObjectOrTypeType(it.text) }
		val memberDecls = ctx.memberDeclaration().mapTo(ArrayList()) { visitMemberDeclaration(it) }
		val block = if (memberDecls.isNotEmpty()) BlockStatement(memberDecls).apply {
			position = Position(ctx.LBRACE().symbol.startPoint(), ctx.RBRACE().symbol.endPoint())
			children = memberDecls
			memberDecls.forEach { it.parent = this }
		} else null
		val type = ObjectOrTypeType(ctx.objectName.text)
		return ObjectDeclaration(type, extendedObject, extended, block).apply {
			position = ctx.toPosition()
			val nodes = mutableListOf<AstNode>()
			nodes.add(type)
			type.parent = this
			extendedObject?.let { nodes.add(it) }
			extendedObject?.parent = this
			for (objectOrTypeType in extended) {
				nodes.add(objectOrTypeType)
				objectOrTypeType.parent = this
			}
			if (block != null) {
				nodes.add(block)
				block.parent = this
			}
			children = nodes
		}
	}

	override fun visitVarDeclaration(ctx: GrovlinParser.VarDeclarationContext): Statement {
		val assignment = visitAssignment(ctx.assignment())
		val isVal = ctx.VAL() != null
		return VarDeclaration(assignment.reference.name, assignment.value, isVal).apply {
			position = ctx.toPosition()
			children = listOf(assignment.value)
			assignment.value.parent = this
		}
	}

	fun visitMemberDeclaration(ctx: GrovlinParser.MemberDeclarationContext): Statement {
		return when (ctx) {
			is GrovlinParser.ObjectMemberDeclarationContext -> visitObjectDeclaration(ctx.objectDeclaration())
			is GrovlinParser.TypeMemberDeclarationContext -> visitTypeDeclaration(ctx.typeDeclaration())
			is GrovlinParser.PropertyMemberDeclarationContext -> visitPropertyDeclaration(ctx.propertyDeclaration())
			is GrovlinParser.DefMemberDeclarationContext -> visitDefDeclaration(ctx.defDeclaration())
			else -> throw UnsupportedOperationException("No visit method found for ${ctx.javaClass.simpleName}")
		}
	}

	private fun visitDefDeclaration(ctx: GrovlinParser.DefDeclarationContext): Statement {
		return when (ctx) {
			is GrovlinParser.MethodDefinitionContext -> visitMethodDeclaration(ctx.methodDeclaration())
			is GrovlinParser.LambdaDefinitionContext -> visitLambdaDeclaration(ctx.lambdaDeclaration())
			else -> throw UnsupportedOperationException("No visit method found for ${ctx.javaClass.simpleName}")
		}
	}

	fun visitStatement(ctx: GrovlinParser.StatementContext): Statement {
		return when (ctx) {
			is GrovlinParser.IfStatementContext -> visitIfStmt(ctx.ifStmt())
			is GrovlinParser.ForStatementContext -> visitForStmt(ctx.forStmt())
			is GrovlinParser.WhileStatementContext -> visitWhileStmt(ctx.whileStmt())
			is GrovlinParser.ReturnStatementContext -> visitReturnStmt(ctx.returnStmt())
			is GrovlinParser.ExpressionStatementContext -> visitExpressionStmt(ctx.expressionStmt())
			is GrovlinParser.VarDeclarationStatementContext -> visitVarDeclaration(ctx.varDeclaration())
			is GrovlinParser.AssignmentStatementContext -> visitAssignment(ctx.assignment())
			is GrovlinParser.ProgramStatementContext -> visitProgram(ctx.program())
			is GrovlinParser.MemberDeclarationStatementContext -> visitMemberDeclaration(ctx.memberDeclaration())
			else -> throw UnsupportedOperationException("No visit method found for ${ctx.javaClass.simpleName}")
		}
	}

	fun visitStatements(ctx: GrovlinParser.StatementsContext?,
						lbrace: TerminalNode?,
						rbrace: TerminalNode?): BlockStatement? = visitStatements(ctx, lbrace?.symbol, rbrace?.symbol)

	fun visitStatements(ctx: GrovlinParser.StatementsContext?,
						lbrace: Token?,
						rbrace: Token?): BlockStatement? {
		val statements = ctx?.statement()?.mapTo(ArrayList()) { visitStatement(it) }
		return statements?.let {
			BlockStatement(it).apply {
				position = Position(
						lbrace?.startPoint() ?: ctx.start.startPoint(),
						rbrace?.endPoint() ?: ctx.stop.endPoint())
				children = statements
				statements.forEach { it.parent = this }
			}
		}
	}

	override fun visitExpressionStatement(ctx: GrovlinParser.ExpressionStatementContext): ExpressionStatement {
		val expression = visitExpression(ctx.expressionStmt().expression())
		return ExpressionStatement(expression).apply {
			position = ctx.toPosition()
			children = listOf(expression)
			expression.parent = this
		}
	}

	override fun visitAssignment(ctx: GrovlinParser.AssignmentContext): Assignment {
		val expression = visitExpression(ctx.expression())
		return Assignment(Reference(ctx.ID().text), expression).apply {
			position = ctx.toPosition()
			children = listOf(expression)
			expression.parent = this
		}
	}

	override fun visitProgram(ctx: GrovlinParser.ProgramContext): Statement {
		val block = visitStatements(ctx.statements(), ctx.LBRACE(), ctx.RBRACE())
		return Program(DEFAULT_GROVLIN_FILE_NAME, block).apply {
			position = ctx.toPosition()
			if (block != null) {
				children = listOf(block)
				block.parent = this
			}

		}
	}

	override fun visitExpressionStmt(ctx: GrovlinParser.ExpressionStmtContext): ExpressionStatement {
		val expression = visitExpression(ctx.expression())
		return ExpressionStatement(expression).apply {
			position = ctx.toPosition()
			children = listOf(expression)
			expression.parent = this
		}
	}

	override fun visitIfStmt(ctx: GrovlinParser.IfStmtContext): IfStatement {
		val condition = visitExpression(ctx.expression())
		val thenBlock = visitStatements(ctx.statements(), ctx.LBRACE(), ctx.RBRACE())
				?: throw AssertionError("If expects a then block!")
		val elifs = ctx.elifStmt().mapTo(ArrayList()) { visitElifStmt(it) }
		val elseStmt = ctx.elseStmt()?.let { visitElseStmt(it) }
		return IfStatement(condition, thenBlock, elifs, elseStmt).apply {
			position = ctx.toPosition()
			val nodes = mutableListOf<AstNode>()
			nodes.add(condition)
			condition.parent = this
			nodes.add(thenBlock)
			thenBlock.parent = this
			elifs.forEach {
				it.parent = this
				nodes.add(it)
			}
			if (elseStmt != null) {
				nodes.add(elseStmt)
				elseStmt.parent = this
			}
			children = nodes
		}
	}

	override fun visitElifStmt(ctx: GrovlinParser.ElifStmtContext): ElifStatement {
		val condition = visitExpression(ctx.expression())
		val block = visitStatements(ctx.statements(), ctx.LBRACE(), ctx.RBRACE())
				?: throw AssertionError("Body is missing!")
		return ElifStatement(condition, block).apply {
			position = ctx.toPosition()
			children = listOf(condition, block)
			condition.parent = this
			block.parent = this
		}
	}

	override fun visitElseStmt(ctx: GrovlinParser.ElseStmtContext): BlockStatement? {
		return visitStatements(ctx.statements(), ctx.LBRACE(), ctx.RBRACE())
	}

	override fun visitForStmt(ctx: GrovlinParser.ForStmtContext): ForStatement {
		val varName = ctx.ID().text
		val condition = visitExpression(ctx.expression())
		val block = visitStatements(ctx.statements(), ctx.LBRACE(), ctx.RBRACE())
				?: throw AssertionError("Body is missing!")
		return ForStatement(varName, condition, block).apply {
			position = ctx.toPosition()
			children = listOf(condition, block)
			condition.parent = this
			block.parent = this
		}
	}

	override fun visitWhileStmt(ctx: GrovlinParser.WhileStmtContext): WhileStatement {
		val condition = visitExpression(ctx.expression())
		val block = visitStatements(ctx.statements(), ctx.LBRACE(), ctx.RBRACE())
				?: throw AssertionError("Body is missing!")
		return WhileStatement(condition, block).apply {
			position = ctx.toPosition()
			children = listOf(condition, block)
			condition.parent = this
			block.parent = this
		}
	}

	override fun visitReturnStmt(ctx: GrovlinParser.ReturnStmtContext): ReturnStatement {
		val expression = visitExpression(ctx.expression())
		return ReturnStatement(expression).apply {
			position = ctx.toPosition()
			children = listOf(expression)
			expression.parent = this
		}
	}

	// Expressions

	override fun visitDecimalLiteral(ctx: GrovlinParser.DecimalLiteralContext): Expression {
		return DecLit(ctx.text).apply {
			position = ctx.toPosition()
		}
	}

	override fun visitSetterAccessExpression(ctx: GrovlinParser.SetterAccessExpressionContext): Expression {
		val scopeExpr = ctx.scope?.let { visitExpression(it) }
		val expression = visitExpression(ctx.assignment().expression())
		return SetterAccessExpression(scopeExpr,
				ctx.assignment().ID().text,
				expression).apply {
			position = ctx.toPosition()
			expression.parent = this
			scopeExpr?.parent = this
			children = scopeExpr?.let { listOf(it, expression) } ?: listOf(expression)
		}
	}

	fun visitExpression(ctx: GrovlinParser.ExpressionContext): Expression {
		return when (ctx) {
			is GrovlinParser.MinusExpressionContext -> visitMinusExpression(ctx)
			is GrovlinParser.NotExpressionContext -> visitNotExpression(ctx)
			is GrovlinParser.BinaryOperationContext -> visitBinaryOperation(ctx)
			is GrovlinParser.CallExpressionContext -> visitCallExpression(ctx)
			is GrovlinParser.ParenExpressionContext -> visitParenExpression(ctx)
			is GrovlinParser.GetterAccessExpressionContext -> visitGetterAccessExpression(ctx)
			is GrovlinParser.SetterAccessExpressionContext -> visitSetterAccessExpression(ctx)
			is GrovlinParser.ThisExpressionContext -> visitThisExpression(ctx)
			is GrovlinParser.TypeConversionContext -> visitTypeConversion(ctx)
			is GrovlinParser.VarReferenceContext -> visitVarReference(ctx)
			is GrovlinParser.ObjectCreationExpressionContext -> visitObjectCreationExpression(ctx)
			is GrovlinParser.IntRangeExpressionContext -> visitIntRangeExpression(ctx)
			else -> throw UnsupportedOperationException("No visit method found for ${ctx.javaClass.simpleName}")
		}
	}

	override fun visitMinusExpression(ctx: GrovlinParser.MinusExpressionContext): Expression {
		val expression = visitExpression(ctx.expression())
		return MinusExpression(expression).apply {
			position = ctx.toPosition()
			expression.parent = this
			children = listOf(expression)
		}
	}

	override fun visitIntLiteral(ctx: GrovlinParser.IntLiteralContext): Expression {
		return IntLit(ctx.text).apply {
			position = ctx.toPosition()
		}
	}

	override fun visitNotExpression(ctx: GrovlinParser.NotExpressionContext): Expression {
		val expression = visitExpression(ctx.expression())
		return NotExpression(expression).apply {
			position = ctx.toPosition()
			expression.parent = this
			children = listOf(expression)
		}
	}

	override fun visitParenExpression(ctx: GrovlinParser.ParenExpressionContext): Expression {
		val expression = visitExpression(ctx.expression())
		return ParenExpression(expression).apply {
			position = ctx.toPosition()
			expression.parent = this
			children = listOf(expression)
		}
	}

	override fun visitBinaryOperation(ctx: GrovlinParser.BinaryOperationContext): Expression {
		val leftExpr = visitExpression(ctx.left)
		val rightExpr = visitExpression(ctx.right)
		return when (ctx.operator.text) {
			"+" -> SumExpression(leftExpr, rightExpr)
			"-" -> SubtractionExpression(leftExpr, rightExpr)
			"*" -> MultiplicationExpression(leftExpr, rightExpr)
			"/" -> DivisionExpression(leftExpr, rightExpr)
			"&&" -> AndExpression(leftExpr, rightExpr)
			"||" -> OrExpression(leftExpr, rightExpr)
			"^" -> XorExpression(leftExpr, rightExpr)
			"==" -> EqualExpression(leftExpr, rightExpr)
			"!=" -> UnequalExpression(leftExpr, rightExpr)
			">=" -> GreaterEqualExpression(leftExpr, rightExpr)
			"<=" -> LessEqualExpression(leftExpr, rightExpr)
			">" -> GreaterExpression(leftExpr, rightExpr)
			"<" -> LessExpression(leftExpr, rightExpr)
			else -> throw UnsupportedOperationException(javaClass.canonicalName)
		}.apply {
			position = ctx.toPosition()
			children = listOf(left, right)
			left.parent = this
			right.parent = this
		}
	}

	override fun visitTypeConversion(ctx: GrovlinParser.TypeConversionContext): Expression {
		val type = visit(ctx.targetType) as Type
		val expression = visitExpression(ctx.value)
		return TypeConversion(expression, type).apply {
			position = ctx.toPosition()
			children = listOf(value, targetType)
			value.parent = this
			targetType.parent = this
		}
	}

	override fun visitIntRangeExpression(ctx: GrovlinParser.IntRangeExpressionContext): Expression {
		val startLit = ctx.INTLIT(0)
		val start = IntLit(startLit.text).apply { position = startLit.toPosition() }
		val endLit = ctx.INTLIT(1)
		val endExclusive = IntLit(endLit.text).apply { position = endLit.toPosition() }
		return IntRangeExpression(start, endExclusive).apply {
			position = ctx.toPosition()
			children = listOf(start, endExclusive)
			start.parent = this
			endExclusive.parent = this
		}
	}

	override fun visitBoolLiteral(ctx: GrovlinParser.BoolLiteralContext): Expression {
		return BoolLit(ctx.BOOLLIT().text.toBoolean()).apply {
			position = ctx.toPosition()
		}
	}

	override fun visitVarReference(ctx: GrovlinParser.VarReferenceContext): Expression {
		return VarReference(Reference(ctx.text)).apply {
			position = ctx.toPosition()
		}
	}

	override fun visitObjectCreationExpression(ctx: GrovlinParser.ObjectCreationExpressionContext): Expression {
		val type = ObjectOrTypeType(ctx.TYPEID().text).apply { position = ctx.TYPEID().toPosition() }
		return ObjectCreation(type).apply {
			position = ctx.toPosition()
			children = listOf(type)
			type.parent = this
		}
	}

	override fun visitCallExpression(ctx: GrovlinParser.CallExpressionContext): Expression {
		val scope = ctx.scope?.let { visitExpression(it) }
		val arguments = ctx.argumentList()?.argument()?.map { visitArgument(it) }
		val methodName = ctx.methodName.text
		return convertToCallConsiderBuiltins(scope, methodName, arguments ?: emptyList()).apply {
			position = ctx.toPosition()
			children = (arguments?.plus(scope))?.filterNotNull() ?: emptyList()
			scope?.parent = this
			arguments?.forEach { it.parent = this }
		}
	}

	private fun convertToCallConsiderBuiltins(scope: Expression?,
											  methodName: String,
											  arguments: List<Expression>): Expression =
			if (scope == null) {
				when (methodName) {
					BUILTIN_PRINTLN_NAME -> PrintLn(arguments)
					BUILTIN_PRINT_NAME -> Print(arguments)
					BUILTIN_READLINE_NAME -> ReadLine(arguments)
					else -> CallExpression(scope, methodName, arguments)
				}
			} else {
				CallExpression(scope, methodName, arguments)
			}

	override fun visitArgument(ctx: GrovlinParser.ArgumentContext): Expression {
		return visitExpression(ctx.expression())
	}

	override fun visitGetterAccessExpression(ctx: GrovlinParser.GetterAccessExpressionContext): Expression {
		val scope = ctx.scope?.let { visitExpression(it) }
		return GetterAccessExpression(scope, ctx.fieldName.text).apply {
			position = ctx.toPosition()
			scope?.let { children = listOf(scope) }
			scope?.parent = this
		}
	}

	override fun visitStringLiteral(ctx: GrovlinParser.StringLiteralContext): Expression {
		return StringLit(ctx.text).apply {
			position = ctx.toPosition()
		}
	}

	override fun visitThisExpression(ctx: GrovlinParser.ThisExpressionContext): Expression {
		return ThisReference(Reference("this")).apply {
			position = ctx.toPosition()
		}
	}

	// Types

	override fun visitInteger(ctx: GrovlinParser.IntegerContext): Type {
		return IntType.apply {
			position = ctx.toPosition()
		}
	}

	override fun visitDecimal(ctx: GrovlinParser.DecimalContext): Type {
		return DecimalType.apply {
			position = ctx.toPosition()
		}
	}

	override fun visitBool(ctx: GrovlinParser.BoolContext): Type {
		return BoolType.apply {
			position = ctx.toPosition()
		}
	}

	override fun visitString(ctx: GrovlinParser.StringContext): Type {
		return StringType.apply {
			position = ctx.toPosition()
		}
	}

	private fun <T : Type> T.setPositions(token: Token?): T = apply { position = token?.toPosition() }

	private fun <T : Type> T.setPositions(token: TerminalNode?): T = apply { position = token?.toPosition() }
}
