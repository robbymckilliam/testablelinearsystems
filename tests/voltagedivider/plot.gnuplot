set terminal pdf
set output 'plot.pdf'
set xlabel 'time (ms)'
set ylabel 'electrical potential'
set xrange [1000:1020]
set style function linespoints
plot 'voltagedivider.csv' using 1:2 t 'x' with lines, \
     'voltagedivider.csv' using 1:3 t 'y' with lines, \
'voltagedivider.csv' using 1:4 t '47/57 x' with lines