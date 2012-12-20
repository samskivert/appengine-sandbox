seq(samskivert.POMUtil.pomToSettings("pom.xml") :_*)

crossPaths := false

scalaVersion := "2.9.2"

appengineSettings

// write classes directly into the webapp directory for easier Jetty testing
// classDirectory in Compile <<= (target) { _ / "webapp" / "WEB-INF" / "classes" }

// no parallel test execution or GAE/DS chokes on self
// parallelExecution in Test := false

libraryDependencies ++= Seq(
  "org.mortbay.jetty" % "jetty" % "6.1.22" % "container",
  "com.novocode" % "junit-interface" % "0.7" % "test->default"
)
