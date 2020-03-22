package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes.AASTORE
import org.jetbrains.org.objectweb.asm.Opcodes.ALOAD
import org.jetbrains.org.objectweb.asm.Opcodes.ANEWARRAY
import org.jetbrains.org.objectweb.asm.Opcodes.ASTORE
import org.jetbrains.org.objectweb.asm.Opcodes.BIPUSH
import org.jetbrains.org.objectweb.asm.Opcodes.CHECKCAST
import org.jetbrains.org.objectweb.asm.Opcodes.DUP
import org.jetbrains.org.objectweb.asm.Opcodes.F_FULL
import org.jetbrains.org.objectweb.asm.Opcodes.F_SAME
import org.jetbrains.org.objectweb.asm.Opcodes.F_SAME1
import org.jetbrains.org.objectweb.asm.Opcodes.GETSTATIC
import org.jetbrains.org.objectweb.asm.Opcodes.GOTO
import org.jetbrains.org.objectweb.asm.Opcodes.ICONST_0
import org.jetbrains.org.objectweb.asm.Opcodes.ICONST_1
import org.jetbrains.org.objectweb.asm.Opcodes.ICONST_2
import org.jetbrains.org.objectweb.asm.Opcodes.ICONST_3
import org.jetbrains.org.objectweb.asm.Opcodes.ICONST_4
import org.jetbrains.org.objectweb.asm.Opcodes.ICONST_5
import org.jetbrains.org.objectweb.asm.Opcodes.IFEQ
import org.jetbrains.org.objectweb.asm.Opcodes.IFNE
import org.jetbrains.org.objectweb.asm.Opcodes.INTEGER
import org.jetbrains.org.objectweb.asm.Opcodes.INVOKESPECIAL
import org.jetbrains.org.objectweb.asm.Opcodes.INVOKESTATIC
import org.jetbrains.org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.jetbrains.org.objectweb.asm.Opcodes.IRETURN
import org.jetbrains.org.objectweb.asm.Opcodes.ISTORE
import org.jetbrains.org.objectweb.asm.Opcodes.NEW


object TestInputsAsASM {
    fun originalSomeMethod(methodVisitor: MethodVisitor) {
        methodVisitor.visitAnnotableParameterCount(1, false)
        val annotationVisitor0 = methodVisitor.visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false)
        annotationVisitor0.visitEnd()
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitLdcInsn("input")
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "checkParameterIsNotNull",
            "(Ljava/lang/Object;Ljava/lang/String;)V",
            false
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLineNumber(7, label1)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/CharSequence")
        methodVisitor.visitVarInsn(ASTORE, 2)
        methodVisitor.visitTypeInsn(NEW, "kotlin/text/Regex")
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitLdcInsn("variablePattern")
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL,
            "kotlin/text/Regex",
            "<init>",
            "(Ljava/lang/String;)V",
            false
        )
        methodVisitor.visitVarInsn(ASTORE, 3)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 4)
        methodVisitor.visitVarInsn(ALOAD, 3)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "kotlin/text/Regex",
            "matches",
            "(Ljava/lang/CharSequence;)Z",
            false
        )
        methodVisitor.visitInsn(IRETURN)
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
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitLdcInsn("input")
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "checkParameterIsNotNull",
            "(Ljava/lang/Object;Ljava/lang/String;)V",
            false
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLineNumber(11, label1)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/CharSequence")
        methodVisitor.visitVarInsn(ASTORE, 2)
        methodVisitor.visitTypeInsn(NEW, "kotlin/text/Regex")
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitLdcInsn("variablePatternWithOption")
        methodVisitor.visitFieldInsn(
            GETSTATIC,
            "kotlin/text/RegexOption",
            "IGNORE_CASE",
            "Lkotlin/text/RegexOption;"
        )
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL,
            "kotlin/text/Regex",
            "<init>",
            "(Ljava/lang/String;Lkotlin/text/RegexOption;)V",
            false
        )
        methodVisitor.visitVarInsn(ASTORE, 3)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 4)
        methodVisitor.visitVarInsn(ALOAD, 3)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "kotlin/text/Regex",
            "matches",
            "(Ljava/lang/CharSequence;)Z",
            false
        )
        methodVisitor.visitInsn(IRETURN)
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
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitLdcInsn("input")
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "checkParameterIsNotNull",
            "(Ljava/lang/Object;Ljava/lang/String;)V",
            false
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLineNumber(15, label1)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/CharSequence")
        methodVisitor.visitVarInsn(ASTORE, 2)
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitLineNumber(16, label2)
        methodVisitor.visitTypeInsn(NEW, "kotlin/text/Regex")
        methodVisitor.visitInsn(DUP)
        val label3 = Label()
        methodVisitor.visitLabel(label3)
        methodVisitor.visitLineNumber(17, label3)
        methodVisitor.visitLdcInsn("variablePatternWithMultipleOptions")
        val label4 = Label()
        methodVisitor.visitLabel(label4)
        methodVisitor.visitLineNumber(18, label4)
        methodVisitor.visitInsn(ICONST_2)
        methodVisitor.visitTypeInsn(ANEWARRAY, "kotlin/text/RegexOption")
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/text/RegexOption", "IGNORE_CASE", "Lkotlin/text/RegexOption;")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitInsn(ICONST_1)
        methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/text/RegexOption", "MULTILINE", "Lkotlin/text/RegexOption;")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/collections/SetsKt",
            "setOf",
            "([Ljava/lang/Object;)Ljava/util/Set;",
            false
        )
        val label5 = Label()
        methodVisitor.visitLabel(label5)
        methodVisitor.visitLineNumber(16, label5)
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL,
            "kotlin/text/Regex",
            "<init>",
            "(Ljava/lang/String;Ljava/util/Set;)V",
            false
        )
        methodVisitor.visitVarInsn(ASTORE, 3)
        val label6 = Label()
        methodVisitor.visitLabel(label6)
        methodVisitor.visitLineNumber(15, label6)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 4)
        methodVisitor.visitVarInsn(ALOAD, 3)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "kotlin/text/Regex",
            "matches",
            "(Ljava/lang/CharSequence;)Z",
            false
        )
        methodVisitor.visitInsn(IRETURN)
        val label7 = Label()
        methodVisitor.visitLabel(label7)
        methodVisitor.visitLocalVariable("this", "LtestInput/Example;", null, label0, label7, 0)
        methodVisitor.visitLocalVariable("input", "Ljava/lang/String;", null, label0, label7, 1)
        methodVisitor.visitMaxs(7, 5)
        methodVisitor.visitEnd()
    }

    fun originalSomeMethodUsingLotsaOptions(methodVisitor: MethodVisitor) {
        methodVisitor.visitAnnotableParameterCount(1, false)
        val annotationVisitor0 = methodVisitor.visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false)
        annotationVisitor0.visitEnd()
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitLdcInsn("input")
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "checkParameterIsNotNull",
            "(Ljava/lang/Object;Ljava/lang/String;)V",
            false
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLineNumber(24, label1)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/CharSequence")
        methodVisitor.visitVarInsn(ASTORE, 2)
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitLineNumber(25, label2)
        methodVisitor.visitTypeInsn(NEW, "kotlin/text/Regex")
        methodVisitor.visitInsn(DUP)
        val label3 = Label()
        methodVisitor.visitLabel(label3)
        methodVisitor.visitLineNumber(26, label3)
        methodVisitor.visitLdcInsn("variablePatternWithMultipleOptions")
        val label4 = Label()
        methodVisitor.visitLabel(label4)
        methodVisitor.visitLineNumber(27, label4)
        methodVisitor.visitIntInsn(BIPUSH, 8)
        methodVisitor.visitTypeInsn(ANEWARRAY, "kotlin/text/RegexOption")
        methodVisitor.visitInsn(DUP)
        val label5 = Label()
        methodVisitor.visitLabel(label5)
        methodVisitor.visitLineNumber(28, label5)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/text/RegexOption", "IGNORE_CASE", "Lkotlin/text/RegexOption;")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitInsn(DUP)
        val label6 = Label()
        methodVisitor.visitLabel(label6)
        methodVisitor.visitLineNumber(29, label6)
        methodVisitor.visitInsn(ICONST_1)
        methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/text/RegexOption", "MULTILINE", "Lkotlin/text/RegexOption;")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitInsn(DUP)
        val label7 = Label()
        methodVisitor.visitLabel(label7)
        methodVisitor.visitLineNumber(30, label7)
        methodVisitor.visitInsn(ICONST_2)
        methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/text/RegexOption", "IGNORE_CASE", "Lkotlin/text/RegexOption;")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitInsn(DUP)
        val label8 = Label()
        methodVisitor.visitLabel(label8)
        methodVisitor.visitLineNumber(31, label8)
        methodVisitor.visitInsn(ICONST_3)
        methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/text/RegexOption", "MULTILINE", "Lkotlin/text/RegexOption;")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitInsn(DUP)
        val label9 = Label()
        methodVisitor.visitLabel(label9)
        methodVisitor.visitLineNumber(32, label9)
        methodVisitor.visitInsn(ICONST_4)
        methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/text/RegexOption", "IGNORE_CASE", "Lkotlin/text/RegexOption;")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitInsn(DUP)
        val label10 = Label()
        methodVisitor.visitLabel(label10)
        methodVisitor.visitLineNumber(33, label10)
        methodVisitor.visitInsn(ICONST_5)
        methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/text/RegexOption", "MULTILINE", "Lkotlin/text/RegexOption;")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitInsn(DUP)
        val label11 = Label()
        methodVisitor.visitLabel(label11)
        methodVisitor.visitLineNumber(34, label11)
        methodVisitor.visitIntInsn(BIPUSH, 6)
        methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/text/RegexOption", "IGNORE_CASE", "Lkotlin/text/RegexOption;")
        methodVisitor.visitInsn(AASTORE)
        methodVisitor.visitInsn(DUP)
        val label12 = Label()
        methodVisitor.visitLabel(label12)
        methodVisitor.visitLineNumber(35, label12)
        methodVisitor.visitIntInsn(BIPUSH, 7)
        methodVisitor.visitFieldInsn(GETSTATIC, "kotlin/text/RegexOption", "MULTILINE", "Lkotlin/text/RegexOption;")
        methodVisitor.visitInsn(AASTORE)
        val label13 = Label()
        methodVisitor.visitLabel(label13)
        methodVisitor.visitLineNumber(27, label13)
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/collections/SetsKt",
            "setOf",
            "([Ljava/lang/Object;)Ljava/util/Set;",
            false
        )
        val label14 = Label()
        methodVisitor.visitLabel(label14)
        methodVisitor.visitLineNumber(25, label14)
        methodVisitor.visitMethodInsn(
            INVOKESPECIAL,
            "kotlin/text/Regex",
            "<init>",
            "(Ljava/lang/String;Ljava/util/Set;)V",
            false
        )
        methodVisitor.visitVarInsn(ASTORE, 3)
        val label15 = Label()
        methodVisitor.visitLabel(label15)
        methodVisitor.visitLineNumber(24, label15)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 4)
        methodVisitor.visitVarInsn(ALOAD, 3)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "kotlin/text/Regex",
            "matches",
            "(Ljava/lang/CharSequence;)Z",
            false
        )
        methodVisitor.visitInsn(IRETURN)
        val label16 = Label()
        methodVisitor.visitLabel(label16)
        methodVisitor.visitLocalVariable("this", "LtestInput/Example;", null, label0, label16, 0)
        methodVisitor.visitLocalVariable("input", "Ljava/lang/String;", null, label0, label16, 1)
        methodVisitor.visitMaxs(7, 5)
        methodVisitor.visitEnd()
    }

    fun originalSomeMethodUsingSamePatternTwice(methodVisitor: MethodVisitor) {
        methodVisitor.visitAnnotableParameterCount(1, false)
        val annotationVisitor0 = methodVisitor.visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false)
        annotationVisitor0.visitEnd()
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitLdcInsn("input")
        methodVisitor.visitMethodInsn(
            INVOKESTATIC,
            "kotlin/jvm/internal/Intrinsics",
            "checkParameterIsNotNull",
            "(Ljava/lang/Object;Ljava/lang/String;)V",
            false
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLineNumber(42, label1)
        methodVisitor.visitTypeInsn(NEW, "kotlin/text/Regex")
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitLdcInsn("variablePattern")
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "kotlin/text/Regex", "<init>", "(Ljava/lang/String;)V", false)
        methodVisitor.visitVarInsn(ASTORE, 2)
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitLineNumber(43, label2)
        methodVisitor.visitTypeInsn(NEW, "kotlin/text/Regex")
        methodVisitor.visitInsn(DUP)
        methodVisitor.visitLdcInsn("variablePattern")
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "kotlin/text/Regex", "<init>", "(Ljava/lang/String;)V", false)
        methodVisitor.visitVarInsn(ASTORE, 3)
        val label3 = Label()
        methodVisitor.visitLabel(label3)
        methodVisitor.visitLineNumber(44, label3)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/CharSequence")
        methodVisitor.visitVarInsn(ASTORE, 4)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 5)
        methodVisitor.visitVarInsn(ALOAD, 2)
        methodVisitor.visitVarInsn(ALOAD, 4)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "kotlin/text/Regex",
            "matches",
            "(Ljava/lang/CharSequence;)Z",
            false
        )
        val label4 = Label()
        methodVisitor.visitJumpInsn(IFNE, label4)
        val label5 = Label()
        methodVisitor.visitLabel(label5)
        methodVisitor.visitLineNumber(44, label5)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/CharSequence")
        methodVisitor.visitVarInsn(ASTORE, 4)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitVarInsn(ISTORE, 5)
        methodVisitor.visitVarInsn(ALOAD, 3)
        methodVisitor.visitVarInsn(ALOAD, 4)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "kotlin/text/Regex",
            "matches",
            "(Ljava/lang/CharSequence;)Z",
            false
        )
        val label6 = Label()
        methodVisitor.visitJumpInsn(IFEQ, label6)
        methodVisitor.visitLabel(label4)
        methodVisitor.visitFrame(
            F_FULL,
            6,
            arrayOf<Any>(
                "testInput/Example",
                "java/lang/String",
                "kotlin/text/Regex",
                "kotlin/text/Regex",
                "java/lang/CharSequence",
                INTEGER
            ),
            0,
            arrayOf()
        )
        methodVisitor.visitInsn(ICONST_1)
        val label7 = Label()
        methodVisitor.visitJumpInsn(GOTO, label7)
        methodVisitor.visitLabel(label6)
        methodVisitor.visitFrame(F_SAME, 0, null, 0, null)
        methodVisitor.visitInsn(ICONST_0)
        methodVisitor.visitLabel(label7)
        methodVisitor.visitFrame(
            F_SAME1,
            0,
            null,
            1,
            arrayOf<Any>(INTEGER)
        )
        methodVisitor.visitInsn(IRETURN)
        val label8 = Label()
        methodVisitor.visitLabel(label8)
        methodVisitor.visitLocalVariable("p2", "Lkotlin/text/Regex;", null, label3, label8, 3)
        methodVisitor.visitLocalVariable("p1", "Lkotlin/text/Regex;", null, label2, label8, 2)
        methodVisitor.visitLocalVariable("this", "LtestInput/Example;", null, label0, label8, 0)
        methodVisitor.visitLocalVariable("input", "Ljava/lang/String;", null, label0, label8, 1)
        methodVisitor.visitMaxs(3, 6)
        methodVisitor.visitEnd()
    }
}
