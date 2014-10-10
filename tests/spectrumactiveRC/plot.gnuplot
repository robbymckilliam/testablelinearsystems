set terminal pdf
set palette gray
set xlabel 'frequency (Hz)'
set ylabel 'magnitude spectrum'
set xrange [0:8000]
set output 'abs.pdf'
plot 'hypothesised.csv' using 1:2 t 'hypothesised' with lines, \
     'measured.csv' using 1:2 t 'measured'

set output 'angle.pdf'
set ylabel 'phase spectrum'
plot 'hypothesised.csv' using 1:3 t 'hypothesised' with lines, \
     'measured.csv' using 1:3 t 'measured'