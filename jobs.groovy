String jenkinsCredentialId = "app-deployer"
String scmBase = "https://github.com/SONDOR-life/"

def viewJobDefn  = [
Projects    :    
    [ 
        Desc        :    "All Projects",
        Folders     :    
        [                                                                           
            Libraries   : 
            [ 
                  plugin        :   [
                    scmUrl  :   scmBase,    
                    project :   "graalvm_cloud_native",
                    desc    :   "GraalVM Demo App" , 
                    display :   "GraalVM Demo App",
                    cred    :   jenkinsCredentialId 
                ]
            ]
        ],
    ]
] 

// Don't change unless you know what you doing             
viewJobDefn.each { entry ->
    // Create a List View
    listView(entry.key) {
        // View Details 
        viewDetails    = entry.value
        
        // Set Description 
        description(viewDetails."Desc")
        
        // Get Folders and create the jobs
        viewDetails."Folders".each { folder ->
            buildJobs(folder)
        }
        
        // Add Jobs to the View
        viewDetails."Folders".each { folder ->
            jobs {
                name(folder.key)
            }
        }
        // View Customization 
        columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
        }
    }
}

// Build the jobs 
def buildJobs( data ) { 
    data.each { entry ->
        // Create / Update Folder by Key 
        folder( entry.key )
        // In each folder Create Jobs
        entry.value.each { job -> 
            jobName = entry.key + "/" + job.key;
            jobDetails = job.value
            jobVCS = jobDetails."scmUrl" + jobDetails."project" + ".git";
            repoUrl = jobDetails."scmUrl" + jobDetails."project";
            jobDesc = jobDetails."desc"
            jobDisplayName = jobDetails."display"
            scmCred =  jobDetails."cred"
            buildMultiBranchJob(jobName, jobVCS, scmCred, jobDesc, jobDisplayName, jobDetails."scmUrl", jobDetails."project", repoUrl)
        }
    }
}
// Define method to build the job 
def buildMultiBranchJob(jobName, jobVCS, credentials, desc, display, scmBase , scmProject, repoLink) {
    // Create job
    multibranchPipelineJob(jobName) {
        // Desc  and Display name 
        description(desc)
        displayName(display)
        
        // Define source
        branchSources {
            branchSource {
                source {
                    git {
                        id(jobName + jobVCS)
                        remote(jobVCS)
                        credentialsId(credentials)
                        traits {
                            gitHubBranchDiscovery {
                                strategyId(1)
                            }
                            wipeWorkspaceTrait()
                            browser {
                                browser {
                                    github {
                                        repoUrl(repoLink)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } // End source
        // How Many Items in the history
        orphanedItemStrategy {
            discardOldItems {
                numToKeep(5)
                daysToKeep(5)
            }
        } // End Orphaned
    } // End Creating
} // End Method 