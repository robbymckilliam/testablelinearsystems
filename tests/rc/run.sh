scala -cp ../lib/Sounder.jar rc.scala
echo Plotting a 2ms piece of recorded data
gnuplot plot.gnuplot
echo Plot saved as plot.pdf
echo Left and right channels should be close together