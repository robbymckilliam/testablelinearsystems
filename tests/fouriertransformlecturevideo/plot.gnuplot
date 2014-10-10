set terminal pdf
set xlabel 'f (Hz)'
set ylabel '|F(x)|'
set xrange [-13000:13000]
set output 'abs.pdf'
set style function linespoints
plot 'ft.csv' using 1:2 t '|F(x)|' with lines

set output 'angle.pdf'
set ylabel 'angle(F(x))'
set style function linespoints
plot 'ft.csv' using 1:3 t '|F(x)|' with lines