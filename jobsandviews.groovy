import jenkins.model.*
import org.jenkinsci.plugins.multibranch.*
import org.jenkinsci.plugins.github_branch_source.*
import com.cloudbees.hudson.plugins.folder.*

// User-defined parameters (replace with your values)
def projectNamePrefix = "MyProject_"
def viewName = "MyGitHubProjects"
def credentialId = "app-deployer" // Secure credential ID, not actual value
def githubUrls = [
    "https://github.com/agileguru/graalvm_cloud_native",
    // Add more URLs as needed
]

// Check for required plugins
def installedPlugins = Jenkins.instance.pluginManager.activePlugins
if (!installedPlugins.contains(GitSCMSourcePlugin.class) ||
    !installedPlugins.contains(MultibranchPipelinePlugin.class) ||
    !installedPlugins.contains(FolderPlugin.class)) {
    println("Error: Required plugins are not installed. Please install the following plugins: Git SCM Source, Multibranch Pipeline, and Folder.")
    return
}

// Create views folder if it doesn't exist
def folder = Jenkins.instance.createViewFolder("Views")

// Get GitHub credential
def credentials = CredentialStore.instance.credentials(com.cloudbees.hudson.plugins.git.credentials.GitCredentialImpl.class)
def githubCredential = credentials.get(credentialId)

if (githubCredential == null) {
    println("Error: GitHub credential with ID '$credentialId' not found.")
    return
}

// Create projects and views
githubUrls.each { githubUrl ->
    def projectName = projectNamePrefix + new File(githubUrl).name
    def displayName = githubUrl.replaceFirst("https://github.com/", "")

    // Create Multibranch Pipeline project
    def project = Jenkins.instance.createProject(MultibranchPipelineProject.class, projectName)
    project.displayName = displayName

    // Configure Git SCM source
    def scm = project.scm
    scm.clear()
    def githubSource = new GitSCMSource(githubUrl, "", "", false, githubCredential, null, null, [], "", false, false)
    scm << githubSource

    // Configure Multibranch Pipeline settings
    project.setBranchSources([githubSource])
    project.setTriggerOnPush() // Or customize trigger as needed

    // Create view and add the project
    def view = folder.createView(ListView.class, viewName + "-" + displayName)
    view.displayName = displayName
    view.jobs << project

    println("Created project '$projectName' and view '$viewName-$displayName'")
}

println("Successfully created projects and views!")
