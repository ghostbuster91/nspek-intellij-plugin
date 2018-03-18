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
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

class NSpekLineMarkerContributor : RunLineMarkerContributor() {

    private val TOOLTIP_PROVIDER = Function<PsiElement, String> { "Run nspec" }

    override fun getInfo(element: PsiElement): Info? {
        if (isIdentifier(element)) {
            val parent = element.parent
            when (parent) {
                is KtClassOrObject -> {
                    if (isJUnit4(parent)) {
                        return Info(
                                AllIcons.RunConfigurations.TestState.Run,
                                TOOLTIP_PROVIDER,
                                *ExecutorAction.getActions(0)
                        )
                    }
                }
                is KtSimpleNameExpression -> {
                    if (parent.mainReference.resolve()?.getKotlinFqName()?.asString() == "com.elpassion.nspek.NSpekMethodContext.o") {
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

fun isJUnit4(cls: KtClassOrObject): Boolean {
    val fqName = FqName("org.junit.runner.RunWith")
    return cls.findAnnotation(fqName) != null
}
