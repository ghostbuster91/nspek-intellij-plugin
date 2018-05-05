package io.ghostbuster91.nspekplugin

import com.intellij.execution.ExecutionBundle
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RuntimeConfigurationWarning
import com.intellij.execution.junit.JUnitConfiguration
import com.intellij.execution.junit.JUnitUtil
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class NSpekRunConfiguration(name: String, project: Project, configurationFactory: ConfigurationFactory)
    : JUnitConfiguration(name, project, createData(), configurationFactory) {

    init {
        configurationModule.init()
    }

    override fun getValidModules(): MutableCollection<Module> {
        return ModuleManager.getInstance(project).modules.toMutableList()
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return NSpekSettingsEditor(configurationModule, project)
    }

    override fun checkConfiguration() {
        val testClassName = persistentData.mainClassName
        val testClass = configurationModule.checkModuleAndClassName(testClassName, ExecutionBundle.message("no.test.class.specified.error.text"))
        if (!JUnitUtil.isTestClass(testClass)) {
            throw RuntimeConfigurationWarning(ExecutionBundle.message("class.isnt.test.class.error.message", testClassName))
        }
        if (testPath.isNullOrBlank()) {
            throw RuntimeConfigurationWarning("Test path cannot be empty!")
        }
    }

    var testPath: String? = null
        set(value) {
            vmParameters = "-DtestPath=\"$value\""
            field = value
        }

    override fun suggestedName(): String? = testPath
}

private fun createData() = JUnitConfiguration.Data().apply {
    TEST_OBJECT = JUnitConfiguration.TEST_METHOD
}