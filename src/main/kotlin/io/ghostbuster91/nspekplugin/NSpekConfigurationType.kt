package io.ghostbuster91.nspekplugin

import com.intellij.execution.configuration.ConfigurationFactoryEx
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project

class NSpekConfigurationType : ConfigurationTypeBase(
        "NSpekConfigurationType",
        "NSpek",
        "Create configuration to run test with nspek",
        AllIcons.General.Information) {

    init {
        addFactory(NSpekConfigurationFactory(this))
    }

    private class NSpekConfigurationFactory(configurationType: ConfigurationType) : ConfigurationFactoryEx<NSpekRunConfiguration>(configurationType) {
        override fun createTemplateConfiguration(p0: Project): RunConfiguration {
            return NSpekRunConfiguration("NSpek", p0, this)
        }
    }
}