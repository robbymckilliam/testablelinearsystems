for f in *.tex
do
pdflatex --shell-escape ${f}
done
