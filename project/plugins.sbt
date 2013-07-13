
resolvers += "Typesafe Repo" at "http://repo.akka.io/releases/"

resolvers += "sonatype-public" at "http://repo1.maven.org/maven2/"

libraryDependencies += "log4j" % "log4j" % "1.2.17"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" 		% "2.1.1")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.4.0")

addSbtPlugin("com.typesafe.akka" % "akka-sbt-plugin" % "2.1.0")

addSbtPlugin("org.scalaxb" %% "sbt-scalaxb" % "local-SNAPSHOT" 
	from "file://./project/sbt-scalaxb-local-SNAPSHOT.jar")

//addSbtPlugin("org.scalaxb" % "sbt-scalaxb" % "0.7.3")

//addSbtPlugin("com.twitter" %% "sbt11-scrooge" % "3.0.0")


