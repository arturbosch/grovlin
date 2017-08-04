package io.gitlab.arturbosch.grovlin.ast.visitors

import io.gitlab.arturbosch.grovlin.GrovlinParser
import io.gitlab.arturbosch.grovlin.GrovlinParserBaseVisitor
import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.AstNode
import io.gitlab.arturbosch.grovlin.ast.BlockStatement
import io.gitlab.arturbosch.grovlin.ast.DEFAULT_GROVLIN_FILE_NAME
import io.gitlab.arturbosch.grovlin.ast.ElifStatement
import io.gitlab.arturbosch.grovlin.ast.ExpressionStatement
import io.gitlab.arturbosch.grovlin.ast.ForStatement
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.INVALID_POSITION
import io.gitlab.arturbosch.grovlin.ast.IfStatement
import io.gitlab.arturbosch.grovlin.ast.LambdaDeclaration
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType
import io.gitlab.arturbosch.grovlin.ast.Position
import io.gitlab.arturbosch.grovlin.ast.Program
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.Reference
import io.gitlab.arturbosch.grovlin.ast.ReturnStatement
import io.gitlab.arturbosch.grovlin.ast.Statement
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.WhileStatement
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

class AntlrStatementVisitor : GrovlinParserBaseVisitor<Statement>() {

	private val exprVisitor = AntlrExpressionVisitor()

	override fun visitLambdaDeclaration(ctx: GrovlinParser.LambdaDeclarationContext): Statement {
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
		val initializer = ctx.assignment()?.expression()?.let { exprVisitor.visit(it) }
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
		val memberDecls = ctx.memberDeclaration().mapTo(ArrayList()) { visit(it) }
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
		val memberDecls = ctx.memberDeclaration().mapTo(ArrayList()) { visit(it) }
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

	fun visitStatements(ctx: GrovlinParser.StatementsContext?,
						lbrace: TerminalNode?,
						rbrace: TerminalNode?): BlockStatement? = visitStatements(ctx, lbrace?.symbol, rbrace?.symbol)

	fun visitStatements(ctx: GrovlinParser.StatementsContext?,
						lbrace: Token?,
						rbrace: Token?): BlockStatement? {
		val statements = ctx?.statement()?.mapTo(ArrayList()) { visit(it) }
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
		val expression = exprVisitor.visit(ctx.expressionStmt().expression())
		return ExpressionStatement(expression).apply {
			position = ctx.toPosition()
			children = listOf(expression)
			expression.parent = this
		}
	}

	override fun visitAssignment(ctx: GrovlinParser.AssignmentContext): Assignment {
		val expression = exprVisitor.visit(ctx.expression())
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
		val expression = exprVisitor.visit(ctx.expression())
		return ExpressionStatement(expression).apply {
			position = ctx.toPosition()
			children = listOf(expression)
			expression.parent = this
		}
	}

	override fun visitIfStmt(ctx: GrovlinParser.IfStmtContext): IfStatement {
		val condition = exprVisitor.visit(ctx.expression())
		val thenBlock = visitStatements(ctx.statements(), ctx.LBRACE(), ctx.RBRACE())
				?: throw AssertionError("If expects a then block!")
		val elifs = ctx.elifStmt().mapTo(ArrayList()) { visitElifStmt(it) } as MutableList<ElifStatement>
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
		val condition = exprVisitor.visit(ctx.expression())
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
		val condition = exprVisitor.visit(ctx.expression())
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
		val condition = exprVisitor.visit(ctx.expression())
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
		val expression = exprVisitor.visit(ctx.expression())
		return ReturnStatement(expression).apply {
			position = ctx.toPosition()
			children = listOf(expression)
			expression.parent = this
		}
	}

	private fun <T : Type> T.setPositions(token: Token?): T = apply { position = token?.toPosition() }

	private fun <T : Type> T.setPositions(token: TerminalNode?): T = apply { position = token?.toPosition() }
}
