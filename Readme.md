# Automate CI / CD On-boarding process in Jenkins in a GitOps friendly way.

Jenkins provides a powerful Groovy-based Domain Specific Language (DSL) that allows you to manage its configuration as code. The Job DSL is a specific DSL for creating and managing Jenkins jobs using an intuitive, code-based syntax. This guide assumes you have a basic understanding of Groovy or Java. To get started, you must install the needed extra plugins as descrie below and have admin access.


## Plugins needed 

1. JOB DSL Plugin
2. List View Plugin
3. Multibranch Pipeline Plugin
4. Folders Plugin (usually installed by default)
5. Git Plugin and a provider plugin (e.g., GitHub Branch Source)

## Data you need to customize in jobs.groovy

```
    String jenkinsCredentialId = "app-deployer"
    String SCMORG  = "agileguru"
    String scmBase = "https://github.com/" + SCMORG + "/"


    def viewJobDefn  = [
        // Name of The Top Level Folder Name
        Projects    :
            [
                Desc        :    "All Projects",
                Folders     :
                [
                    Demo   :
                    [
                        graal        :   [
                            scmUrl  :   scmBase,
                            project :   "graalvm_cloud_native",
                            desc    :   "GraalVM Demo App",
                            display :   "GraalVM Demo App",
                            cred    :   jenkinsCredentialId,
                            org     :   SCMORG
                        ]
                    ],
                ],
            ],
    ]
```

<br/><br/>

## Notes ...
1. Line number 1 defines the credential-id for authentication for the git repositories.
2. Line number 2 defines the Github user / organisation as the source of git repositories for job creation.
3. Line 5 onwards till line 55 is the place where you need to define your Folder structure and repository details for you to manage.
4. From line 55 only change if you want to customize it further. For most use cases the defaults should be good enough.


## Create the Seeding job 

* Create a new Freestyle project in Jenkins. This will be your "Seed Job" — the single job you run to create all other jobs.
* In the Source Code Management section, select Git and enter the repository URL for the seed job repository you created in Step 3.
* In the Build section, add a build step called "Process Job DSLs".
* Select "Look on file system" and set "DSL Scripts" to *.groovy.
* This tells Jenkins to execute the jobs.groovy file from your repository, which will, in turn, read your create the jobs.


## You are up and running .... 

After completing the above steps we have 1. An easy to manage git repository to define your jobs and the job hierarchies in a well structure manner. 2. Mechanism / Framework to track our Jenkins job following good Devops / DRY principles that is scallable with minimal changes. 3. Management of Jenkins Job that follows GitOps Principles which is also good for DR planning. You can see the output of and LIVE implementation of this on Agile Guru Devops Portal at our <a href="https://devops.alacritysys.com/jenkins/view/Seed%20Admin/job/admin/job/SeedJob/" target="blank"> Own Jenkins instance </a>.

## Detailed Blog on the implementation 

https://tech.agileguru.org/posts/devops/automate-jenkins-jobs-gitops.html 