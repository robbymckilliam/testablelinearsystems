#export JAVA_OPTS="-Xprof -server -Xms1g -Xmx1g"
export JAVA_OPTS="-server -Xms2g -Xmx2g"
CP=../lib/bignums.jar:../lib/JTransforms-3.0.jar:../lib/ScalaNumbers.jar:../lib/JLargeArrays-1.2.jar
scala -nocompdaemon -cp $CP fftbenchmark.scala
