set terminal pdf
set pm3d map
set palette gray
set xlabel 'time (ms)'
set ylabel 'electrical potential'
set output 'plot.pdf'
set xrange [1000:1020]
plot 'data.csv' using 1:2 t 'x' with lines, \
     'data.csv' using 1:3 t 'y' with lines, \
'data.csv' using 1:4 t '-11/6 x' with lines
