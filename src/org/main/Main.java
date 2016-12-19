package org.main;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.Callback;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;

import org.engine.Model;
import org.engine.Shader;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Main {

	private GLFWErrorCallback 		errorCallback;
	private GLFWKeyCallback 		keyCallback;
	private GLFWMouseButtonCallback	mouseCallback;
	private GLFWWindowSizeCallback	wsCallback;
	private Callback				debugProc;
	
	private long window;
	
	public static final int WIDTH = 1280, HEIGHT = 720;
	
	private float deltaTime = 0.0f;
	private float lastFrame = 0.0f;
	
	private boolean keys[] = new boolean[1024];
	private boolean firstMouse = true;
	
	float yaw   =  -90.0f;	// Yaw is initialized to -90.0 degrees since a yaw of 0.0 results in a direction vector pointing to the right (due to how Eular angles work) so we initially rotate a bit to the left.
	float pitch =  0.0f;
	float lastX =  (float) (640 / 2.0);
	float lastY =  (float) (480 / 2.0);
	
	private Vector3f eye = new Vector3f(0.0f, 0.0f, 3.0f);
	private Vector3f center = new Vector3f(0.0f, 0.0f, -1.0f);
	private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
	
	private Vector3f lightPos = new Vector3f(1.2f, 1.0f, 2.0f);
	
	private float[] vertices = {
		-0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
		 0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f, 
		 0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f, 
		 0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f, 
		-0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f, 
		-0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f, 
		
		-0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
		 0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
		 0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
		 0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
		-0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
		-0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
		
		-0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
		-0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
		-0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
		-0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
		-0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
		-0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
		
		 0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
		 0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
		 0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
		 0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
		 0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
		 0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
		
		-0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
		 0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
		 0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
		 0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
		-0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
		-0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
		
		-0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
		 0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
		 0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
		 0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
		-0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
		-0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
	};

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		try {
			init();
			loop();

			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
		} finally {
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}
	}

	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();

		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
		    if (key >= 0 && key < 1024) {
		        if (action == GLFW_PRESS)
		            keys[key] = true;
		        else if (action == GLFW_RELEASE)
		            keys[key] = false;
		    }
		});
		
		glfwSetCursorPosCallback(window, (long window, double xpos, double ypos) -> {
			
		    if (firstMouse)
		    {
		        lastX = (float)xpos;
		        lastY = (float)ypos;
		        firstMouse = false;
		    }
			
			float xoffset = (float)xpos - lastX;
			float yoffset = lastY - (float)ypos; // Reversed since y-coordinates range from bottom to top
			lastX = (float)xpos;
			lastY = (float)ypos;
			
		    float sensitivity = 0.04f;	// Change this value to your liking
		    xoffset *= sensitivity;
		    yoffset *= sensitivity;
		    
		    yaw   += xoffset;
		    pitch += yoffset;
		    
		    // Make sure that when pitch is out of bounds, screen doesn't get flipped
		    if (pitch > 89.0f)
		        pitch = 89.0f;
		    if (pitch < -89.0f)
		        pitch = -89.0f;
		    
		    Vector3f front = new Vector3f();
		    front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		    front.y = (float) Math.sin(Math.toRadians(pitch));
		    front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		    front.normalize(center);
		});
		
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(
			window,
			(vidmode.width() - WIDTH) / 2,
			(vidmode.height() - HEIGHT) / 2
		);

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
	}

	private void loop() {
		GL.createCapabilities();
		
		debugProc = GLUtil.setupDebugMessageCallback();
		
		Shader containerShaderProgram = new Shader("Shaders/container.vs", "Shaders/container.fs");
		Shader lampShaderProgram = new Shader("Shaders/lamp.vs", "Shaders/lamp.fs");
		Model container = new Model(vertices, 2);
		Model lamp = new Model(vertices, 1);
		
		while ( !glfwWindowShouldClose(window) ) {
			float currentFrame = (float) glfwGetTime();
			deltaTime = currentFrame - lastFrame;
			lastFrame = currentFrame;  
			
			glfwPollEvents();
			doMovement();
			
			glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			// *************************Container Drawing*************************
			containerShaderProgram.use();
			int objectColorLoc = glGetUniformLocation(containerShaderProgram.getProgramID(), "objectColor");
			int lightColorLoc = glGetUniformLocation(containerShaderProgram.getProgramID(), "lightColor");
			int lightPosLoc    = glGetUniformLocation(containerShaderProgram.getProgramID(), "lightPos");
	        glUniform3f(objectColorLoc, 1.0f, 0.5f, 0.31f);
	        glUniform3f(lightColorLoc,  1.0f, 0.5f, 1.0f);
	        glUniform3f(lightPosLoc,    lightPos.x, lightPos.y, lightPos.z);
			
	        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
	        
	        Vector3f eyeCenter = new Vector3f();
	        eye.add(center, eyeCenter);
	        Matrix4f view = new Matrix4f().lookAt(eye, eyeCenter, up);
	        int viewLoc = glGetUniformLocation(containerShaderProgram.getProgramID(), "view");
	        view.get(fb);
	        glUniformMatrix4fv(viewLoc, false, fb);
	        
			Matrix4f projection = new Matrix4f().perspective((float)Math.toRadians(45.0), (float)640/480, 0.1f, 100.0f);
			int projectionLoc = glGetUniformLocation(containerShaderProgram.getProgramID(), "projection");
			projection.get(fb);
			glUniformMatrix4fv(projectionLoc, false, fb);
			
			container.bind();
			
			Matrix4f model = new Matrix4f();
			int modelLoc = glGetUniformLocation(containerShaderProgram.getProgramID(), "model");
			model.get(fb);
			glUniformMatrix4fv(modelLoc, false, fb);
			glDrawArrays(GL_TRIANGLES, 0, 36);
			
			glBindVertexArray(0);
	        
	        // *************************Lamp Drawing*************************
			lampShaderProgram.use();
	        modelLoc = glGetUniformLocation(lampShaderProgram.getProgramID(), "model");
	        viewLoc  = glGetUniformLocation(lampShaderProgram.getProgramID(), "view");
	        projectionLoc  = glGetUniformLocation(lampShaderProgram.getProgramID(), "projection");
	        
	        view = new Matrix4f().lookAt(eye, eyeCenter, up);
	        viewLoc = glGetUniformLocation(lampShaderProgram.getProgramID(), "view");
	        view.get(fb);
	        glUniformMatrix4fv(viewLoc, false, fb);
	        
			projection = new Matrix4f().perspective((float)Math.toRadians(45.0), (float)640/480, 0.1f, 100.0f);
			projectionLoc = glGetUniformLocation(lampShaderProgram.getProgramID(), "projection");
			projection.get(fb);
			glUniformMatrix4fv(projectionLoc, false, fb);
	        
	        model = new Matrix4f();
	        model.translate(lightPos);
	        model.scale(new Vector3f(0.2f));
	        model.get(fb);
	        glUniformMatrix4fv(modelLoc, false, fb);
	        
	        lamp.bind();
	        glDrawArrays(GL_TRIANGLES, 0, 36);
	        glBindVertexArray(0);
	        
			glfwSwapBuffers(window);
		}
	}
	
	private void doMovement() {
		float cameraSpeed = 2.0f * deltaTime;
	    if(keys[GLFW_KEY_W]) {
	    	Vector3f temp = new Vector3f();
	    	center.mul(cameraSpeed, temp);
	    	eye.add(temp);
	    }
		if(keys[GLFW_KEY_S]) {
			Vector3f temp = new Vector3f();
	    	center.mul(cameraSpeed, temp);
	    	eye.sub(temp);
		}
		if(keys[GLFW_KEY_A]) {
			Vector3f temp = new Vector3f();
			center.cross(up, temp);
			temp.normalize();
			temp.mul(cameraSpeed);
			eye.sub(temp);
	    }
		if(keys[GLFW_KEY_D]) {
			Vector3f temp = new Vector3f();
			center.cross(up, temp);
			temp.normalize();
			temp.mul(cameraSpeed);
			eye.add(temp);
		}	
	}

	public static void main(String[] args) {
		new Main().run();
	}

}








