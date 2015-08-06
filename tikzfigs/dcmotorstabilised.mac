kb:1;
kt:1;
r:1;
b:1;
j:1;

theta(t) := %pi/2*(1+erf(t));
dtheta(t) := diff(theta(t),t);
ddtheta(t) := diff(dtheta(t),t);

v(t) := theta(t) + (r*b/kt + kb)*dtheta(t) + r*j/kt*ddtheta(t);
plot2d( [theta(t), dtheta(t), ddtheta(t), v(t)], [t, 0, 3]); 

tex(v(t));
