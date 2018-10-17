
FROM fnproject/fn-java-fdk-build:jdk9-1.0.75 as opencv-build-base
RUN apt update && apt install -y libopencv-core3.2 libopencv3.2-jni libopencv3.2-java

FROM opencv-build-base as build-stage
WORKDIR /function
ENV MAVEN_OPTS -Dhttp.proxyHost=www-proxy-lon.uk.oracle.com -Dhttp.proxyPort=80 -Dhttps.proxyHost=www-proxy-lon.uk.oracle.com -Dhttps.proxyPort=80 -Dhttp.nonProxyHosts=localhost|127.0.0.1|10.167.103.241|artifactory-slc.oraclecorp.com|192.168.99.100|*.oraclecorp.com|*.oracle.com|*.grungy.usi|ocliffe-pc.local -Dmaven.repo.local=/usr/share/maven/ref/repository
ADD pom.xml /function/pom.xml
RUN ["mvn", "package", "dependency:copy-dependencies", "-DincludeScope=runtime", "-DskipTests=true", "-Dmdep.prependGroupId=true", "-DoutputDirectory=target", "--fail-never"]
ADD src /function/src
RUN ["mvn", "package"]

FROM opencv-base
WORKDIR /function
COPY --from=build-stage /function/target/*.jar /function/app/
COPY --from=opencv-build-base /function/target/*.jar /function/app/
COPY data /function/data
CMD ["com.example.fn.FacesFunctions::handleRequest"]
