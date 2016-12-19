#version 330 core
out vec4 color;

// The lamp doesn't take in colours from the program, just outputs white light

void main()
{
    color = vec4(1.0f); // Set alle 4 vector values to 1.0f
}