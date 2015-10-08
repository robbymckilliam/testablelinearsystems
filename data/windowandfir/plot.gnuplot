set terminal pdf
set palette gray
set xlabel 'frequency (Hz)'
set ylabel 'magnitude spectrum'
set xrange [0:4.1]

set output 'spectra.pdf'
plot 'rect.csv' using 1:2 t 'rectangular' with lines, \
     'bartlett.csv' using 1:2 t 'Bartlett' with lines, \
     'hann.csv' using 1:2 t 'Hann' with lines, \
     'blackman.csv' using 1:2 t 'Blackman' with lines