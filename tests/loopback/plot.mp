prologues := 2;
filenametemplate "%j-%c.mps";

input graph;
input sarith;

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

picture dd; dd :=
dashpattern(on 4bp off 2bp on 1bp off 2bp);

picture nothing; nothing := currentpicture; currentpicture := ORIGINAL_PICTURE;

picture diamond;
diamond := image(draw unitsquare shifted(-0.5,-0.5) scaled 0.17cm rotated 45; );

picture square;
draw unitsquare shifted(-0.5,-0.5) scaled 0.17cm;
square := currentpicture; currentpicture := ORIGINAL_PICTURE;

picture circle; 
circle := image( draw fullcircle scaled 0.12cm; );

picture cross;
cross := image( 
draw (-0.09cm,0.0)--(0.09cm,0.0);
draw (0,-0.09cm)--(0,0.09cm);
);

picture dot; 
dot := image( fill fullcircle scaled 0.07cm; );

picture times; times := cross rotated 45;

%draws and oval of width w and height h
def oval(expr w, h) = (w/2,0)...(0,h/2)...(-w/2,0)...(0,-h/2)...cycle  enddef;

picture nsmall; 
nsmall := image( label(btex $N=75$ etex, (0,0)) rotated -23; );
picture nlarge; 
nlarge := image( label(btex $N=10235$ etex, (0,0)) rotated -23; );

%%%%%%%%%%%%%
%%%%%Legend picture

picture legend;

sx = 0.2cm;
sh = 0.2cm;
ups = 0.35cm;

%ls:=1;
%label.rt(btex $L=40$ etex, (1cm,sh + ls*ups));
%draw (sx,sh + ls*ups)--(1cm,sh + ls*ups) dashed evenly;

ls:=0;
label.rt(btex $\tilde{x}$ etex, (1cm,sh+ls*ups));
draw (sx,sh + ls*ups)--(1cm,sh + ls*ups);
draw dot shifted 1/2[(sx,sh + ls*ups),(1cm,sh + ls*ups)];

ls:=1;
label.rt(btex $\tilde{y}$ etex, (1cm,sh+ls*ups));
draw (sx,sh + ls*ups)--(1cm,sh + ls*ups);
draw circle shifted 1/2[(sx,sh + ls*ups),(1cm,sh + ls*ups)];

legend := currentpicture; currentpicture := ORIGINAL_PICTURE;


plotheight = 7cm;
plotwidth = 10cm;
legendx = 0.1cm;
legendy = 0.1cm;
clipboundary = -3cm;

vardef xmin = "0.999" enddef;
vardef xmax = "1.021" enddef;

%draw line with markers every so often.  Inputs:
%name - file with data
%d - is plot marker to use,
%n - is the column index for the data file
plotmarkersevery := 20;
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
   gdraw p;
enddef;

%%%%%%%%%%%%
%%%%Begin plot 

beginfig(1);
draw begingraph(plotwidth, plotheight);
 setcoords(linear,linear);
 glabel.bot(btex time (s) etex, OUT) shifted (0,1.1cm);
  %glabel.lft(btex  etex, OUT) rotated 90 shifted (1.3cm,-1.6cm);

  markerdraw("loopback.csv", dot, 2);
  markerdraw("loopback.csv", circle, 3);

  setrange(xmin,whatever,xmax,whatever);
  otick.bot(btex $1$ etex, 1);
    otick.bot(btex $1.005$ etex, "1.005");
  otick.bot(btex $1.01$ etex, 1.01);
    otick.bot(btex $1.015$ etex, "1.015");
  otick.bot(btex $1.02$ etex, 1.02);
  autogrid(,otick.lft);

  draw legend shifted(legendx,legendy);

endgraph;

clip currentpicture to (clipboundary,clipboundary)--(clipboundary,plotheight+0.25bp)--(plotwidth+0.25bp,plotheight+0.25bp)--(plotwidth+0.25bp,clipboundary)--cycle;

endfig;


beginfig(2);
draw begingraph(plotwidth, plotheight);
 setcoords(linear,linear);
 glabel.bot(btex time (s) etex, OUT) shifted (0,1.1cm);
  %glabel.lft(btex  etex, OUT) rotated 90 shifted (1.3cm,-1.6cm);

  markerdraw("cutchannel.csv", dot, 2);
  markerdraw("cutchannel.csv", circle, 3);

  setrange(xmin,whatever,xmax,whatever);
  otick.bot(btex $1$ etex, 1);
    otick.bot(btex $1.005$ etex, "1.005");
  otick.bot(btex $1.01$ etex, 1.01);
    otick.bot(btex $1.015$ etex, "1.015");
  otick.bot(btex $1.02$ etex, 1.02);
  autogrid(,otick.lft);

  draw legend shifted(legendx,legendy);

endgraph;

clip currentpicture to (clipboundary,clipboundary)--(clipboundary,plotheight+0.25bp)--(plotwidth+0.25bp,plotheight+0.25bp)--(plotwidth+0.25bp,clipboundary)--cycle;

endfig;


end;
