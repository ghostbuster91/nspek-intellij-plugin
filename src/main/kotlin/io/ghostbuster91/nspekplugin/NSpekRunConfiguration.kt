package io.ghostbuster91.nspekplugin

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class NSpekRunConfiguration(name: String, project: Project, configurationFactory: ConfigurationFactory)
    : LocatableConfigurationBase(project, configurationFactory, name) {

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
}