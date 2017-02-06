package io.gitlab.arturbosch.grovlin.parser.ast

import java.lang.reflect.ParameterizedType
import kotlin.reflect.jvm.javaType
import kotlin.reflect.memberProperties

const val indentBlock = "\t"

fun Node.asString(indent: String = ""): String {
	val sb = StringBuilder()
	sb.append("$indent${javaClass.simpleName}\n")
	javaClass.kotlin.memberProperties.filter { it.name != "position" }.forEach {
		val type = it.returnType.javaType
		val value = it.get(this)
		if (type is ParameterizedType && type.rawType == List::class.java) {
			val paramType = type.actualTypeArguments[0]
			if (paramType is Class<*> && Node::class.java.isAssignableFrom(paramType)) {
				(value as List<*>).forEach { sb.append((it as Node).asString(indent + indentBlock)) }
			}
		} else {
			if (value is Node) {
				sb.append(value.asString(indent + indentBlock))
			} else {
				sb.append("$indent$indentBlock$value\n")
			}
		}
	}
	return sb.toString()
}