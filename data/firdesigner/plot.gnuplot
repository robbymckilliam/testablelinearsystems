set terminal pdf
set palette gray
set xlabel 'frequency (Hz)'
set ylabel 'magnitude spectrum'
set xrange [0:8000]

set output 'spectra.pdf'
plot 'magnitude.csv' using 1:2 t 'rectangular' with lines
