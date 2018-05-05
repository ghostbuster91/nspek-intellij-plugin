package io.ghostbuster91.nspekplugin

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.junit.JUnitConfiguration
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import java.util.*

class NSpekRunConfiguration(name: String, javaRunConfigurationModule: Project, configurationFactory: ConfigurationFactory)
    : JUnitConfiguration(name, javaRunConfigurationModule, createData(), configurationFactory) {

    override fun getValidModules(): MutableCollection<Module> {
        return Arrays.asList(*ModuleManager.getInstance(project).modules)
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return NSpekSettingsEditor()
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