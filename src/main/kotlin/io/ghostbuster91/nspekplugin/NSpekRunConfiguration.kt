package io.ghostbuster91.nspekplugin

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.SettingsEditor
import java.util.*

class NSpekRunConfiguration(name: String, javaRunConfigurationModule: JavaRunConfigurationModule, configurationFactory: ConfigurationFactory)
    : ModuleBasedConfiguration<JavaRunConfigurationModule>(name, javaRunConfigurationModule, configurationFactory) {

    override fun getValidModules(): MutableCollection<Module> {
        return Arrays.asList(*ModuleManager.getInstance(project).modules)
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return NSpekSettingsEditor()
    }

    override fun getState(p0: Executor, p1: ExecutionEnvironment): RunProfileState? {
        return createCommandLineState(p1, GeneralCommandLine("./gradlew", "test").apply { setWorkDirectory(project.basePath) })
    }

    private fun createCommandLineState(environment: ExecutionEnvironment, commandLine: GeneralCommandLine): CommandLineState {
        return CommandLineState(environment, commandLine).apply {
            consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project)
        }
    }

    var testPath: String? = null
}