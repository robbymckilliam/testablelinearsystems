import numbers.finite.Complex
import numbers.finite.PolarComplex
import numbers.finite.RectComplex
import sounder.FileIO.MonoWavReader
import sounder.FileIO.MonoWavWriter
import sounder.Sounder._
import sounder.Util._
import scala.math.sin
import scala.math.abs
import scala.math.sqrt
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.round
import scala.math.ceil
import scala.math.pow
import scala.math.Pi

/** Return element of {0,1,...,y-1} equivalent to x mod y */
def mod(x : Int, y : Int) = (x % y + y) % y

/** The fast Fourier transform.  Only works for N a power of 2. */
def fft(N : Int, c : Int => Complex) : Int => Complex = {
  if(N==1) return k => c(0)
  if(N%2 != 0) throw new RuntimeException("N must be a power of 2 for this FFT")
  val Dp = fft(N/2, n => c(2*n))
  val Dq = fft(N/2, n => c(2*n+1))
  val d = (0 to N-1).map( k => Dp(k) + PolarComplex(1,-2*Pi*k/N)*Dq(k) ) //write the values for a single period into an array
  k => d(mod(k,N))
}

/** The inverse fast Fourier transform. Only works for N a power of 2.  */
def ifft(N : Int, d : Int => Complex) : Int => Complex = {
  val cc = fft(N, k => d(k).conjugate)
  k => cc(k).conjugate/N
}

println("Reading file audio.wav")
val wavereader = new MonoWavReader("../fouriertransformlecturevideo/audio.wav") //object for reading wav files
val F = wavereader.sampleRate //sample rate
val P = 1.0/F //sample period
val csamples = wavereader.toArray //read samples to array c
val N = csamples.size
val L = round(pow(2, ceil(log2(N)))).toInt //smallest power of 2 larger than N
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
