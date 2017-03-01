package io.gitlab.arturbosch.grovlin.compiler.java.inmemory;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory java file manager.
 *
 * @author michael
 */
class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	// compiled classes in bytes:
	private final Map<String, byte[]> classBytes = new HashMap<>();

	MemoryJavaFileManager(JavaFileManager fileManager) {
		super(fileManager);
	}

	Map<String, byte[]> getClassBytes() {
		return new HashMap<>(this.classBytes);
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		classBytes.clear();
	}

	@Override
	public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, Kind kind,
											   FileObject sibling) throws IOException {
		if (kind == Kind.CLASS) {
			return new MemoryOutputJavaFileObject(className);
		} else {
			return super.getJavaFileForOutput(location, className, kind, sibling);
		}
	}

	JavaFileObject makeStringSource(String name, String code) {
		return new MemoryInputJavaFileObject(name, code);
	}

	static class MemoryInputJavaFileObject extends SimpleJavaFileObject {

		final String code;

		MemoryInputJavaFileObject(String name, String code) {
			super(URI.create("string:///" + name), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
			return CharBuffer.wrap(code);
		}
	}

	class MemoryOutputJavaFileObject extends SimpleJavaFileObject {
		final String name;

		MemoryOutputJavaFileObject(String name) {
			super(URI.create("string:///" + name), Kind.CLASS);
			this.name = name;
		}

		@Override
		public OutputStream openOutputStream() {
			return new FilterOutputStream(new ByteArrayOutputStream()) {
				@Override
				public void close() throws IOException {
					out.close();
					ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
					classBytes.put(name, bos.toByteArray());
				}
			};
		}

	}
}