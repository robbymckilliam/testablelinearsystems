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

//window width
val W = 3.0

//cuttoff frequency
val c = 1.0;

//rectangular window
def rect(t : Double) = if( abs(t) > W/2 ) Complex.zero else Complex.one

//Triangle (Bartlett) window
def bartlett(t : Double) = {
  if( abs(t) > W/2) Complex.zero 
  else if( t < 0.0) RectComplex(1.0 + 2*t/W,0)
  else RectComplex(1.0 - 2*t/W,0)
}

def hann(t : Double) = rect(t)*(1 + cos(2*Pi*t/W))/2.0

def blackman(t : Double) = {
val a0 = 21.0/50
val a1 = 1.0/2
val a2 = 2.0/25
rect(t)*(a0 + a1*cos(2*Pi*t/W) + a2*cos(4*Pi*t/W))
}

def mod2pi(x : Double) = x - floor(x/2/Pi)*2*Pi

// Approximates the integral of f from a to b using the trapezoidal rule with N intervals.
def trapezoidal(f : Double => Complex, a : Double, b : Double, N : Int) : Complex = {
  val del = (b - a)/N
  val inner = (1 to N-1).foldLeft(Complex.zero)((s,n) => s+f(a + n*del)*2)
  return ( inner + f(a) + f(b) ) * del/2
}

//Fourier transform of windowed function by trapezoidal integration
def F(x : Double => Complex) : Double => Complex = {
  val N = (W*1000).toInt
  f => trapezoidal( t => x(t)*PolarComplex(1.0,-2*Pi*f*t), -W/2, W/2, N)
}

//now write magnitude spectra to file for different windows sinc functions
{
  val fmin = -4.1
  val fmax = 4.1
  val fstep = 0.01

  print("Writing rectangular window ... "); writetofile( F(t => rect(t)*2*c*sinc(2*c*t)), "rect" ); println("done");
  print("Writing Bartlett window ... "); writetofile( F(t => bartlett(t)*2*c*sinc(2*c*t)), "bartlett" ); println("done");
  print("Writing Hann window ... "); writetofile( F(t => hann(t)*2*c*sinc(2*c*t)), "hann" ); println("done");
  print("Writing Blackman window ... "); writetofile( F(t => blackman(t)*2*c*sinc(2*c*t)), "blackman" ); println("done");

  /// Format Doubles string to a reasonable number of decimal places
  def fmt(x : Double) = "%f" format x

  //write Fourier transform to file
  def writetofile( Fx : Double => Complex, fname : String) = {
    val filetfun = new java.io.FileWriter(fname + ".csv")
    (fmin to fmax by fstep).foreach( f => filetfun.write(fmt(f) + "\t" + fmt(Fx(f).magnitude) + "\n") )
    filetfun.close
  }
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
val tol = 1e-3
val Frect = F(rect)
val pass = (-2.0 to 2.0 by 0.1).foldLeft(true)( (p,f) => p & ( (Frect(f) - W*sinc(W*f)).magnitude < tol ) )
println("Fourier test 1 " + {if(pass) "PASSED" else "FAILED"} )
}

//expect Fourier transform of Bartlett window of width W to by W sinc^2(Wf/2)/2
{
def sqr(x : Double) = x*x; //square of a double
val tol = 1e-3
val Fbartlett = F(bartlett)
val pass = (-2.0 to 2.0 by 0.1).foldLeft(true)( (p,f) => p & ( (Fbartlett(f) - W*sqr(sinc(W*f/2))/2).magnitude < tol ) )
println("Fourier test 1 " + {if(pass) "PASSED" else "FAILED"} )
}
