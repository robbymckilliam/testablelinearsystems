import sounder.Sounder._
import sounder.Util._
import scala.math.sin
import scala.math.sqrt
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.pow
import scala.math.Pi

print("Plug in the active RC and press ENTER")
System.in.read

println("Playing a sequence of tones and recording input")

val Fs = 44100 
def f(k : Int) = 110.0*pow(2.0,k/2.0) 
val ks = 0 to 12
//for each k = 0,..,21 compute estimated spectrum and return the values
//into the list spectrumestimates
val spectrumestimates = ks.map { k =>

  print(f(k) + "Hz ")

  def xtrue(t : Double) = sin(2*Pi*f(k)*t)
  val (x, y) = playRecord(xtrue, 0, 1, Fs) //play 1s and record

  val L = min(x.length, y.length)   //total number of samples recorded
  if(L < (9999+8820+1)) throw new java.lang.ArrayIndexOutOfBoundsException("Number of samples recorded isn't enough for some reason")

  //compute energy using 8820 samples, chop off first 9999 to avoid potential 
  //distortion when the soundcard starts up 
  val energyx = (9999 until (9999+8820) ).foldLeft(0.0){ (sum, i) => sum + x(i)*x(i) }
  val energyy = (9999 until (9999+8820) ).foldLeft(0.0){ (sum, i) => sum + y(i)*y(i) }

  sqrt(energyy/energyx) //return estimate of the magnitude spectrum
}

val C2 = 200e-9 //capacitor C2 value
val C1 = 2*C2 //capacitor C1 value
val R1 = 560 //resistor values
val R2 = 560
val c = 2009 //cuttoff frequency 1/(sqrt(2)*pi*R1*C1) is approximately 1125

//hypothesised magnitude spectrum
def Lambda(f : Double) = 1.0/sqrt( 1 + pow(f/c,4) )

println("Writing hypothesised spectrum to file hypothesised.csv")
val fmin = 0.0
val fmax = f(ks.max) + 500.0
println(fmax)
val filethypo = new java.io.FileWriter("hypothesised.csv")
for(f <- fmin to fmax by 20.0) { //fairly high resolution plot of spectrum
   filethypo.write(f.toString.replace('E', 'e') + "\t" + Lambda(f).toString.replace('E', 'e') +  "\n")
}
filethypo.close

println("Writing measured spectrum to file measured.csv")
val filemeas = new java.io.FileWriter("measured.csv")
for(k <- ks)  {
   filemeas.write(f(k).toString.replace('E', 'e') + "\t" + spectrumestimates(k).toString.replace('E', 'e') +  "\n")
}
filemeas.close


println("Scala finished")
