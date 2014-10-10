scala -cp ../lib/Sounder.jar loopback.scala
echo Plotting a 2ms piece of recorded data
gnuplot plot.gnuplot
echo Plot saved as loopback.pdf and cutchannel.pdf
echo Left and right channels should be close together
