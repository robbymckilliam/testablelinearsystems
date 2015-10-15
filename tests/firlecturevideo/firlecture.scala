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

//rectangular function
def rect(t : Double) = if( abs(t) > 0.5 ) Complex.zero else Complex.one

//Triangle (Bartlett) window
def bartlett(t : Double) = {
  if( abs(t) > 0.5) Complex.zero 
  else if( t < 0.0) RectComplex(1.0 + 2*t,0)
  else RectComplex(1.0 - 2*t,0)
}

//Hann (or raised cosine) window
def hann(t : Double) = rect(t)*(1 + cos(2*Pi*t))/2.0

//Blackman window with width 1
def blackman(t : Double) = {
  val a0 = 21.0/50
  val a1 = 1.0/2
  val a2 = 2.0/25
  rect(t)*(a0 + a1*cos(2*Pi*t) + a2*cos(4*Pi*t))
}

//filter parameters
val gamma = 4900.0 //cuttoff frequency
val W = 10.0/gamma //window width
def w(t : Double) =  blackman(t/W) //window function.  Using Blackman window with width W
def h(n : Int) = 2*gamma*P*w(t)*sinc(2*c*P*n) //filter impulse response
val a = floor(F*W/2).toInt //number of taps is 2a+1
def LcPw( x : Double => Complex


println("Reading file audio.wav")
val wavereader = new MonoWavReader("../fouriertransformlecturevideo/audio.wav") //object for reading wav files
val F = wavereader.sampleRate //sample rate
val P = 1.0/F //sample period
val csamples = wavereader.toArray //read samples to array c
val N = csamples.size
println("File contains " + N + " samples at rate " + F + "Hz")

//complex valued sequence with first N elements equal to the audio samples 
def c(n : Int) = if(n>=0 && n<N) RectComplex(csamples(n),0) else Complex.zero 

//fast Fourier transform of length L of c
println("Computing fast Fourier transform")
val DLc = fft(L,c) 

//zero parts of Fourier transform above 7200Hz and within [4900Hz,5900Hz]
def d(k : Int) = {
if(abs(fracpart((1.0*k)/L)) > 7200*P) Complex.zero 
else if(abs(fracpart((1.0*k)/L-5400*P)) < 500*P) Complex.zero
else if(abs(fracpart((1.0*k)/L+5400*P)) < 500*P) Complex.zero
else DLc(k)
}

//inverse discrete Fourier transform to get samples of a signal without hum at 8kHz
println("Computing inverse fast Fourier transform")
val b = ifft(L,d)

//we expect b to be real valued.  Throw an exception if any imaginary parts are largish
val imagtol = 1e-2
(0 to L-1).foreach( n => if( abs(b(n).imag) > imagtol ) throw new RuntimeException("imaginary part of b is not close to zero for some reason!") ) 

//write first N samples to wav file nohum.wav
println("Writing audio to file nohum.wav")
val wavewriter = new MonoWavWriter("nohum.wav",F)
(0 to N-1).foreach( n => wavewriter.put(b(n).real) )
wavewriter.close
