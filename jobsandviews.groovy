import jenkins.model.Jenkins

// Define your settings
def githubOrganization = "your-github-organization" // Replace with your organization name
def credentialId = "app-deployer" // Replace with your credential ID
def projectsList = [ // List of projects (format: [name, branchPattern])
  ["graalvm_cloud_native", "*"],

]
//  All branch in above but can be restricted ... 
//  ["project2", "feature/*"],
//  ["project3", "release/*"]


def viewName = "My Multibranch Projects" // Optional: Name of the view to create

// Create projects and views
projectsList.each { projectName, branchPattern ->
  println "Creating project: $projectName"
  def project = Jenkins.instance.createProject(
    hudson.model.MultibranchPipelineProject.class,
    projectName
  )
  
  def branchSource = project.branchSources.addGitSCMSource(
    "origin",
    "https://github.com/$githubOrganization/$projectName.git",
    credentialId,
    branchPattern
  )
  
  // Set additional configurations (optional)
  // branchSource.configureStrategy(...)
  
  project.save()
  
  if (viewName) {
    println "Adding project to view: $viewName"
    def view = Jenkins.instance.getView(viewName)
    if (!view) {
      view = Jenkins.instance.createView(hudson.model.ListView.class, viewName)
    }
    view.add(project)
    view.save()
  }
}

println "Script completed successfully."
