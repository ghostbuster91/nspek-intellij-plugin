package io.ghostbuster91.nspekplugin

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.util.Function
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

class NSpekLineMarkerContributor : RunLineMarkerContributor() {

    private val TOOLTIP_PROVIDER = Function<PsiElement, String> { "Run nspec" }

    override fun getInfo(element: PsiElement): Info? {
        if (isIdentifier(element)) {
            val parent = element.parent
            if (parent is KtSimpleNameExpression) {
                val classLevelFunction = parent.getParentOfType<KtNamedFunction>(true)
                if (classLevelFunction?.findAnnotation(NSPEK_TEST_ANNOTATION) != null) {
                    if (parent.mainReference.resolve()?.getKotlinFqName() == NSPEK_METHOD_CONTEXT_INVOCATION) {
                        return Info(
                                AllIcons.RunConfigurations.TestState.Run,
                                TOOLTIP_PROVIDER,
                                *ExecutorAction.getActions(0)
                        )
                    }
                }
            }
        }
        return null
    }

    companion object {
        val NSPEK_TEST_ANNOTATION = FqName("com.elpassion.nspek.Test")
        val NSPEK_METHOD_CONTEXT_INVOCATION = FqName("com.elpassion.nspek.NSpekMethodContext.o")
    }
}

fun isIdentifier(element: PsiElement): Boolean {
    val node = element.node
    if (node != null) {
        val elementType = node.elementType
        if (elementType is KtToken) {
            return elementType.toString() == "IDENTIFIER"
        }
    }
    return false
}