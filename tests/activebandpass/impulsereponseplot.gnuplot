set terminal pdf
set palette gray
set xlabel 'time (ms)'
set ylabel 'electrical potential'
set output 'impulseplot.pdf'
set xrange [1000:1004]
plot 'impulsedata.csv' using 1:2 t 'x' with lines, \
     'impulsedata.csv' using 1:3 t 'y' with lines, \
'impulsedata.csv' using 1:4 t 'h*x' with lines