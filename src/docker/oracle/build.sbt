enablePlugins(DockerPlugin)

dockerfile in docker := {
  new sbtdocker.mutable.Dockerfile {
    from("wnameless/oracle-xe-11g")
    expose(22, 1521)
  }
}