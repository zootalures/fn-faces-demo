#!/usr/bin/env bash
# creates a local repo with OCI artifacts in them
groupId="com.oracle.oci.sdk"
version="1.2.48"

set -e
set -x
repodir=$(pwd)/repo
(
  mkdir -p /tmp/deploy/repo
  mvn dependency:get -Dmaven.repo.local=/tmp/deploy/repo -DartifactId=oci-java-sdk-objectstorage -DgroupId=com.oracle.oci.sdk -Dversion=1.2.48  -Dpackaging=pom -Dtransitive=true -DremoteRepositories=oci::default::http://artifactory.aka.lgl.grungy.us/libs-release/,central::::https://repo.maven.apache.org/maven2


  for artifactId in "oci-java-sdk" "oci-java-sdk-common" "oci-java-sdk-objectstorage" "oci-java-sdk-objectstorage-extensions" "oci-java-sdk-objectstorage-generated" "oci-java-sdk-objectstorage-parent" ; do
     base="/tmp/deploy/repo/com/oracle/oci/sdk/$artifactId/$version"
     if [ -f ${base}/${artifactId}-${version}.jar ] ; then
         mvn deploy:deploy-file -Durl=file://"${repodir}"  -DgeneratePom=false  -Dfile=${base}/${artifactId}-${version}.jar  -DpomFile=${base}/${artifactId}-${version}.pom -DgroupId=${groupId} -DartifactId=${artifactId} -Dpackaging=jar -Dversion=${version}
     else
         mvn deploy:deploy-file -Durl=file://"${repodir}"  -DgeneratePom=false  -Dfile=${base}/${artifactId}-${version}.pom  -DpomFile=${base}/${artifactId}-${version}.pom -DgroupId=${groupId} -DartifactId=${artifactId} -Dpackaging=pom -Dversion=${version}
     fi
  done
)