package io.gitlab.arturbosch.grovlin.compiler.args

import com.beust.jcommander.IStringConverter
import com.beust.jcommander.ParameterException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ExistingPathConverter : IStringConverter<Path> {
	override fun convert(value: String): Path {
		val config = Paths.get(value)
		if (Files.notExists(config))
			throw ParameterException("Provided path '$value' does not exist!")
		return config
	}
}