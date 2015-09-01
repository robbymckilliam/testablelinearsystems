import numbers.finite.Complex
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
import scala.math.Pi

print("Plug in the Butterworth filter and press ENTER")
System.in.read

println("Playing a sequence of tones and recording input")

val Fs = 44100 //sample rate (CD quality
val P = 1.0/Fs //sample period
def f(k : Int) = round(110.0*pow(2.0,(k+1)/2.0)) 
val ks = 0 to 12
val discard = 10000 //the number of samples we will discard to account for distortion
val L = 8820 //the number of sample we will take out (corresponds with 200ms of signal)
val j = RectComplex(0,1) //the square root of -1

//for each k = 0,..,21 compute estimated spectrum and return the values
//into the list spectrumestimates
val Q : Seq[Complex] = ks.map { k =>

  print(f(k) + "Hz ")

  def xtrue(t : Double) = sin(2*Pi*f(k)*t)
  val (ys, xs) = playRecord(xtrue, 0, 1.0, Fs) //play for 1 second and record


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

val C2 = 1e-7 //capacitor C2 value
val C1 = 2*C2 //capacitor C1 value
val R1 = 560 //resistor values
val R2 = 560
val c = 1.0/sqrt(2.0)/Pi/R1/C1 //cuttoff frequency 1/(sqrt(2)*pi*R1*C1) is approximately 1125
println("Cuttoff frequency is " + c)

//hypothesised transfer function
def lambda(s : Complex) = {
val beta1 = PolarComplex(1,3*Pi/4.0)
val beta2 = beta1.conjugate
val s2pic = s/2.0/Pi/c
Complex.one/(s2pic - beta1)/(s2pic - beta2)
}

//hypothesised  spectrum
def Lambda(f : Double) = lambda(j*2*Pi*f)

//write output to files
{
println("Writing hypothesised spectrum to file hypothesised.csv")
val fmin = 0.001
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
