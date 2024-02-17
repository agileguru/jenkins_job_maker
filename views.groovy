def buildStatusViews  = [

    Development :   [
        name    :   "Build Monitor -> Work-In-Progress",
        desc    :   "Development / Feature Branches",
        recurse :   true,
        regex   :   "^(?!.*RELEASE.*).*"
    ],
    Release      :   [
        name    :   "Build Monitor -> Released",
        desc    :   "Release Branches",
        recurse :   true,
        regex   :   ".*RELEASE.*\$"
    ],
]


buildStatusViews.each { entry ->
    viewDetails    = entry.value
    buildMonitorView (viewDetails."name") {
        description(viewDetails."desc")
        recurse(viewDetails."recurse")
        jobs {
             regex(viewDetails."regex")
        }
    }
}