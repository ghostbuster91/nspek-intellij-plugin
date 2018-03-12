package io.ghostbuster91.nspekplugin

import com.intellij.openapi.options.SettingsEditor
import javax.swing.JComponent

class NSpekSettingsEditor : SettingsEditor<NSpekRunConfiguration>() {

    private val editorPanel = SettingsEditorPanel()

    override fun createEditor(): JComponent {
        return editorPanel.panel
    }

    override fun applyEditorTo(p0: NSpekRunConfiguration) {

    }

    override fun resetEditorFrom(p0: NSpekRunConfiguration) {

    }
}