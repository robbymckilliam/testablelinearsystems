CP=""
for f in ../lib/*.jar
do
CP=$CP:${f}
done

scala -cp $CP firspectraplot.scala

gnuplot plot.gnuplot
