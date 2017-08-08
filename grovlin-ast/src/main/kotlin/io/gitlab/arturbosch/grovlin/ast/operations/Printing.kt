package io.gitlab.arturbosch.grovlin.ast.operations

import io.gitlab.arturbosch.grovlin.ast.AstNode
import io.gitlab.arturbosch.grovlin.ast.Node
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaType
import kotlin.reflect.memberProperties

const val TAB = "\t"

fun AstNode.asString(indent: String = ""): String {
	val sb = StringBuilder()
	sb.append("$indent${javaClass.simpleName}\n")
	filteredMembers().forEach { it.propertyToString(it.get(this), indent, sb) }
	return sb.toString()
}

fun AstNode.filteredMembers(): Sequence<KProperty1<AstNode, *>> {
	return javaClass.kotlin.memberProperties.asSequence()
			.filterNot { it.name == "position" }
			.filterNot { it.name == "parent" }
			.filterNot { it.name == "children" }
			.filterNot { it.name == "errors" }
			.filterNot { it.name == "typeReference" }
}

private fun KProperty1<AstNode, *>.propertyToString(value: Any?, indent: String, sb: StringBuilder) {
	val type = returnType.javaType
	if (type is ParameterizedType && type.rawType == List::class.java) {
		val paramType = type.actualTypeArguments[0]
		if (paramType is Class<*> && Node::class.java.isAssignableFrom(paramType)) {
			(value as List<*>).forEach { sb.append((it as Node).asString(indent + TAB)) }
		}
	} else {
		if (value is Node) {
			sb.append(value.asString(indent + TAB))
		} else {
			sb.append("$indent$TAB$value\n")
		}
	}
}
