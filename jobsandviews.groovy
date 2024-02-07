// Import necessary classes
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext
import javaposse.jobdsl.dsl.helpers.wrapper.WrapperContext
import javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration
import javaposse.jobdsl.plugin.LookupStrategy
import javaposse.jobdsl.plugin.LookupStrategyContext
import javaposse.jobdsl.plugin.LookupStrategyFactory
import javaposse.jobdsl.plugin.LookupStrategyService
import javaposse.jobdsl.plugin.TranslateLookupContext
import javaposse.jobdsl.plugin.TranslateLookupStrategy
import javaposse.jobdsl.plugin.TranslationExtension
import javaposse.jobdsl.plugin.TranslationExtensionContext
import javaposse.jobdsl.plugin.TranslationExtensionFactory
import javaposse.jobdsl.plugin.TranslationExtensionPoint
import javaposse.jobdsl.plugin.TranslationExtensionService
import javaposse.jobdsl.plugin.TranslationWorkspace
import javaposse.jobdsl.plugin.gui.ConfigRoundTrip
import javaposse.jobdsl.plugin.gui.TranslationProgress
import javaposse.jobdsl.plugin.gui.TranslationProgressContext
import javaposse.jobdsl.plugin.parser.ParserContext
import javaposse.jobdsl.plugin.sandbox.AbstractSandbox
import javaposse.jobdsl.plugin.scm.ScmContext
import javaposse.jobdsl.plugin.scms.GitContext
import javaposse.jobdsl.plugin.scms.GitContextFactory
import javaposse.jobdsl.plugin.scms.ScmContext
import javaposse.jobdsl.plugin.scmsteps.CheckoutContext
import javaposse.jobdsl.plugin.scms.CheckoutContextFactory
import javaposse.jobdsl.plugin.scmsteps.CheckoutStep
import javaposse.jobdsl.plugin.scmsteps.CheckoutStepContext
import javaposse.jobdsl.plugin.scmsteps.CheckoutStepFactory
import javaposse.jobdsl.plugin.scmsteps.CheckoutStepFactoryProvider
import javaposse.jobdsl.plugin.scmsteps.CheckoutStepProvider
import javaposse.jobdsl.plugin.scmsteps.CheckoutStepService
import javaposse.jobdsl.plugin.scmsteps.CheckoutStepSource
import javaposse.jobdsl.plugin.scmsteps.GitStep
import javaposse.jobdsl.plugin.scmsteps.GitStepContext
import javaposse.jobdsl.plugin.scmsteps.GitStepFactory
import javaposse.jobdsl.plugin.scmsteps.GitStepFactoryProvider
import javaposse.jobdsl.plugin.scmsteps.GitStepProvider
import javaposse.jobdsl.plugin.scmsteps.GitStepService
import javaposse.jobdsl.plugin.scmsteps.GitStepSource
import javaposse.jobdsl.plugin.scmsteps.GitStepTranslator
import javaposse.jobdsl.plugin.scmsteps.GitStepTranslatorFactory
import javaposse.jobdsl.plugin.scmsteps.GitStepTranslatorProvider
import javaposse.jobdsl.plugin.scmsteps.GitStepTranslatorService
import javaposse.jobdsl.plugin.scmsteps.GitTranslatorFactory
import javaposse.jobdsl.plugin.scmsteps.RemoteScmSource
import javaposse.jobdsl.plugin.scmsteps.ScmTranslator
import javaposse.jobdsl.plugin.scmsteps.ScmTranslatorContext
import javaposse.jobdsl.plugin.scmsteps.ScmTranslatorFactory
import javaposse.jobdsl.plugin.scmsteps.ScmTranslatorProvider
import javaposse.jobdsl.plugin.scmsteps.ScmTranslatorService
import javaposse.jobdsl.plugin.scmsteps.ValidateTypeChecker
import javaposse.jobdsl.plugin.scriptgenerator.scripts.AbstractScriptGenerator
import javaposse.jobdsl.plugin.scriptgenerator.scripts.PipelineScriptGenerator
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorContext
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorContextExtension
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorContextExtensionPoint
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorContextExtensionService
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorContextFactory
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorContextProvider
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorContextService
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorContextSource
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorContextTranslator
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorFactory
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorProvider
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorService
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorSource
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorTranslator
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorTranslatorFactory
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorTranslatorProvider
import javaposse.jobdsl.plugin.scriptgenerator.scripts.ScriptGeneratorTranslatorService
import javaposse.jobdsl.plugin.scmsteps.BuildContext
import javaposse.jobdsl.plugin.scmsteps.BuildStepContext
import javaposse.jobdsl.plugin.scmsteps.BuildStepFactory
import javaposse.jobdsl.plugin.scmsteps.BuildStepProvider
import javaposse.jobdsl.plugin.scmsteps.BuildStepService
import javaposse.jobdsl.plugin.scmsteps.BuildStepSource
import javaposse.jobdsl.plugin.scmsteps.BuildStepTranslator
import javaposse.jobdsl.plugin.scmsteps.BuildStepTranslatorFactory
import javaposse.jobdsl.plugin.scmsteps.BuildStepTranslatorProvider
import javaposse.jobdsl.plugin.scmsteps.BuildStepTranslatorService
import javaposse.jobdsl.plugin.scmsteps.BuildStrategyContext
import javaposse.jobdsl.plugin.scmsteps.BuildStrategyFactory
import javaposse.jobdsl.plugin.scmsteps.BuildStrategyProvider
import javaposse.jobdsl.plugin.scmsteps.BuildStrategyService
import javaposse.jobdsl.plugin.scmsteps.BuildStrategySource
import javaposse.jobdsl.plugin.scmsteps.BuildStrategyTranslator
import javaposse.jobdsl.plugin.scmsteps.BuildStrategyTranslatorFactory
import javaposse.jobdsl.plugin.scmsteps.BuildStrategyTranslatorProvider
import javaposse.jobdsl.plugin.scmsteps.BuildStrategyTranslatorService
import javaposse.jobdsl.plugin.scmsteps.BuildTypeChecker
import javaposse.jobdsl.plugin.scmsteps.PipelineContext
import javaposse.jobdsl.plugin.scmsteps.PipelineStep
import javaposse.jobdsl.plugin.scmsteps.PipelineStepContext
import javaposse.jobdsl.plugin.scmsteps.PipelineStepFactory
import javaposse.jobdsl.plugin.scmsteps.PipelineStepProvider
import javaposse.jobdsl.plugin.scmsteps.PipelineStepService
import javaposse.jobdsl.plugin.scmsteps.PipelineStepSource
import javaposse.jobdsl.plugin.scmsteps.PipelineStepTranslator
import javaposse.jobdsl.plugin.scmsteps.PipelineStepTranslatorFactory
import javaposse.jobdsl.plugin.scmsteps.PipelineStepTranslatorProvider
import javaposse.jobdsl.plugin.scmsteps.PipelineStepTranslatorService
import javaposse.jobdsl.plugin.scmsteps.PipelineTypeChecker
import javaposse.jobdsl.plugin.scmsteps.ScmContext
import javaposse.jobdsl.plugin.scmsteps.ScmStep
import javaposse.jobdsl.plugin.scmsteps.ScmStepContext
import javaposse.jobdsl.plugin.scmsteps.ScmStepFactory
import javaposse.jobdsl.plugin.scmsteps.ScmStepProvider
import javaposse.jobdsl.plugin.scmsteps.ScmStepService
import javaposse.jobdsl.plugin.scmsteps.ScmStepSource
import javaposse.jobdsl.plugin.scmsteps.ScmStepTranslator
import javaposse.jobdsl.plugin.scmsteps.ScmStepTranslatorFactory
import javaposse.jobdsl.plugin.scmsteps.ScmStepTranslatorProvider
import javaposse.jobdsl.plugin.scmsteps.ScmStepTranslatorService
import javaposse.jobdsl.plugin.scmsteps.ScmTypeChecker
import javaposse.jobdsl.plugin.scmsteps.ValidateScmType
import javaposse.jobdsl.plugin.scmsteps.ValidateTypeChecker
import javaposse.jobdsl.plugin.scmsteps.exceptions.InvalidTypeException

def jenkins = Jenkins.instance

// Define function to create GitHub organization folder
def createGithubOrganizationFolder(String organization, List<String> repositories, String credentialsId) {
    jenkins.getDescriptor("com.cloudbees.hudson.plugins.folder.Folder").getDescriptorConfigs().each { descriptor ->
        if (descriptor.getJsonSafeClassName() == "com.cloudbees.hudson.plugins.folder.Folder$DescriptorImpl") {
            descriptor.doCreateItem(null, [
                "name": organization,
                "mode": "com.cloudbees.hudson.plugins.folder.AbstractFolder$1",
                "viewsTabBar": [
                    "stapler-class": "hudson.views.DefaultViewsTabBar"
                ],
                "defaultView": [
                    "stapler-class": "hudson.model.AllView"
                ],
                "healthMetrics": [],
                "icon": "icon-folder",
                "iconAltText": "",
                "description": "",
                "disableRememberMe": false,
                "folderViews": [
                    [
                        "stapler-class": "hudson.model.AllView",
                        "defaultFieldValues": [
                            "useincluderegex": "false",
                            "includes": "",
                            "columns": "",
                            "excludes": ""
                        ]
                    ]
                ],
                "historyDiscarder": [
                    "stapler-class": "hudson.model.BoundedLogRotator",
                    "daysToKeepStr": "10",
                    "numToKeepStr": "10",
                    "artifactDaysToKeepStr": "-1",
                    "artifactNumToKeepStr": "-1"
                ],
                "security": [
                    "stapler-class": "hudson.security.ProjectMatrixAuthorizationStrategy",
                    "permission": [
                        "com.cloudbees.hudson.plugins.folder.properties.AuthorizationMatrixProperty": [
                            "permission": "hudson.model.Item.Build:anonymous",
                            "permission": "hudson.model.Item.Cancel:anonymous",
                            "permission": "hudson.model.Item.Configure:anonymous",
                            "permission": "hudson.model.Item.Create:anonymous",
                            "permission": "hudson.model.Item.Delete:anonymous",
                            "permission": "hudson.model.Item.Discover:anonymous",
                            "permission": "hudson.model.Item.Move:anonymous",
                            "permission": "hudson.model.Item.Read:anonymous",
                            "permission": "hudson.model.Item.Workspace:anonymous",
                            "permission": "hudson.model.Run.Delete:anonymous",
                            "permission": "hudson.model.Run.Update:anonymous",
                            "permission": "com.cloudbees.hudson.plugins.folder.properties.AuthorizationMatrixProperty.Configure:anonymous"
                        ]
                    ]
                ],
                "views": []
            ])
        }
    }
    
    // Add repositories to organization folder
    def organizationFolder = jenkins.getItemByFullName(organization)
    if (organizationFolder != null && organizationFolder instanceof com.cloudbees.hudson.plugins.folder.AbstractFolder) {
        repositories.each { repository ->
            organizationFolder.createProject(jenkins.getDescriptor("org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject").newInstance(
                null,
                repository,
                null
            ), repository)
            def project = organizationFolder.getItem(repository)
            def traits = project.getBranchSource("https://github.com/" + organization + "/" + repository)
                .getTraits()
            traits.add(jenkins.getDescriptor("jenkins.plugins.git.traits.BranchDiscoveryTrait").newInstance())
            traits.add(jenkins.getDescriptor("jenkins.plugins.git.traits.CloneOptionTrait").newInstance("https", credentialsId, null, null, null, null))
            project.addTrigger(jenkins.getDescriptor("com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger").newInstance("1d"))
            project.save()
        }
    }
}

// Define GitHub organization, repositories, and credentials
def githubOrganization = "SONDOR-life"
def repositories = ["graalvm_cloud_native"] // Add your repositories here
def credentialsId = "app-deployer"

// Create GitHub organization folder
createGithubOrganizationFolder(githubOrganization, repositories, credentialsId)
