scala -cp ../lib/Sounder.jar activeRC.scala
echo Plotting a 2ms piece of recorded data
gnuplot plot.gnuplot
echo Plot saved as plot.pdf