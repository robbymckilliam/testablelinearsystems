import numbers.finite.Complex
import numbers.finite.Complex._
import numbers.finite.PolarComplex
import numbers.finite.RectComplex
import sounder.Sounder._
import sounder.Util._
import scala.math.sin
import scala.math.sqrt
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.round
import scala.math.ceil
import scala.math.pow
import scala.math.atan
import scala.math.Pi

print("Plug in the active RC and press ENTER")
System.in.read

println("Playing a sequence of tones and recording input")

val j = RectComplex(0,1) //square root of negative 1
val Fs = 44100 //sample rate (CD quality
val P = 1.0/Fs //sample period
def f(k : Int) = round(110.0*pow(2.0,k/2.0)) 
val ks = 0 to 12
val discard = 10000 //the number of samples we will discard to account for distortion
val L = 8820 //the number of sample we will take out (corresponds with 200ms of signal)

//for each k = 0,..,21 compute estimated spectrum and return the values
//into the list spectrumestimates
val Q : Seq[Complex] = ks.map { k =>

  print(f(k) + "Hz ")

  def xtrue(t : Double) = sin(2*Pi*f(k)*t)
  val (ys, xs) = playRecord(xtrue, 0, 1.0, Fs) //play for 1 second and record

  if(min(xs.length, ys.length) < (discard+L)) throw new java.lang.ArrayIndexOutOfBoundsException("Number of samples recorded isn't enough for some reason")

  //chop off first discard=10000 samples  to avoid distortion when the soundcard
  //starts up and take the following L=8820
  val x = xs.slice(discard, discard+L)
  val y = ys.slice(discard, discard+L)

  val d = 2*Pi*f(k)*P
  val C = PolarComplex(1,-d*(L+1))*sin(d*L)/sin(d)/L

  val A = x.indices.foldLeft(Complex.zero)( (s,ell) => s + PolarComplex(1,-d*ell)*x(ell) ) 
  val B = y.indices.foldLeft(Complex.zero)( (s,ell) => s + PolarComplex(1,-d*ell)*y(ell) ) 

  val Qk = (B - B.conjugate*C)/(A -A.conjugate*C)
  Qk //return estimate of the spectrum
}

val R = 27e3 //resistor value
val C = 10e-9 //capacitor value
val RC = 27e-5 //R*C //resistor value.

//hypothesised  spectrum
def Lambda(f : Double) = -1/(1 + 2*Pi*RC*f*j)

//write output to files
{
println("Writing hypothesised spectrum to file hypothesised.csv")
val fmin = 0.0
  val fmax = f(ks.max) + 500.0
  val file = new java.io.FileWriter("hypothesised.csv")
  for(f <- fmin to fmax by 20.0) { //fairly high resolution plot of spectrum
    file.write(f.toString.replace('E', 'e') + "\t" + 
      Lambda(f).magnitude.toString.replace('E', 'e') + "\t" + 
      mod2pi(Lambda(f).angle).toString.replace('E', 'e')+  "\n")
  }
  file.close
}

{
println("Writing measured spectrum to file measured.csv")
  val file = new java.io.FileWriter("measured.csv")
  for(k <- ks)  {
    file.write(f(k).toString.replace('E', 'e') + "\t" + 
      Q(k).magnitude.toString.replace('E', 'e') + "\t" + 
      mod2pi(Q(k).angle).toString.replace('E', 'e') + "\n")
  }
  file.close
}


println("Scala finished")
