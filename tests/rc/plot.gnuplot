set terminal pdf dashed
set pm3d map
set palette gray
set output 'plot.pdf'
set xrange [1.02:1.04]
plot 'rc.csv' using 1:2 t 'Left' with lines, \
     'rc.csv' using 1:3 t 'Right' with lines