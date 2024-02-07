import jenkins.model.Jenkins
import hudson.model.*
import hudson.tasks.*
import hudson.plugins.git.*
import org.jenkinsci.plugins.*

def createProject(String projectName, String githubUrl, String credentialsId) {
    def existingItem = Jenkins.instance.getItemByFullName(projectName)
    if (existingItem != null) {
        println("Project $projectName already exists")
        return
    }

    def gitBranchDiscovery = new BranchDiscoveryTrait()
    def gitTagDiscovery = new OriginPullRequestDiscoveryTrait(Strategy.DEFAULT)
    def gitSubmodule = new SubmoduleOptionTrait(false, false, false, false)

    def traits = [
        gitBranchDiscovery,
        gitTagDiscovery,
        gitSubmodule
    ]

    def branchSource = new BranchSource(new BranchSourceConfigSource(new BranchSourceConfig(githubUrl, credentialsId), traits))

    def project = Jenkins.instance.createProject(MultiBranchProject.class, projectName)
    project.getSourcesList().add(branchSource)
    project.save()
}

def createView(String viewName, List<String> projectNames) {
    def existingView = Jenkins.instance.getView(viewName)
    if (existingView != null) {
        println("View $viewName already exists")
        return
    }

    def listView = new ListView(viewName, Jenkins.instance)

    projectNames.each { projectName ->
        def job = Jenkins.instance.getItemByFullName(projectName)
        if (job != null) {
            listView.add(job)
        } else {
            println("Job $projectName not found")
        }
    }

    Jenkins.instance.addView(listView)
}

def githubProjects = [
    "https://github.com/SONDOR-life/graalvm_cloud_native",
]
def githubCredentialsId = "app-deployer"
def projectNamePrefix = "MyMultiBranchProject"
def viewName = "MyView"

githubProjects.eachWithIndex { githubProject, index ->
    def projectName = "${projectNamePrefix}_${index + 1}"
    createProject(projectName, githubProject, githubCredentialsId)
}

createView(viewName, (1..githubProjects.size()).collect { "${projectNamePrefix}_${it}" })
