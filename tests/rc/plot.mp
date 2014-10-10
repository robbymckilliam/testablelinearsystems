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

picture diamond;
diamond := image(draw unitsquare shifted(-0.5,-0.5) scaled 0.17cm rotated 45; );

picture square;
draw unitsquare shifted(-0.5,-0.5) scaled 0.17cm;
square := currentpicture; currentpicture := ORIGINAL_PICTURE;

picture circle; 
circle := image( draw fullcircle scaled 0.13cm; );

picture cross;
cross := image( 
draw (-0.09cm,0.0)--(0.09cm,0.0);
draw (0,-0.09cm)--(0,0.09cm);
);

picture dot; 
dot := image( fill fullcircle scaled 0.08cm; );

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

ls:=1;
label.rt(btex $\tilde{y}$ etex, (1cm,sh+ls*ups));
draw (sx,sh + ls*ups)--(1cm,sh + ls*ups);
draw circle shifted 1/2[(sx,sh + ls*ups),(1cm,sh + ls*ups)];

ls:=2;
label.rt(btex $H(\tilde{x})$ etex, (1cm,sh+ls*ups));
draw dot shifted 1/2[(sx,sh + ls*ups),(1cm,sh + ls*ups)];

legend := currentpicture; currentpicture := ORIGINAL_PICTURE;


plotheight = 7cm;
plotwidth = 10cm;
legendx = 0.1cm;
legendy = 0.1cm;
clipboundary = -3cm;

vardef xmin = "1.019" enddef;
vardef xmax = "1.041" enddef;

%%%%%%%%%%%%
%%%%Begin plot 

beginfig(1);
draw begingraph(plotwidth, plotheight);
 setcoords(linear,linear);
 glabel.bot(btex time (s) etex, OUT) shifted (0,1.1cm);
  %glabel.lft(btex  etex, OUT) rotated 90 shifted (1.3cm,-1.6cm);

  path px;
  gdata("rc.csv", s, augment.px(s1, s2); );
  gdraw px;
  
  path py;
  gdata("rc.csv", s, augment.py(s1, s3);
	if s1 Sleq xmax :
	  if xmin Sleq s1 :
	    glabel(circle,s1,s3);
	  fi; fi;);
  gdraw py;

  path ph;
  gdata("rc.csv", s,
	if s1 Sleq xmax :
	  if xmin Sleq s1 :
	    glabel(dot,s1,s4);
	  fi; fi; );

  setrange(xmin,whatever,xmax,whatever);
  otick.bot(btex $1$ etex, 1);
    %otick.bot(btex $1.005$ etex, "1.005");
  otick.bot(btex $1.01$ etex, 1.01);
    %otick.bot(btex $1.015$ etex, "1.015");
  otick.bot(btex $1.02$ etex, 1.02);
  autogrid(,otick.lft);

  draw legend shifted(legendx,legendy);

endgraph;

clip currentpicture to (clipboundary,clipboundary)--(clipboundary,plotheight+0.25bp)--(plotwidth+0.25bp,plotheight+0.25bp)--(plotwidth+0.25bp,clipboundary)--cycle;

endfig;

end;