package com.gavwu.plugins.intellij.complexity.markers

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.ui.JBColor
import com.intellij.ui.TextIcon
import com.intellij.util.Function
import java.awt.Color
import java.awt.Font

class ComplexityLineMarkerInfo<T : PsiElement?>(
    complexity: Int,
    element: T,
    complexityType: String?
) : LineMarkerInfo<T>(
    element,
    element!!.textRange,
    getIcon(complexity),
    Function<T, String> { ignored: T ->
        String.format(
            "%s: %d",
            complexityType,
            complexity
        )
    },
    null,
    GutterIconRenderer.Alignment.CENTER
) {
    companion object {
        private fun getIcon(complexity: Int): TextIcon {
            val textIcon = TextIcon(
                getComplexityText(complexity),
                getForeground(complexity),
                getBackground(complexity),
                2
            )
            textIcon.setFont(Font(null, Font.PLAIN,  /* TODO: get editor font size */12))
            textIcon.setRound(3)
            return textIcon
        }

        private fun getComplexityText(complexity: Int): String {
            return if (complexity < 1e3) {
                complexity.toString()
            } else if (complexity < 1e6) {
                (complexity / 1000).toString() + "k"
            } else if (complexity < 1e9) {
                (complexity / 1000000).toString() + "m"
            } else {
                String.format("10^%.0f", Math.log10(complexity.toDouble()))
            }
        }

        private fun getForeground(complexity: Int): Color {
            if (complexity < 30) return JBColor.WHITE else if (complexity < 40) return JBColor.BLACK
            return JBColor.WHITE
        }

        private fun getBackground(complexity: Int): Color {
            if (complexity < 20) return JBColor.GREEN.brighter() else if (complexity < 30) return JBColor.GREEN else if (complexity < 40) return JBColor.YELLOW else if (complexity < 100) return JBColor.ORANGE else if (complexity < 1000) return JBColor.RED else if (complexity < 10000) return JBColor.RED.darker()
            return JBColor(Color(128, 0, 128), Color(220, 50, 220))
        }
    }
}