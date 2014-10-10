set terminal pdf
set pm3d map
set palette gray
set output 'plot.pdf'
set xlabel 'time (sec)'
set ylabel 'electrical potential'
set xrange [1:1.02]
plot 'cutchannel.csv' using 1:2 t 'Left' with lines, \
     'cutchannel.csv' using 1:3 t 'Right' with lines