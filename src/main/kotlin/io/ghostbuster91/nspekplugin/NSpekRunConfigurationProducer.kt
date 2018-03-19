package io.ghostbuster91.nspekplugin

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.execution.junit.JavaRunConfigurationProducerBase
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentsOfType
import org.jetbrains.kotlin.asJava.toLightClass
import org.jetbrains.kotlin.asJava.toLightMethods
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

class NSpekRunConfigurationProducer : JavaRunConfigurationProducerBase<NSpekRunConfiguration>(
        ConfigurationTypeUtil.findConfigurationType(NSpekConfigurationType::class.java)) {

    override fun setupConfigurationFromContext(configuration: NSpekRunConfiguration,
                                               context: ConfigurationContext,
                                               sourceElement: Ref<PsiElement>): Boolean {
        var configurationSet = false
        if (!sourceElement.isNull) {
            val element = sourceElement.get()!!
            if (isInKotlinFile(element) && isInsideAnnotatedTestFunction(element)) {
                val testPath = extractTestPath(element)
                if (testPath.isNotEmpty()) {
                    configuration.testPath = testPath
                    val classOrObject = element.getParentOfType<KtClassOrObject>(true)
                    configuration.setMainClass(classOrObject?.toLightClass())
                    configuration.persistentData.METHOD_NAME = element.getParentOfType<KtNamedFunction>(true)?.toLightMethods()?.first()?.name
                    configurationSet = true
                }
            }
        }

        if (configurationSet) {
            configuration.setModule(context.module)
            configuration.setGeneratedName()
        }

        return configurationSet
    }

    private fun extractTestPath(element: PsiElement): String {
        return element.parentsOfType<KtBinaryExpression>()
                .toList()
                .reversed()
                .map { it.firstChild.reference?.canonicalText }
                .joinToString("|")
    }

    override fun isConfigurationFromContext(configuration: NSpekRunConfiguration, context: ConfigurationContext): Boolean {
        val element = context.psiLocation
        if (element != null && isInKotlinFile(element) && isInsideAnnotatedTestFunction(element)) {
            val testPath = extractTestPath(element)
            return testPath.isNotEmpty() && configuration.testPath == testPath && configuration.configurationModule.module == context.module
        }
        return false
    }

    override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext) = true
}