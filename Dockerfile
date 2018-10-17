FROM fnproject/fn-java-fdk-build:jdk9-1.0.75 as opencv-build-base

RUN apt update && apt install -y  build-essential git cmake curl python2.7
WORKDIR /build

RUN curl  -o openjdk.zip https://download.java.net/openjdk/jdk9/ri/openjdk-9+181_linux-x64_ri.zip  && unzip openjdk.zip

RUN curl  -o  opencv.zip https://codeload.github.com/opencv/opencv/zip/3.2.0  &&\
  unzip opencv.zip &&\
   cd opencv-3.2.0  &&\
    mkdir build

RUN cd /build && \
    curl -o  ant.tar.gz  http://www.mirrorservice.org/sites/ftp.apache.org//ant/binaries/apache-ant-1.10.5-bin.tar.gz && \
    tar -xzf ant.tar.gz && \
    mv apache-ant-1.10.5 /opt/ant && \
    rm ant.tar.gz

ENV ANT_HOME /opt/ant
ENV PATH ${PATH}:/opt/ant/bin


RUN   cd /build/opencv-3.2.0/build &&\
      JAVA_HOME=/build/java-se-9-ri/jdk-9 cmake -D BUILD_opencv_java=ON -D  BUILD_FAT_JAVA_LIB=ON -D BUILD_IPP_IW=OFF -D BUILD_ITT=OFF -D WITH_1394=OFF -D WITH_CUDA=OFF -D WITH_CUFFT=OFF -D WITH_GSTREAMER=OFF -D WITH_IPP=OFF \
          -D WITH_JASPER=OFF -D WITH_WEBP=OFF -D WITH_OPENEXR=OFF -D WITH_PVAPI=OFF -D WITH_GIGEAPI=OFF -D WITH_QT=OFF -D WITH_TIFF=OFF \
          -D WITH_V4L=OFF -D WITH_LIBV4L=OFF -D WITH_OPENCL=OFF -D WITH_OPENCLAMDFFT=OFF -D WITH_OPENCLAMDBLAS=OFF -D WITH_MATLAB=OFF \
          -D WITH_GPHOTO2=OFF -D WITH_LAPACK=OFF-D BUILD_EXAMPLES=OFF -D BUILD_opencv_apps=OFF -D BUILD_DOCS=OFF \
          -D BUILD_PERF_TESTS=OFF -D BUILD_TESTS=OFF -D CMAKE_INSTALL_PREFIX=/usr/local/ .. && make -j8 install

RUN cp /build/opencv-3.2.0/build/lib/* /usr/local/lib/

FROM fnproject/fn-java-fdk-build:jdk9-1.0.75 as build-stage

WORKDIR /function
ENV MAVEN_OPTS  -Dmaven.repo.local=/usr/share/maven/ref/repository
COPY  --from=opencv-build-base /build/opencv-3.2.0/build/lib/* /usr/local/lib/
ADD pom.xml /function/pom.xml
RUN ["mvn", "package", "dependency:copy-dependencies", "-DincludeScope=runtime", "-DskipTests=true", "-Dmdep.prependGroupId=true", "-DoutputDirectory=target", "--fail-never"]
ADD src /function/src
RUN ["mvn", "package"]

FROM opencv-base
WORKDIR /function

COPY --from=opencv-build-base /build/opencv-3.2.0/build/lib/* /usr/local/lib/
COPY --from=build-stage /function/target/*.jar /function/app/
COPY data /function/data
CMD ["com.example.fn.FacesFunctions::handleRequest"]
