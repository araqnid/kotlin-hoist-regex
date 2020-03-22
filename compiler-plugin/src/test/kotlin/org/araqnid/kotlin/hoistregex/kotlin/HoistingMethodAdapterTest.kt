package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes.ALOAD
import org.jetbrains.org.objectweb.asm.Opcodes.ASTORE
import org.jetbrains.org.objectweb.asm.Opcodes.CHECKCAST
import org.jetbrains.org.objectweb.asm.Opcodes.DUP
import org.jetbrains.org.objectweb.asm.Opcodes.ICONST_0
import org.jetbrains.org.objectweb.asm.Opcodes.INVOKESPECIAL
import org.jetbrains.org.objectweb.asm.Opcodes.INVOKESTATIC
import org.jetbrains.org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.jetbrains.org.objectweb.asm.Opcodes.IRETURN
import org.jetbrains.org.objectweb.asm.Opcodes.ISTORE
import org.jetbrains.org.objectweb.asm.Opcodes.NEW
import org.jetbrains.org.objectweb.asm.util.Textifier
import org.jetbrains.org.objectweb.asm.util.TraceMethodVisitor
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Want to convert a sequence like this:
//       11: new           #19                 // class kotlin/text/Regex
//      14: dup
//      15: ldc           #21                 // String \\S+
//      17: invokespecial #25                 // Method kotlin/text/Regex."<init>":(Ljava/lang/String;)V
// into:
//    new   // class kotlin/text/Regex
//    getstatic // Field $regex$whatever on theClass
//    invokespecial

class HoistingMethodAdapterTest {
    @Test
    fun `extracts regex creations and assigns symbols`() {
        val textifier = Textifier()
        val allocatedPatterns = mutableMapOf<String, String>()
        val hostingAdapter = HoistingMethodAdapter("testInputs.Example", allocatedPatterns, TraceMethodVisitor(textifier))
        visitOriginalSomeMethod(hostingAdapter)
        val allText = textifier.getText().joinToString("")
        assertFalse(allText.contains("""
                |    NEW kotlin/text/Regex
                """.trimMargin()), "Method produced:\n\n$allText")
        assertTrue(allText.contains("""
                |    GETSTATIC testInputs.Example.${'$'}pattern${'$'}0 : Lkotlin/text/Regex;
                """.trimMargin()), "Method produced:\n\n$allText")
        assertEquals(allocatedPatterns, mapOf("\$pattern\$0" to "variablePattern"))
    }

    private fun visitOriginalSomeMethod(methodVisitor: MethodVisitor) {
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
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "kotlin/text/Regex", "<init>", "(Ljava/lang/String;)V", false)
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
}