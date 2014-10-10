set terminal pdf
set palette gray
set xlabel 'time (ms)'
set ylabel 'electrical potential'
set output 'plot.pdf'
set xrange [1000:1004]
plot 'data.csv' using 1:2 t 'x' with lines, \
     'data.csv' using 1:3 t 'y' with lines, \
'data.csv' using 1:4 t 'h*x' with lines