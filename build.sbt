/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Keys.{resolvers, `package` => packageTask}
Global / onChangedBuildSource := ReloadOnSourceChanges

import sbt.Resolver

val flowframeVersion = "0.2-SNAPSHOT"

lazy val commonSettings = Seq(
	version := flowframeVersion,
	scalaVersion := "2.13.13",
        scalacOptions += "-deprecation",
	scalacOptions += "-Wconf:cat=other-match-analysis:s",
	autoCompilerPlugins := true,
	resolvers += Resolver.mavenLocal,
	Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.AllLibraryJars
)
autoCompilerPlugins := true

//resolvers ++= Seq(
//	"Facebook Nexus internal releases" at "https://maven.thefacebook.com/nexus/content/repositories/releases",
//	"Facebook Nexus lib releases" at "https://maven.thefacebook.com/nexus/content/repositories/libs-releases-local",
//	"Facebook Nexus internal snapshots" at "https://maven.thefacebook.com/nexus/content/repositories/snapshots",
//	"Facebook Nexus lib snapshots" at "https://maven.thefacebook.com/nexus/content/repositories/libs-snapshots-local",
//	"Facebook Nexus public" at "https://maven.thefacebook.com/nexus/content/groups/public"
//)

lazy val plugin = project
	.settings(commonSettings: _*)
	.settings(
		name				:= "flowframe",
		version 			:= flowframeVersion,
		organization 		:= "com.facebook",
		crossVersion 		:= CrossVersion.full, // compiler api needs full version match
		//assembly / assemblyJarName := s"${name.value}_${scalaVersion}-${version.value}.jar",
		//assemblyShadeRules := Seq(
		//  ShadeRule.rename("scala.util.parsing.combinator.**" -> "shadedParsingCombinatorForFlowFrame.@1").inAll
		//),
		libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
		libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
		libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2", //% "provided",
		//libraryDependencies += "org.scalatestplus" %% "scalacheck-1-17" % "3.2.18.0" % Test,
		//libraryDependencies += "org.scalatestplus" %% "scalacheck-1-15" % "3.3.0.0-SNAP3" % Test,
		//libraryDependencies += "org.scalatest" %% "scalatest-propspec" % "3.3.0-SNAP3" % "test",
		//libraryDependencies += "org.scalatest" %% "scalatest-matchers-core" % "3.3.0-SNAP3" % "test",
		//libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.3.0-SNAP3" % "test",
        //libraryDependencies += "org.apache.spark" %% "spark-core" % "3.5.1",
        //libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.5.1"
	) in file ("flowframe-plugin")

val pluginJar = plugin / Compile / packageTask

lazy val runtime = project
	.settings(commonSettings: _*)
	.settings(
		//Compile / fork := true,
		Test / fork := false,
		//scalacOptions += s"-Jdummy=${pluginJar.value.lastModified}",
		// build tests with plugin
		Test/scalacOptions ++= Seq(
		        s"-Xplugin:${pluginJar.value.getAbsolutePath}",
			// enable the plugin
			s"-P:flowframe:lang:purpose",
			// rebuild when plugin changes
			s"-Jdummy=${pluginJar.value.lastModified}",
		),
		addCompilerPlugin("com.facebook" % s"flowframe_2.13.13" % flowframeVersion),
		//libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
		//Test/javaOptions += "-J-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005",
                //libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
		//libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18.0" % Test,
		//libraryDependencies += "org.scalatestplus" %% "scalacheck-1-17" % "3.2.18.0" % Test,
		//libraryDependencies += "org.apache.spark" %% "spark-core" % "3.5.1",
			//"org.apache.spark" %% "spark-sql" % "3.5.1",),
	) in file ("flowframe-runtime")
