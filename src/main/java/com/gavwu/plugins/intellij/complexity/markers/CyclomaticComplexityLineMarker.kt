package com.gavwu.plugins.intellij.complexity.markers

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.psi.PsiElement
import com.siyeh.ig.classmetrics.CyclomaticComplexityVisitor

class CyclomaticComplexityLineMarker :
    AbstractComplexityLineMarker("Cyclomatic Complexity") {

    override fun <E : PsiElement, P : PsiElement> getComplexityMarkerInfo(
        elementToMark: E,
        vararg blocksToMeasure: P
    ): LineMarkerInfo<E>? {
        val visitor = CyclomaticComplexityVisitor()
        blocksToMeasure.forEach { it.accept(visitor); }
        val complexity = visitor.complexity
        return if (complexity <= 1) {
            null
        } else {
            ComplexityLineMarkerInfo(complexity, elementToMark, complexityType)
        }
    }
}
