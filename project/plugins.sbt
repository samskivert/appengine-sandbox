libraryDependencies += "com.samskivert" % "sbt-pom-util" % "0.3"

addSbtPlugin("com.eed3si9n" % "sbt-appengine" % "0.4.1")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.cc",
  Resolver.url("sbt-plugin-releases",
    url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)
)
