package io.ghostbuster91.nspekplugin

import com.intellij.application.options.ModulesComboBox
import com.intellij.execution.ExecutionBundle
import com.intellij.execution.junit.JUnitUtil
import com.intellij.execution.junit.TestClassFilter
import com.intellij.execution.testframework.SourceScope
import com.intellij.execution.ui.ClassBrowser
import com.intellij.execution.ui.ConfigurationModuleSelector
import com.intellij.ide.util.ClassFilter
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.psi.JavaCodeFragment
import com.intellij.psi.PsiClass
import com.intellij.ui.EditorTextFieldWithBrowseButton
import javax.swing.JPanel
import javax.swing.JTextField

class SettingsEditorPanel(private val project: Project) {
    lateinit var panel: JPanel
    lateinit var testPathTextInput: JTextField
    lateinit var classBrowserHolder: LabeledComponent<EditorTextFieldWithBrowseButton>
    lateinit var moduleSelectorHolder: LabeledComponent<ModulesComboBox>
    lateinit var moduleSelectorModel: ConfigurationModuleSelector

    private fun createUIComponents() {
        val modules = ModuleManager.getInstance(project).modules.toMutableList()
        moduleSelectorHolder = LabeledComponent()
        moduleSelectorHolder.component = ModulesComboBox().apply { setModules(modules) }
        moduleSelectorModel = ConfigurationModuleSelector(project, moduleSelectorHolder.component)
        classBrowserHolder = LabeledComponent()
        val classBrowser = TestClassBrowser(project, moduleSelectorModel)
        classBrowserHolder.component = EditorTextFieldWithBrowseButton(project, true, visibilityChecker(classBrowser))
        classBrowser.setField(classBrowserHolder.component)
    }

    private fun visibilityChecker(classBrowser: TestClassBrowser) =
            JavaCodeFragment.VisibilityChecker { declaration, place ->
                try {
                    if (declaration is PsiClass && (classBrowser.filter.isAccepted(declaration) || classBrowser.findClass(declaration.qualifiedName) != null && place.parent != null)) {
                        JavaCodeFragment.VisibilityChecker.Visibility.VISIBLE
                    } else {
                        JavaCodeFragment.VisibilityChecker.Visibility.NOT_VISIBLE
                    }
                } catch (e: ClassBrowser.NoFilterException) {
                    JavaCodeFragment.VisibilityChecker.Visibility.NOT_VISIBLE
                }
            }
}

private class TestClassBrowser(
        project: Project,
        private val moduleSelector: ConfigurationModuleSelector
) : ClassBrowser(project, ExecutionBundle.message("choose.test.class.dialog.title")) {

    public override fun findClass(className: String?) = moduleSelector.configurationModule.findClass(className)

    public override fun getFilter(): ClassFilter.ClassFilterWithScope {
        val classFilter: ClassFilter.ClassFilterWithScope
        try {
            classFilter = TestClassFilter.create(SourceScope.modulesWithDependencies(arrayOf(moduleSelector.module)), moduleSelector.module)
        } catch (e: JUnitUtil.NoJUnitException) {
            throw ClassBrowser.NoFilterException.noJUnitInModule(moduleSelector.module)
        }
        return classFilter
    }
}