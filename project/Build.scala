import sbt._
import sbt.Keys._

object IntroToScalaBuild extends Build {
    import OpenCL._
    import TestingDeps._
    import AkkaDeps._
    import CacheTech._
    import DataProcessor._

    val localRepoResolvers = Seq( "Local Maven Repo" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
        "Local Ivy Repo" at "file://"+Path.userHome.absolutePath+"/.ivy2/local")

    val jol = "org.openjdk.jol" % "jol-core" % "0.4-SNAPSHOT"
    lazy val demo = Project(
    id = "introduction-to-scala",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
        name := "Scala",
        version := "0.1-SNAPSHOT",
        scalaVersion := "2.11.6",
        scalacOptions ++= Seq("-feature", "-deprecation", "-language:postfixOps","-language:higherKinds", "-language:implicitConversions"),
        resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo),
        resolvers ++= localRepoResolvers,
        resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
        resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
        resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",

        scalacOptions in Test ++= Seq("-Yrangepos"),
        libraryDependencies ++= testDeps,
        libraryDependencies ++= Seq(jocl, jol, jcs, jcs_core, jcs_jcache),
        libraryDependencies ++= Seq(actors, actorCluster),
        libraryDependencies ++= Seq(scalaReflect),
        libraryDependencies ++= Seq(actortestkit),
        libraryDependencies ++= Seq(persistence),
        libraryDependencies ++= Seq(jackson_scala, jackson_databind)
        ) )
}

object CacheTech {
    val jcs = "org.apache.commons" % "commons-jcs" % "2.0-SNAPSHOT"
    val jcs_core = "org.apache.commons" % "commons-jcs-core" % "2.0-SNAPSHOT"
    val jcs_jcache = "org.apache.commons" % "commons-jcs-jcache" % "2.0-SNAPSHOT"
}

object TestingDeps {
    val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"
    val junit4Interface = "com.novocode" % "junit-interface" % "0.10-M4" % "test"
    val junit4 = "junit" % "junit" % "4.11" % "test"
    val specs2 = "org.specs2" %% "specs2-core" % "3.5" % "test"
    val testDeps = Seq(specs2, scalaTest, junit4, junit4Interface)
}

object AkkaDeps {
    val persistence = "com.typesafe.akka" % "akka-persistence_2.10" % "2.3.4"
    val actors = "com.typesafe.akka" % "akka-actor_2.10" % "2.3.4"
    val actorCluster = "com.typesafe.akka" % "akka-cluster_2.10" % "2.3.4"
    val actortestkit = "com.typesafe.akka" % "akka-testkit_2.10" % "2.3.4" % "test"
    val scalaReflect =  "org.scala-lang" % "scala-reflect" % "2.11.6"
}

object DataProcessor {
  val jackson_scala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.3.6-SNAPSHOT"
  val jackson_databind = "com.fasterxml.jackson.core" % "jackson-databind" % "2.3.6-SNAPSHOT"
}

object OpenCL {
    val jocl = "com.nativelibs4java" % "javacl" % "1.0.0-RC3"
}

