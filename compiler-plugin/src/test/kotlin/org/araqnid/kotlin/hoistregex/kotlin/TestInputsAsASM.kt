package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes

object TestInputsAsASM {
    fun originalSomeMethod(methodVisitor: MethodVisitor) {
        methodVisitor.visitAnnotableParameterCount(1, false)
        val annotationVisitor0 = methodVisitor.visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false)
        annotationVisitor0.visitEnd()
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitLdcInsn("input")
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "checkParameterIsNotNull",
            "(Ljava/lang/Object;Ljava/lang/String;)V",
            false
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLineNumber(7, label1)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/CharSequence")
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 2)
        methodVisitor.visitTypeInsn(Opcodes.NEW, "kotlin/text/Regex")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitLdcInsn("variablePattern")
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "kotlin/text/Regex",
            "<init>",
            "(Ljava/lang/String;)V",
            false
        )
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 3)
        methodVisitor.visitInsn(Opcodes.ICONST_0)
        methodVisitor.visitVarInsn(Opcodes.ISTORE, 4)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 3)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "kotlin/text/Regex",
            "matches",
            "(Ljava/lang/CharSequence;)Z",
            false
        )
        methodVisitor.visitInsn(Opcodes.IRETURN)
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitLocalVariable("this", "LtestInput/Example;", null, label0, label2, 0)
        methodVisitor.visitLocalVariable("input", "Ljava/lang/String;", null, label0, label2, 1)
        methodVisitor.visitMaxs(3, 5)
        methodVisitor.visitEnd()
    }

    fun originalSomeMethodUsingSingleOption(methodVisitor: MethodVisitor) {
        methodVisitor.visitAnnotableParameterCount(1, false)
        val annotationVisitor0 = methodVisitor.visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false)
        annotationVisitor0.visitEnd()
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitLdcInsn("input")
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "checkParameterIsNotNull",
            "(Ljava/lang/Object;Ljava/lang/String;)V",
            false
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLineNumber(11, label1)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/CharSequence")
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 2)
        methodVisitor.visitTypeInsn(Opcodes.NEW, "kotlin/text/Regex")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitLdcInsn("variablePatternWithOption")
        methodVisitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            "kotlin/text/RegexOption",
            "IGNORE_CASE",
            "Lkotlin/text/RegexOption;"
        )
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "kotlin/text/Regex",
            "<init>",
            "(Ljava/lang/String;Lkotlin/text/RegexOption;)V",
            false
        )
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 3)
        methodVisitor.visitInsn(Opcodes.ICONST_0)
        methodVisitor.visitVarInsn(Opcodes.ISTORE, 4)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 3)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "kotlin/text/Regex",
            "matches",
            "(Ljava/lang/CharSequence;)Z",
            false
        )
        methodVisitor.visitInsn(Opcodes.IRETURN)
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitLocalVariable("this", "LtestInput/Example;", null, label0, label2, 0)
        methodVisitor.visitLocalVariable("input", "Ljava/lang/String;", null, label0, label2, 1)
        methodVisitor.visitMaxs(4, 5)
        methodVisitor.visitEnd()
    }

    fun originalSomeMethodUsingMultipleOptions(methodVisitor: MethodVisitor) {
        methodVisitor.visitAnnotableParameterCount(1, false)
        val annotationVisitor0 = methodVisitor.visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false)
        annotationVisitor0.visitEnd()
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitLdcInsn("input")
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "checkParameterIsNotNull",
            "(Ljava/lang/Object;Ljava/lang/String;)V",
            false
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLineNumber(15, label1)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/CharSequence")
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 2)
        methodVisitor.visitTypeInsn(Opcodes.NEW, "kotlin/text/Regex")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitLdcInsn("variablePatternWithMultipleOptions")
        methodVisitor.visitInsn(Opcodes.ICONST_2)
        methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, "kotlin/text/RegexOption")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitInsn(Opcodes.ICONST_0)
        methodVisitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            "kotlin/text/RegexOption",
            "IGNORE_CASE",
            "Lkotlin/text/RegexOption;"
        )
        methodVisitor.visitInsn(Opcodes.AASTORE)
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitInsn(Opcodes.ICONST_1)
        methodVisitor.visitFieldInsn(
            Opcodes.GETSTATIC,
            "kotlin/text/RegexOption",
            "MULTILINE",
            "Lkotlin/text/RegexOption;"
        )
        methodVisitor.visitInsn(Opcodes.AASTORE)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "kotlin/collections/SetsKt",
            "setOf",
            "([Ljava/lang/Object;)Ljava/util/Set;",
            false
        )
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "kotlin/text/Regex",
            "<init>",
            "(Ljava/lang/String;Ljava/util/Set;)V",
            false
        )
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 3)
        methodVisitor.visitInsn(Opcodes.ICONST_0)
        methodVisitor.visitVarInsn(Opcodes.ISTORE, 4)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 3)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "kotlin/text/Regex",
            "matches",
            "(Ljava/lang/CharSequence;)Z",
            false
        )
        methodVisitor.visitInsn(Opcodes.IRETURN)
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitLocalVariable("this", "LtestInput/Example;", null, label0, label2, 0)
        methodVisitor.visitLocalVariable("input", "Ljava/lang/String;", null, label0, label2, 1)
        methodVisitor.visitMaxs(7, 5)
        methodVisitor.visitEnd()
    }
}
