CP=""
for f in ../lib/*.jar
do
CP=$CP:${f}
done

#run all the scal files
for f in *.scala
do
scala -cp $CP ${f}
done



