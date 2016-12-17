import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys._

object Build extends Build {

  val dependencyResolvers = Seq("Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/")

  val dependencies = Seq(
    "com.typesafe.slick" %% "slick" % "3.1.1",
    "org.scala-lang" % "scala-reflect" % "2.11.8",

    "org.scalatest" %% "scalatest" % "3.0.0" % "test",
    "com.typesafe.slick" %% "slick-extensions" % "3.1.0" % "test",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1" % "test",
    "com.h2database" % "h2" % "1.4.192" % "test",
    "mysql" % "mysql-connector-java" % "5.1.38" % "test",
    "org.postgresql" % "postgresql" % "9.4.1211" % "test",
    "org.slf4j" % "slf4j-simple" % "1.7.21" % "test",
    "org.apache.derby" % "derby" % "10.11.1.1" % "test",
    "org.hsqldb" % "hsqldb" % "2.3.4" % "test",
    "joda-time" % "joda-time" % "2.9.6" % "test"
  )

  lazy val project =
    Project("root", file("."))
      .configs(AllDbsTest, Db2Test, SqlServerTest)
      .settings(inConfig(AllDbsTest)(Defaults.testTasks): _*)
      .settings(inConfig(Db2Test)(Defaults.testTasks): _*)
      .settings(inConfig(SqlServerTest)(Defaults.testTasks): _*)
      .settings(
        name := "slick-repo",
        version := "1.0-SNAPSHOT",
        scalaVersion := "2.11.8",
        libraryDependencies ++= dependencies,
        resolvers ++= dependencyResolvers,

        parallelExecution in Test := false,
        coverageEnabled := true,

        testOptions in Test := Seq(Tests.Filter(baseFilter)),
        testOptions in Db2Test := Seq(Tests.Filter(db2Filter)),
        testOptions in AllDbsTest := Seq(Tests.Filter(allDbsFilter)),
        testOptions in SqlServerTest := Seq(Tests.Filter(sqlServerFilter))
      )

  lazy val mysql =
    Project("mysql", file("src/docker/mysql"))
      .settings(
        name := "mysql"
      )

  lazy val oracle =
    Project("oracle", file("src/docker/oracle"))
      .settings(
        name := "oracle"
      )

  lazy val db2 =
    Project("db2", file("src/docker/db2"))
      .settings(
        name := "db2"
      )

  lazy val postgres =
    Project("postgres", file("src/docker/postgres"))
      .settings(
        name := "postgres"
      )

  val dbPrefixes = Seq("MySQL", "Oracle", "Postgres", "Derby", "Hsql")
  val db2Prefix = Seq("DB2")
  val sqlServerPrefix = Seq("SQLServer")
  lazy val AllDbsTest = config("alldbs") extend Test
  lazy val Db2Test = config("db2") extend Test
  lazy val SqlServerTest = config("sqlserver") extend Test

  def testName(name: String): String = name.substring(name.lastIndexOf('.') + 1)

  def allDbsFilter(name: String): Boolean = dbPrefixes.exists(p => testName(name) startsWith p)

  def db2Filter(name: String): Boolean = db2Prefix.exists(p => testName(name) startsWith p)

  def sqlServerFilter(name: String): Boolean = sqlServerPrefix.exists(p => testName(name) startsWith p)

  def baseFilter(name: String): Boolean = !allDbsFilter(name) && !db2Filter(name) && !sqlServerFilter(name)
}
