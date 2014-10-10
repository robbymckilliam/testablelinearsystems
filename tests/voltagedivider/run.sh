scala -cp ../lib/Sounder.jar voltagedivider.scala
echo Plotting a 5ms piece of recorded data
gnuplot plot.gnuplot
echo Plot saved as plot.pdf
echo Left and right channels should be close together