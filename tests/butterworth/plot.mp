prologues := 2;
filenametemplate "%j-%c.mps";

input sarith;
input graph;

%input TEX; 
%TEXPRE("%&latex" & char(10) & 
%"\documentclass[12pt]{article}\begin{document}"); 
%TEXPOST("\end{document}"); 

verbatimtex
%&latex
\documentclass[8pt]{extarticle}
  \usepackage{amsmath}
  \usepackage{amssymb}
  \usepackage{amsthm}
  \begin{document}
  etex

init_numbers(btex $-$ etex,btex $1$ etex, btex ${\times}10$ etex, btex ${}^-$ etex, btex ${}^2$ etex);

picture ORIGINAL_PICTURE;
ORIGINAL_PICTURE := currentpicture;

picture nothing; nothing := currentpicture; currentpicture := ORIGINAL_PICTURE;

picture dd; dd :=
dashpattern(on 4bp off 2bp on 1bp off 2bp);

picture diamond;
diamond := image(draw unitsquare shifted(-0.5,-0.5) scaled 0.17cm rotated 45; );

picture square;
draw unitsquare shifted(-0.5,-0.5) scaled 0.17cm;
square := currentpicture; currentpicture := ORIGINAL_PICTURE;

picture circle; 
circle := image( draw fullcircle scaled 0.16cm; );

picture cross;
cross := image( 
draw (-0.09cm,0.0)--(0.09cm,0.0);
draw (0,-0.09cm)--(0,0.09cm);
);

picture dot; 
dot := image( fill fullcircle scaled 0.1cm; );

picture times; times := cross rotated 45;

%draws and oval of width w and height h
def oval(expr w, h) = (w/2,0)...(0,h/2)...(-w/2,0)...(0,-h/2)...cycle  enddef;

%%%%%%%%%%%%%
%%%%%Legend picture

picture legend;

sx = 0.2cm;
sh = 0.2cm;
ups = 0.35cm;

ls:=1;
label.rt(btex $\vert\Lambda(H)\vert$ etex, (1cm,sh+ls*ups));
draw (sx,sh + ls*ups)--(1cm,sh + ls*ups);
%draw dot shifted 1/2[(sx,sh + ls*ups),(1cm,sh + ls*ups)];

ls:=0;
label.rt(btex measured etex, (1cm,sh+ls*ups));
%draw (sx,sh + ls*ups)--(1cm,sh + ls*ups);
draw dot shifted 1/2[(sx,sh + ls*ups),(1cm,sh + ls*ups)];

legend := currentpicture; currentpicture := ORIGINAL_PICTURE;

picture legendphase;

ls:=1;
label.rt(btex $\angle\Lambda(H)$ etex, (1cm,sh+ls*ups));
draw (sx,sh + ls*ups)--(1cm,sh + ls*ups);
%draw dot shifted 1/2[(sx,sh + ls*ups),(1cm,sh + ls*ups)];

ls:=0;
label.rt(btex measured etex, (1cm,sh+ls*ups));
%draw (sx,sh + ls*ups)--(1cm,sh + ls*ups);
draw dot shifted 1/2[(sx,sh + ls*ups),(1cm,sh + ls*ups)];

legendphase := currentpicture; currentpicture := ORIGINAL_PICTURE;

plotheight = 7cm;
plotwidth = 10cm;
legendx = 0.1cm;
legendy = 0.1cm;
clipboundary = -3cm;

vardef xmin = "0" enddef;
vardef xmax = "7400" enddef;

%draw line with markers every so often.  Inputs:
%name - file with data
%d - is plot marker to use,
%n - is the column index for the data file
plotmarkersevery := 1;
def markerdraw(expr name, d, n) =
  path p;
  counter := 0;
  gdata(name, s, augment.p(s1, s[n]);
	if s1 Sleq xmax :
	  if xmin Sleq s1 :
	    if counter = plotmarkersevery :
	      glabel(d,s1,s[n]);
	      counter := 0;
	    fi; fi; fi;
	counter := counter + 1;
	);
   %gdraw p;
enddef;

def linedraw(expr name, n) =
  path p;
  counter := 0;
  gdata(name, s, augment.p(s1, s[n]););
  gdraw p;
enddef;


path plotclip;
plotclip := (clipboundary,clipboundary)--(clipboundary,plotheight+0.25bp)--(plotwidth+7bp,plotheight+0.25bp)--(plotwidth+7bp,clipboundary)--cycle;

%%%%%%%%%%%%
%%%%Begin magnitude plot 

beginfig(1);
draw begingraph(plotwidth, plotheight);
 setcoords(linear,linear);
 glabel.bot(btex frequency (Hz) etex, OUT) shifted (1cm,1.1cm);
  glabel.lft(btex magnitude spectrum etex, OUT) rotated 90 shifted (-0.2cm,1.7cm);

  linedraw("hypothesised.csv", 2);
  markerdraw("measured.csv", dot, 2);
  
  setrange(xmin,whatever,xmax,whatever);
  %otick.bot(btex $0$ etex, 1000);
  %autogrid(,);

  draw legend shifted(legendx,legendy);

endgraph;

clip currentpicture to plotclip;

endfig;

%%%%%%%%%%%%
%%%%Begin phase plot 

beginfig(2);
draw begingraph(plotwidth, plotheight);
 setcoords(linear,linear);
 glabel.bot(btex frequency (Hz) etex, OUT) shifted (1cm,1.1cm);
  glabel.lft(btex phase spectrum etex, OUT) rotated 90 shifted (-0.2cm,1.7cm);

  linedraw("hypothesised.csv", 3);
  markerdraw("measured.csv", dot, 3);
  
  setrange(xmin,whatever,xmax,whatever);
  %otick.bot(btex $0$ etex, 1000);
  %autogrid(,);

  draw legendphase shifted(legendx,legendy);

endgraph;

clip currentpicture to plotclip;

endfig;



end;
