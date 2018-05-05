package io.ghostbuster91.nspekplugin

import com.intellij.execution.configurations.JavaRunConfigurationModule
import com.intellij.execution.junit.JUnitUtil
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import javax.swing.JComponent

class NSpekSettingsEditor(
        module: JavaRunConfigurationModule,
        private val project: Project
) : SettingsEditor<NSpekRunConfiguration>() {

    private val editorPanel = SettingsEditorPanel(module, project)

    override fun createEditor(): JComponent {
        return editorPanel.panel
    }

    override fun applyEditorTo(configuration: NSpekRunConfiguration) {
        configuration.testPath = editorPanel.testPathTextInput.text
        val selectedModule = editorPanel.myModule.component.selectedModule
        configuration.setModule(selectedModule)
        val text = editorPanel.classBrowserHolder.component.text
        val testClass = if (!StringUtil.isEmptyOrSpaces(text)) JUnitUtil.findPsiClass(text, selectedModule, project) else null
        if (testClass != null && testClass.isValid) {
            configuration.setMainClass(testClass)
        }
    }

    override fun resetEditorFrom(configuration: NSpekRunConfiguration) {
        editorPanel.testPathTextInput.text = configuration.testPath
        editorPanel.classBrowserHolder.component.text = configuration.persistentData.mainClassName
        editorPanel.myModule.component.selectedModule = configuration.modules.firstOrNull() //TODO: It seems to be incorrect
    }
}