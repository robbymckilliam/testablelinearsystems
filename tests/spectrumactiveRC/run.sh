CP=../lib/Sounder.jar:../lib/bignums.jar:../lib/JTransforms-3.0.jar:../lib/ScalaNumbers.jar:../lib/JLargeArrays-1.2.jar
scala -nocompdaemon -cp $CP spectrumactiveRC.scala
echo Plotting a 2ms piece of recorded data
gnuplot plot.gnuplot
echo Plots saved as abs.pdf and angle.pdf
