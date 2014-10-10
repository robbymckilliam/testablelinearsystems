import sounder.Sounder._
import scala.math.sin
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.Pi

println("Plug the output of the soundcard to the input (loopback) and press ENTER")
System.in.read

println("Playing the function 0.5 sin(200 pi t) for 2 seconds and recording input")

val f1 = 100
val f2 = 433
val Fs = 44100
val xtrue : Double => Double = t => sin(2*Pi*f1*t)/3 + sin(2*Pi*f2*t)/3
val (left, right) = playRecord(xtrue, 0, 2.0, Fs)
//playSamples(left)
//playSamples(right)

//total number of samples recorded
val L = min(left.length, right.length)

// sinc function for reconstructing signals
def sinc(t : Double) : Double = { 
    if(t.abs < 5e-3 ) {  //use a 4th order expansion if t is small
      val t2 = t*t
      return  1.0 - t2*( 1.0/6 - 1.0/120*t2 )
    }
    else return sin(Pi*t)/(Pi*t)
}
//value at which we truncate sinc function (makes things faster)
val sinc_truncate = 50

//reconstructed input signal x
def x(t : Double) : Double = {
  val mini = max(0, floor(Fs*t - sinc_truncate).toInt)
  val maxi = min(L-1, ceil(Fs*t + sinc_truncate).toInt)
  (mini to maxi).foldLeft(0.0){ (sum, i) => sum + left(i)*sinc(Fs*t - i) } 
}
//reconstructed output signal y
def y(t : Double) : Double = {
  val mini = max(0, floor(Fs*t - sinc_truncate).toInt)
  val maxi = min(L-1, ceil(Fs*t + sinc_truncate).toInt)
  (mini to maxi).foldLeft(0.0){ (sum, i) => sum + right(i)*sinc(Fs*t - i) } 
}
//H(x) (H is identity in this test)
def Hy(t : Double) : Double = y(t)

println("Writing data to file rc.csv")
val tmin = 1.018
val tmax = 1.042
val filetfun = new java.io.FileWriter("rc.csv")
(tmin to tmax by 0.0002) foreach { t =>
   filetfun.write(t.toString.replace('E', 'e') + "\t" + x(t).toString.replace('E', 'e')  + "\t" + y(t).toString.replace('E', 'e') + "\t" + Hy(t).toString.replace('E', 'e') +  "\n")
}
filetfun.close

println("Scala finished")
