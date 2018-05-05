package io.ghostbuster91.nspekplugin

import com.intellij.application.options.ModulesComboBox
import com.intellij.execution.ExecutionBundle
import com.intellij.execution.configurations.JavaRunConfigurationModule
import com.intellij.execution.junit.JUnitUtil
import com.intellij.execution.junit.TestClassFilter
import com.intellij.execution.testframework.SourceScope
import com.intellij.execution.ui.ClassBrowser
import com.intellij.ide.util.ClassFilter
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.psi.JavaCodeFragment
import com.intellij.psi.PsiClass
import com.intellij.ui.EditorTextFieldWithBrowseButton
import javax.swing.JPanel
import javax.swing.JTextField

class SettingsEditorPanel(
        private val module: JavaRunConfigurationModule,
        private val project: Project
) {
    lateinit var panel: JPanel
    lateinit var testPathTextInput: JTextField
    lateinit var classBrowserHolder: LabeledComponent<EditorTextFieldWithBrowseButton>
    lateinit var myModule: LabeledComponent<ModulesComboBox>

    private fun createUIComponents() {
        val modules = ModuleManager.getInstance(project).modules.toMutableList()
        myModule = LabeledComponent()
        myModule.component = ModulesComboBox().apply { setModules(modules) }
        classBrowserHolder = LabeledComponent()
        val classBrowser = TestClassBrowser(module, project, myModule.component)
        classBrowserHolder.component = EditorTextFieldWithBrowseButton(project, true, JavaCodeFragment.VisibilityChecker { declaration, place ->
            try {
                if (declaration is PsiClass && (classBrowser.filter.isAccepted(declaration) || classBrowser.findClass(declaration.qualifiedName) != null && place.parent != null)) {
                    return@VisibilityChecker JavaCodeFragment.VisibilityChecker.Visibility.VISIBLE
                }
            } catch (e: ClassBrowser.NoFilterException) {
                return@VisibilityChecker JavaCodeFragment.VisibilityChecker.Visibility.NOT_VISIBLE
            }
            JavaCodeFragment.VisibilityChecker.Visibility.NOT_VISIBLE
        })
        classBrowser.setField(classBrowserHolder.component)
    }
}

private class TestClassBrowser(
        private val module: JavaRunConfigurationModule,
        project: Project,
        private val component: ModulesComboBox
) : ClassBrowser(project, ExecutionBundle.message("choose.test.class.dialog.title")) {

    public override fun findClass(className: String?) = module.findClass(className)

    public override fun getFilter(): ClassFilter.ClassFilterWithScope {
        val classFilter: ClassFilter.ClassFilterWithScope
        try {
            classFilter = TestClassFilter.create(SourceScope.modulesWithDependencies(arrayOf(component.selectedModule)), module.module)
        } catch (e: JUnitUtil.NoJUnitException) {
            throw ClassBrowser.NoFilterException.noJUnitInModule(component.selectedModule)
        }
        return classFilter
    }
}