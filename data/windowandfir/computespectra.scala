import numbers.finite.Complex
import numbers.finite.PolarComplex
import numbers.finite.RectComplex
import scala.math.abs
import scala.math.sin
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.Pi
import sounder.Util.sinc

println("Generating spectra of windows finite impulse response filters")

//window width
val W = 10.0

//rectangular window
def rect(t : Double) = if( abs(t) > W/2 ) 0.0 else 1.0

//Triangle (Bartlett) window
def bartlett(t : Double) = {
  if( abs(t) > W/2) 0.0 
  else if( t < 0.0) 1.0 + t/W
  else 1.0 - t/W
}

def mod2pi(x : Double) = x - floor(x/2/Pi)*2*Pi

/// Format Doubles string to a reasonable number of decimal places
def fmt(x : Double) = "%f" format x

for(m <- 1 to 4) {
val filetfun = new java.io.FileWriter("data" + m + ".csv")
(fmin to fmax by 0.05) foreach { f =>
   filetfun.write(fmt(f) + "\t" + fmt(mod2pi(lambda(m,f).angle)) + "\n")
}
filetfun.close
}

println("Scala finished")
