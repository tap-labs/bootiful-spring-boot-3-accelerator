LOCAL_PATH = os.getenv("LOCAL_PATH", default='.')
NAMESPACE = os.getenv("NAMESPACE", default='default')

k8s_custom_deploy(
    'bootiful-spring-boot-service',
    apply_cmd="tanzu apps workload apply -f config/workload.yaml --update-strategy replace --debug --live-update" +
               " --local-path " + LOCAL_PATH +
               " --namespace " + NAMESPACE +
               " --yes --output yaml",
    delete_cmd="tanzu apps workload delete -f config/workload.yaml --namespace " + NAMESPACE + " --yes",
    container_selector='workload',

    # Live Update IntelliJ:
    deps=['build.gradle.kts', './build/classes/java/main', './build/resources/main'],
    live_update=[
       sync('./build/classes/java/main', '/workspace/BOOT-INF/classes'),
       sync('./build/resources/main', '/workspace/BOOT-INF/classes')
    ]
)

k8s_resource('bootiful-spring-boot-service', port_forwards=["8080:8080"],
            extra_pod_selectors=[{'carto.run/workload-name': 'bootiful-spring-boot-service', 'app.kubernetes.io/component': 'run'}])
