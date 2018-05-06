package io.ghostbuster91.nspekplugin

import com.intellij.execution.junit.JUnitUtil
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import javax.swing.JComponent

class NSpekSettingsEditor(private val project: Project) : SettingsEditor<NSpekRunConfiguration>() {

    private val editorPanel = SettingsEditorPanel(project)

    override fun createEditor(): JComponent {
        return editorPanel.panel
    }

    override fun applyEditorTo(configuration: NSpekRunConfiguration) {
        configuration.testPath = editorPanel.testPathTextInput.text
        val text = editorPanel.classBrowserHolder.component.text
        configuration.setModule(editorPanel.moduleSelectorModel.module)
        val testClass = if (!StringUtil.isEmptyOrSpaces(text)) JUnitUtil.findPsiClass(text, editorPanel.moduleSelectorModel.module, project) else null
        if (testClass != null && testClass.isValid) {
            configuration.setMainClass(testClass)
        }
    }

    override fun resetEditorFrom(configuration: NSpekRunConfiguration) {
        editorPanel.testPathTextInput.text = configuration.testPath
        editorPanel.classBrowserHolder.component.text = configuration.persistentData.mainClassName
        editorPanel.moduleSelectorModel.reset(configuration)
    }
}