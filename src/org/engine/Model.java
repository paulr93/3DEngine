package org.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Model {
	private float[] vertices;
	private int vbo, vao, numAttribs;
	
	public Model(float[] vertices, int numAttribs) {
		this.vertices = vertices;
		this.numAttribs = numAttribs;
		init();
	}
	
	private void init() {
		vao = glGenVertexArrays();
		vbo = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		
		glBindVertexArray(vao);
		
		for(int i = 0; i < numAttribs; i++) {
			glVertexAttribPointer(i, 3, GL_FLOAT, false, 6 * 4, i * 3 * 4);
			glEnableVertexAttribArray(i);
		}
		
		glBindVertexArray(0);
		
	}
	
	public void bind() {
		glBindVertexArray(vao);
	}
}
