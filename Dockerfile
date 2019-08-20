FROM fnproject/fn-java-fdk-build:jdk9-1.0.75 as build-stage

WORKDIR /function
ENV MAVEN_OPTS  -Dmaven.repo.local=/usr/share/maven/ref/repository

# Pre-cache maven deps in a layer in the build image
ADD pom.xml /function/pom.xml
COPY repo /function/repo
RUN ["mvn", "package", "dependency:copy-dependencies", "-DincludeScope=runtime", "-DskipTests=true", "-Dmdep.prependGroupId=true", "-DoutputDirectory=target"  ]


# Extract the pre-compiled binary out of the openCV dep ,delete it and the other binaries to reduce footprint
RUN mkdir -p /opencv/jar && \
    cd /opencv/jar && \
    unzip /usr/share/maven/ref/repository/org/openpnp/opencv/3.2.0-0/opencv-3.2.0-0.jar  && \
    mv nu/pattern/opencv/linux/x86_64/libopencv_java320.so /usr/lib/ && \
    rm -r nu/pattern/opencv && \
    jar cvf ../opencv_min.jar .

COPY data /function/data

COPY testdata /function/testdata
ADD src /function/src
RUN ["mvn", "package"]


FROM fnproject/fn-java-fdk:jdk9-1.0.75
WORKDIR /function

COPY --from=build-stage /usr/lib/libopencv_java320.so /function/runtime/lib
COPY --from=build-stage /opencv/opencv_min.jar /function/app/
COPY --from=build-stage /function/target/*.jar /function/app/
COPY data /function/data
CMD ["com.example.fn.FacesFunctions::handleRequest"]
