import numbers.finite.Complex
import numbers.finite.PolarComplex
import numbers.finite.RectComplex
import sounder.FileIO.MonoWavReader
import sounder.FileIO.MonoWavWriter
import sounder.Sounder._
import sounder.Util._
import scala.math.sin
import scala.math.cos
import scala.math.abs
import scala.math.sqrt
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.round
import scala.math.ceil
import scala.math.pow
import scala.math.Pi

println("Reading file audio.wav")
val wavereader = new MonoWavReader("../fouriertransformlecturevideo/audio.wav") //object for reading wav files
val F = wavereader.sampleRate //sample rate
val P = 1.0/F //sample period
val csamples = wavereader.toArray //read samples to array c
val N = csamples.size
println("File contains " + N + " samples at rate " + F + "Hz")

//complex valued sequence with first N elements equal to the audio samples 
def c(n : Int) = if(n>=0 && n<N) csamples(n) else 0.0 

//rectangular function
def rect(t : Double) = if( abs(t) > 0.5 ) 0.0 else 1.0

//Blackman window with width 1
def blackman(t : Double) = {
  val a0 = 21.0/50
  val a1 = 1.0/2
  val a2 = 2.0/25
  rect(t)*(a0 + a1*cos(2*Pi*t) + a2*cos(4*Pi*t))
}

//filter parameters
val gamma = 4600.0; //cuttoff frequency in Hz
val W = 12.0/gamma; //window width
val a = floor(F*W/2).toInt; //number of taps is 2a+1
def w(t : Double) =  blackman(t/W); //window function.  Using Blackman window with width W
def h(n : Int) = 2*gamma*P*w(n*P)*sinc(2*gamma*n*P); //filter impulse response

//discrete time Fourier transform of the impulse response h
def Dh(f : Double) = (-a to a).foldLeft(Complex.zero)( (s, n) => s + h(n)*PolarComplex(1,-2*Pi*f*n) )

//spectrum of this FIR filter
def Lambda(f : Double) = Dh(P*f)

println("Blackman windowed FIR low pass filter with:")
println(" cuttoff frequency " + gamma)
println(" window width " + W)
println(" " + (2*a + 1) + " taps")
println(" attenuation at 4KHz " + Lambda(4000).magnitude)
println(" attenuation at 5KHz " + Lambda(5000).magnitude)

//write the magnitdue spectrum of this filter to file 
println("Writing magnitude spectrum on interval [0,8kHz] to file magnitude.csv")
def fmt(x : Double) = "%f" format x; /// Format Doubles string to a reasonable number of decimal places
val magLambdafile = new java.io.FileWriter("magnitude.csv")
(0 to 8000 by 2).foreach( f => magLambdafile.write(fmt(f) + "\t" + fmt(Lambda(f).magnitude) + "\n") )
magLambdafile.close

//discrete convolution of h and c. These are the samples of the response of the filter
def d(n : Int) = (-a to a).foldLeft(0.0)( (s, m) => s + h(m)*c(n-m) )

//write first N samples to wav file nohum.wav
println("Writing filtered audio to file nohum.wav")
val wavewriter = new MonoWavWriter("nohum.wav",F)
(0 to N-1).foreach( n => wavewriter.put(d(n)) )
wavewriter.close
