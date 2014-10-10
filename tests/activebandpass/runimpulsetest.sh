scala -cp ../lib/Sounder.jar impulseresponsetest.scala
echo Plotting a 2ms piece of recorded data
gnuplot impulsereponseplot.gnuplot
echo Plot saved as impulsereponseplot.pdf