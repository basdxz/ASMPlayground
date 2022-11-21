package com.github.basdxz.asmplayground;

import lombok.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.io.File;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        // Loading the class, ignore
        val clazz = TestClass.class;
        val className = clazz.getName();
        val classAsPath = className.replace('.', '/') + ".class";
        val stream = clazz.getClassLoader().getResourceAsStream(classAsPath);
        assert stream != null;
        val bytes = IOUtils.toByteArray(stream);

        // Creates and fills the class node
        val classNode = new ClassNode(ASM9);
        val classReader = new ClassReader(bytes);
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        // Being lazy and just grabbing the second method, the first is the constructor
        val methodNode = classNode.methods.get(1);

        // New list of instructions and label
        val newInstructions = new InsnList();
        val label = new LabelNode();

        // Loads the local variables x and y, then invokes the static method
        newInstructions.add(new VarInsnNode(ILOAD, 1));
        newInstructions.add(new VarInsnNode(ILOAD, 2));
        newInstructions.add(new MethodInsnNode(INVOKESTATIC, "com/github/basdxz/asmplayground/TestClass", "injectedMethod", "(II)Z", false));

        // Jumps down to the label if the result is equal to true
        newInstructions.add(new JumpInsnNode(IFEQ, label));

        // Loads false (0) onto the stack
        newInstructions.add(new InsnNode(ICONST_0));

        // Returns the boolean (technically and int in bytecode) off the stack, leaving the method
        newInstructions.add(new InsnNode(IRETURN));

        // The label to resume the method as normal
        newInstructions.add(label);

        // Inserts the created instructions at the start of the method
        methodNode.instructions.insert(newInstructions);

        // Writes and saves the class to disk
        val classWriter = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
        classNode.accept(classWriter);
        val file = new File("TestClass.class");
        FileUtils.writeByteArrayToFile(file, classWriter.toByteArray());
    }
}