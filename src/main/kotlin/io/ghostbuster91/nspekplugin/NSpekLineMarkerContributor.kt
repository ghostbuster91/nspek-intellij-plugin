package io.ghostbuster91.nspekplugin

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.util.Function
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

class NSpekLineMarkerContributor : RunLineMarkerContributor() {

    private val TOOLTIP_PROVIDER = Function<PsiElement, String> { "Run nspec" }

    override fun getInfo(element: PsiElement): Info? {
        if (isIdentifier(element)) {
            val parent = element.parent
            if (parent is KtSimpleNameExpression) {
                if (isInsideAnnotatedTestFunction(parent) && referenceResolveToMethodContextInvocation(parent)) {
                    return Info(
                            AllIcons.RunConfigurations.TestState.Run,
                            TOOLTIP_PROVIDER,
                            *ExecutorAction.getActions(0)
                    )
                }
            }
        }
        return null
    }
}