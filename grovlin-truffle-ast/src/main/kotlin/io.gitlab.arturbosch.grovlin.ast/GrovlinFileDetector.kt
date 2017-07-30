package io.gitlab.arturbosch.grovlin.ast

import java.io.IOException
import java.nio.file.Path
import java.nio.file.spi.FileTypeDetector

/**
 * @author Artur Bosch
 */
class GrovlinFileDetector : FileTypeDetector() {
	@Throws(IOException::class)
	override fun probeContentType(path: Path): String? {
		val fileName = path.fileName.toString()
		if (fileName.endsWith(".gv") || fileName.endsWith(".grovlin")) {
			return GrovlinLanguage.MIME_TYPE
		}
		return null
	}
}
