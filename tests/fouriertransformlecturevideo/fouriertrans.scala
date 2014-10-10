import numbers.finite.Complex
import numbers.finite.PolarComplex
import numbers.finite.RectComplex
import sounder.FileIO.MonoWavReader
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

println("Reading file audio.wav")
val wavereader = new MonoWavReader("audio.wav") //object for reading wav files
val F = wavereader.sampleRate //sample rate
val P = 1.0/F //sample period
val c = wavereader.toArray //read samples to array c
val N = c.size
println("File contains " + N + " samples at rate " + F + "Hz")

//uncomment to play back samples (takes one minute)
//playSamples(c,F)

//the discrete time Fourier transform of samples c
def Dc(f : Double) = (0 to N-1).foldLeft(Complex.zero)( (s, n) => s + PolarComplex(1,-2*Pi*n*f) * c(n) )

//the Fourier transform of the bandlimited signal with samples given by c
def Fx(f : Double) = if( abs(f) < F/2 ) Dc(P*f)*P else Complex.zero

{
print("Writing Fourier transform on interval -12kHz to 12kHz in steps of 20Hz ")
val starttime = (new java.util.Date).getTime; //record the amount of time this takes
writeToFile(-12000.0, 12000.0, 20.0, "ft.csv")
val endtime = (new java.util.Date).getTime;
println(" finished in " + ((endtime-starttime)/1000.0) + " seconds.")
}

{
print("Writing Fourier transform on interval 7998Hz to 8002Hz in steps of 5mHz ")
val starttime = (new java.util.Date).getTime; //record the amount of time this takes
writeToFile(7997.85, 8002.15, 5e-3, "ft8k.csv")
val endtime = (new java.util.Date).getTime;
println(" finished in " + ((endtime-starttime)/1000.0) + " seconds.")
}

/*
{
print("Writing Fourier transform on interval 4900Hz to 5900Hz in steps of 1Hz ")
val starttime = (new java.util.Date).getTime; //record the amount of time this takes
writeToFile(4900, 5900, 1, "ft4900.csv")
val endtime = (new java.util.Date).getTime;
println(" finished in " + ((endtime-starttime)/1000.0) + " seconds.")
}
*/

//a function for writting views of the Fourier transform to a file
def writeToFile(fmin : Double, fmax : Double, fstep : Double, name : String) = {
  val file = new java.io.FileWriter(name)
  for(f <- fmin to fmax by fstep) { //fairly high resolution plot of spectrum
    val Fxf = Fx(f) //compute the Fourier transform at frequency f
    //write out magnitude and phase
    file.write(f.toString.replace('E', 'e') + "\t" + 
      Fxf.magnitude.toString.replace('E', 'e') + "\t" +
      mod2pi(Fxf.angle).toString.replace('E', 'e') + "\n")
    print(".") //this takes a while so print dots as we compute each point
  }
  file.close
}
