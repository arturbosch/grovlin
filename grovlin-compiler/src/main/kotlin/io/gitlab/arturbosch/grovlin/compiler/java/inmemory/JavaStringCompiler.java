package io.gitlab.arturbosch.grovlin.compiler.java.inmemory;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In-memory compile Java source code as String.
 *
 * @author michael
 * @author Artur Bosch
 */
public class JavaStringCompiler {

	private JavaCompiler compiler;
	private StandardJavaFileManager stdManager;

	public JavaStringCompiler() {
		this.compiler = ToolProvider.getSystemJavaCompiler();
		this.stdManager = compiler.getStandardFileManager(null, null, null);
	}

	/**
	 * Compile Java sources in memory.
	 *
	 * @param fileNameToSources A map of sources with their class names.
	 * @return The compiled results as Map that contains class name as key,
	 * class binary as value.
	 * @throws IOException If compile error.
	 */
	public Map<String, byte[]> compile(Map<String, String> fileNameToSources) throws IOException {
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
			List<JavaFileObject> fileObjects = fileNameToSources.entrySet().stream()
					.map(it -> manager.makeStringSource(it.getKey(), it.getValue()))
					.collect(Collectors.toList());
			CompilationTask task = compiler.getTask(null, manager, null, null, null, fileObjects);
			Boolean result = task.call();
			if (result == null || !result) {
				throw new GrovlinRuntimeError("Compilation failed.");
			}
			return manager.getClassBytes();
		}
	}

	/**
	 * Load class from compiled classes.
	 *
	 * @param name       Full class name.
	 * @param classBytes Compiled results as a Map.
	 * @return The Class instance.
	 * @throws ClassNotFoundException If class not found.
	 * @throws IOException            If load error.
	 */
	private Class<?> loadClass(String name, Map<String, byte[]> classBytes) throws ClassNotFoundException, IOException {
		try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
			return classLoader.loadClass(name);
		}
	}

	public void run(String name, Map<String, byte[]> classBytes) throws IOException, ClassNotFoundException,
			NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
		Class<?> aClass = loadClass(name, classBytes);
		Method main = aClass.getMethod("main", String[].class);
		Object o = aClass.newInstance();
		main.invoke(o, (Object) null);
	}
}
