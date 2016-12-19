package org.engine;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
	
	private String vertexShaderSource, fragmentShaderSource, infoLog = " ";
	private int[] success = {0};
	private int vertex, fragment, program;
	
	/**
	 * 
	 * @param vertexShaderSource The file path to your vertex shader source
	 * @param fragmentShaderSource The file path to your fragment shader source
	 */
	public Shader(String vertexShaderSource, String fragmentShaderSource) {
		try {
			this.vertexShaderSource = readFile(vertexShaderSource, StandardCharsets.UTF_8);
			this.fragmentShaderSource = readFile(fragmentShaderSource, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		compileShaders();
	}
	
	/**
	 * Takes in a file path and returns the file as a String
	 * @param path
	 * @param encoding
	 * @return The file in String form
	 * @throws IOException
	 */
	private String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	/**
	 * Compile the shaders. Provides relevant info on failure
	 */
	private void compileShaders() {
		vertex = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertex, vertexShaderSource);
		glCompileShader(vertex);
		glGetShaderiv(vertex, GL_COMPILE_STATUS, success);
		if(success[0] == 0) {
			infoLog = glGetShaderInfoLog(vertex);
			System.out.println("ERROR::SHADER::VERTEX::COMPILATION_FAILED\n" + infoLog);
		}
		
		fragment = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragment, fragmentShaderSource);
		glCompileShader(fragment);
		glGetShaderiv(fragment, GL_COMPILE_STATUS, success);
		if(success[0] == 0) {
			infoLog = glGetShaderInfoLog(fragment);
			System.out.println("ERROR::SHADER::FRAGMENT::COMPILATION_FAILED\n" + infoLog);
		}
		
		program = glCreateProgram();
		
		glAttachShader(program, vertex);
		glAttachShader(program, fragment);
		glLinkProgram(program);
		
		glGetProgramiv(program, GL_LINK_STATUS, success);
		
		if(success[0] == 0) {
			infoLog = glGetProgramInfoLog(program);
			System.out.println("ERROR::SHADER::PROGRAM::LINKING_FAILED\n" + infoLog);
		}
		
		glDeleteShader(vertex);
		glDeleteShader(fragment);
	}
	
	/**
	 * Use the generated shader program
	 */
	public void use() {
		glUseProgram(program);
	}
	
	public int getProgramID() {
		return this.program;
	}
}
