set terminal pdf
set palette gray
set xlabel 'frequency (Hz)'
set ylabel 'magnitude spectrum'
set xrange [0:7400]
set output 'plot.pdf'
plot 'hypothesised.csv' using 1:2 t 'hypothesised' with lines, \
     'measured.csv' using 1:2 t 'measured'