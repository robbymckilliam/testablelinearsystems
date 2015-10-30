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
def rect(t : Double) = if( abs(t) > 0.5 ) 0.0 else 1.0

//Triangle (Bartlett) window
def bartlett(t : Double) = {
  if( abs(t) > 0.5) 0.0
  else if( t < 0.0) 1.0 + 2*t
  else 1.0 - 2*t
}

def hann(t : Double) = rect(t)*(1 + cos(2*Pi*t))/2.0

def blackman(t : Double) = {
val a0 = 21.0/50
val a1 = 1.0/2
val a2 = 2.0/25
rect(t)*(a0 + a1*cos(2*Pi*t) + a2*cos(4*Pi*t))
}

//filter parameters
val F = 8000.0
val P = 1/F
val gamma = 2400.0; //cuttoff frequency in Hz
val W = 24.0/gamma; //window width
val a = floor(F*W/2).toInt; //number of taps is 2a+1

writemagspectrum(rect, "rectangular")
writemagspectrum(bartlett, "Bartlett")
writemagspectrum(hann, "Hann")
writemagspectrum(blackman, "Blackman")

def writemagspectrum( window : Double => Double, name : String){

  def w(t : Double) =  window(t/W); //window function.  Using Blackman window with width W
  def h(n : Int) = 2*gamma*P*w(n*P)*sinc(2*gamma*n*P); //filter impulse response

  //discrete time Fourier transform of the impulse response h
  def Dh(f : Double) = (-a to a).foldLeft(Complex.zero)( (s, n) => s + PolarComplex(1,-2*Pi*f*n)*h(n) )

  //spectrum of this FIR filter
  def Lambda(f : Double) = Dh(P*f)

  println(name + " windowed FIR low pass filter with:")
  println(" cuttoff frequency " + gamma)
  println(" window width " + W)
  println(" " + (2*a + 1) + " taps")
  println(" magnitude spectrum at 2300KHz " + Lambda(2300).magnitude)
  println(" magnitude spectrum at 2500KHz " + Lambda(2500).magnitude)

  //write the magnitdue spectrum of this filter to file
  println(" writing magnitude spectrum on interval [0,8kHz] to file " + name + ".csv")
  def fmt(x : Double) = "%f" format x; /// Format Doubles string to a reasonable number of decimal places
  val magLambdafile = new java.io.FileWriter(name + ".csv")
    (0 to 4000 by 2).foreach( f => magLambdafile.write(fmt(f) + "\t" + fmt(Lambda(f).magnitude) + "\n") )
  magLambdafile.close

}
