CP=../lib/Sounder.jar:../lib/bignums.jar:../lib/JTransforms-3.0.jar:../lib/ScalaNumbers.jar:../lib/JLargeArrays-1.2.jar
scala -cp $CP spectrumtest.scala
echo Plotting a 2ms piece of recorded data
gnuplot spectrumplot.gnuplot
echo Plot saved as spectrumplot.pdf
