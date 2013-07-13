import sbt._

import Keys._


object ProjectBuild extends Build {
    val Organization = "com.colingodsey"
    val Version = "0.1"
    val ScalaVersion = "2.10.2"

    object Dependencies {
        object V {

        }

        val deps = Seq(

            )
    }

    val jvmOpts = """ -XX:ParallelGCThreads=4   
-Xms20m -Xmx1500m -XX:MaxPermSize=1024m 
-XX:+UseParallelGC -Xminf=5 -Xmaxf=10 -XX:GCTimeRatio=1 
-XX:+UseNUMA -XX:+AggressiveOpts -server  
-XX:ReservedCodeCacheSize=512m
-XX:CompileThreshold=50000 -XX:+BackgroundCompilation  
-XX:+TieredCompilation -XX:+UseTLAB 
-XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=30 
-XX:+UseFastAccessorMethods """ //-XX:+UseG1GC -XX:+UseSerialGC -XX:-UseConcMarkSweepGC  -XX:+UseParallelGC

    lazy val server = Project(
        id = "Logos-Core",
        base = file("."),
        settings = defaultSettings ++ Seq(
//            distJvmOptions in (Dist, run) := jvmOpts,
            libraryDependencies ++= Dependencies.deps
            //EclipseKeys.executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE17),

            //sourceGenerators in Compile <+= JDBCPKeys.jdbcp in Compile,
            //createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Managed)
        )) //aggregate(hostnameVerifierStubber)// % "->package")

    lazy val buildSettings = Defaults.defaultSettings ++ Seq(
        organization := Organization,
        version := Version,
        scalaVersion := ScalaVersion,
        crossPaths := false,
        organizationName := "Colin Godsey",
        organizationHomepage := Some(url("http://www.colingodsey.com")))

    lazy val defaultSettings = buildSettings ++ Seq(
        resolvers += "spray repo" at "http://repo.spray.cc/",
        resolvers += "mvn repo" at "http://repo1.maven.org/maven2/",
        resolvers += "scala tools" at "http://scala-tools.org/repo-releases/",
        resolvers += "gridgain repo" at "http://www.gridgainsystems.com/maven2/",
        resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",

        // compile options
        scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation",
            "-unchecked", "-optimise",
            "-Xlog-free-terms",
            //"-Ymacro-debug-lite",
            "-target:jvm-1.7"),//, "-Dscalac.patmat.analysisBudget=off"),
        javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"))
}

