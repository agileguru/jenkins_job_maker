def nodejsDockerPodTemplate(String label = 'nodejs-docker-agent') {
  podTemplate(label: label, yaml: libraryResource('podTemplate.yaml'))
}