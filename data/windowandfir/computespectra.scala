import numbers.finite.Complex
import numbers.finite.PolarComplex
import numbers.finite.RectComplex
import scala.math.abs
import scala.math.sin
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.cos
import scala.math.Pi
import sounder.Util.sinc

println("Generating spectra of windows finite impulse response filters")

//rectangular window
def rect(t : Double) = if( abs(t) > 0.5 ) Complex.zero else Complex.one

//Triangle (Bartlett) window
def bartlett(t : Double) = {
  if( abs(t) > 0.5) Complex.zero 
  else if( t < 0.0) RectComplex(1.0 + 2*t,0)
  else RectComplex(1.0 - 2*t,0)
}

def hann(t : Double) = rect(t)*(1 + cos(2*Pi*t))/2.0

def blackman(t : Double) = {
val a0 = 21.0/50
val a1 = 1.0/2
val a2 = 2.0/25
rect(t)*(a0 + a1*cos(2*Pi*t) + a2*cos(4*Pi*t))
}

// Approximates the integral of f from a to b using the trapezoidal rule with N intervals.
def trapezoidal(f : Double => Complex, a : Double, b : Double, N : Int) : Complex = {
  val del = (b - a)/N
  val inner = (1 to N-1).foldLeft(Complex.zero)((s,n) => s+f(a + n*del)*2)
  return ( inner + f(a) + f(b) ) * del/2
}

//Fourier transform of windowed function by trapezoidal integration
def Fwindowed(W: Double, x : Double => Complex) : Double => Complex = {
  val N = (W*1000).toInt
  f => trapezoidal( t => x(t)*PolarComplex(1.0,-2*Pi*f*t), -W/2, W/2, N)
}


/// Format Doubles string to a reasonable number of decimal places
def fmt(x : Double) = "%f" format x

computeallresponseplot(1.0,1.0)
computeallresponseplot(2.0,1.0)
computeallresponseplot(3.0,1.0)
computeallresponseplot(4.0,1.0)
computeallresponseplot(5.0,1.0)
//computeallresponseplot(8.0,1.0)
//computeall(9.0,1.0)
computeallresponseplot(10.0,1.0)

// Compute all windows with width W and cuttoff frequency c
def computeallresponseplot(W : Double, c : Double) = {

  println("Computing with window width " + W + " and cuttoff frequency " + c)

  //period and sample rate of our discrete time system
  val Pd = 1.0/4
  val Fd = 1.0/Pd

  def F(x: Double => Complex) : Double => Complex = Fwindowed(W,x)

  def Fper(x: Double => Complex) : Double => Complex = {
    val Ft = F(x); //compute Fourier transform
                   //return periodised Fourier transform. Summing more than 3 is overkill
    f => Ft(f - Fd) + Ft(f) + Ft(f + Fd)
  }

  //now write magnitude spectra to file for different windows sinc functions
  val fstep = 0.01
  val wcstr = "_W" + W + "_c" + c

  print("Writing rectangular window ... ")
  writefilterandresponsetofile( F(t => rect(t/W)*2*c*sinc(2*c*t)), "rect" + wcstr )
  writefilterandresponsetofile( Fper(t => rect(t/W)*2*c*sinc(2*c*t)), "rect_periodised" + wcstr )
  println("done");

  print("Writing Bartlett window ... ")
  writefilterandresponsetofile( F(t => bartlett(t/W)*2*c*sinc(2*c*t)), "bartlett" + wcstr)
  writefilterandresponsetofile( Fper(t => bartlett(t/W)*2*c*sinc(2*c*t)), "bartlett_periodised" + wcstr)
  println("done")
  
  print("Writing Hann window ... ")
  writefilterandresponsetofile( F(t => hann(t/W)*2*c*sinc(2*c*t)), "hann" + wcstr)
  writefilterandresponsetofile( Fper(t => hann(t/W)*2*c*sinc(2*c*t)), "hann_periodised" + wcstr)
  println("done")

  print("Writing Blackman window ... ")
  writefilterandresponsetofile( F(t => blackman(t/W)*2*c*sinc(2*c*t)), "blackman" + wcstr)
  writefilterandresponsetofile( Fper(t => blackman(t/W)*2*c*sinc(2*c*t)), "blackman_periodised" + wcstr)
  println("done")

  //write Fourier and response to file transform to file
  def writefilterandresponsetofile( Fx : Double => Complex, fname : String) = {
    writetofile(f => Fx(f).magnitude, fname + ".csv", -4.3, 4.3, fstep)
    //left hand raised cosine
    writetofile(f => (Fx(f)*cos(2*Pi*f)).magnitude, fname + "_left.csv", -1.75, -1.25, fstep)
    //left hand raised cosine
    writetofile(f => (Fx(f)*cos(2*Pi*f)).magnitude, fname + "_right.csv", 1.25, 1.75, fstep)
    //centered rectangular pulse
    writetofile(f => Fx(f).magnitude*4.0/3.0, fname + "_middle.csv", -0.5, 0.5, fstep)
    //write window straight to file in range -0.5 to 3.0
    writetofile(f=>Fx(f).real, fname + "_re", -0.2,3.1,0.005)
  }

}

//write Fourier transform to file
def writetofile( x : Double => Double, fname : String, fmin : Double, fmax : Double, fstep : Double) = {
  val filetfun = new java.io.FileWriter(fname + ".csv")
    (fmin to fmax by fstep).foreach( f => filetfun.write(fmt(f) + "\t" + fmt(x(f)) + "\n") )
  filetfun.close
}

println("Scala finished")




///// Below are some tests on the trapezoidal integration and Fourier transform

//test trapezoidal method
{
val N = 1000
val tol = 2.0/N
def f(t: Double) = Complex.one
val intg = trapezoidal(f, 0,1,N)
val pass = (intg - 1.0).magnitude < tol
println("trapezoidal test 1 " + {if(pass) "PASSED" else "FAILED"} )
}
{
val N = 1000
val tol = 2.0/N
def f(t: Double) = RectComplex(1,t)
val intg = trapezoidal(f, 0,1,N)
val expt = RectComplex(1,0.5)
val pass = (intg - expt).magnitude < tol
println("trapezoidal test 2 " + {if(pass) "PASSED" else "FAILED"} )
}
{
val N = 1000
val tol = 2.0/N
def f(t: Double) = RectComplex(t,t*t)
val intg = trapezoidal(f, 0,1,N)
val expt = RectComplex(0.5,1.0/3.0)
val pass = (intg - expt).magnitude < tol
println("trapezoidal test 3 " + {if(pass) "PASSED" else "FAILED"} )
}

//test Fourier transform
//expect Fourier transform of rectangular window of width W to by Wsinc(Wf)
{
val W = 3.0
val tol = 1e-3
val Frect = Fwindowed(W,t => rect(t/W))
val pass = (-2.0 to 2.0 by 0.1).foldLeft(true)( (p,f) => p & ( (Frect(f) - W*sinc(W*f)).magnitude < tol ) )
println("Fourier test 1 " + {if(pass) "PASSED" else "FAILED"} )
}

//expect Fourier transform of Bartlett window of width W to by W sinc^2(Wf/2)/2
{
val W = 3.0
def sqr(x : Double) = x*x; //square of a double
val tol = 1e-3
val Fbartlett = Fwindowed(W,t => bartlett(t/W))
val pass = (-2.0 to 2.0 by 0.1).foldLeft(true)( (p,f) => p & ( (Fbartlett(f) - W*sqr(sinc(W*f/2))/2).magnitude < tol ) )
println("Fourier test 1 " + {if(pass) "PASSED" else "FAILED"} )
}
