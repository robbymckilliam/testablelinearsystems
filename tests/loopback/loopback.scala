import sounder.Sounder._
import sounder.Util._
import scala.math.sin
import scala.math.min
import scala.math.max
import scala.math.floor
import scala.math.ceil
import scala.math.Pi

print("Plug the output of the soundcard to the input (loopback) and press ENTER")
System.in.read

playSinusoidAndRecordToFile("loopback.csv")

println("Playing the function sin(200 pi t) for 2 seconds and recording input")

print("Plug in a cable with one channel cut and press ENTER")
System.in.read

playSinusoidAndRecordToFile("cutchannel.csv")

println("Playing the function sin(200 pi t) for 2 seconds and recording input")

println("Scala finished")

// Play sinsoid of 500Hz and records output of left and right channels in file fname
def playSinusoidAndRecordToFile(fname : String){

  val fc = 500
  val Fs = 44100
  val (left, right) = playRecord(t => sin(2*Pi*fc*t), 0, 2.0, Fs)
  //playSamples(left)
  //playSamples(right)

  //total number of samples recorded
  val L = min(left.length, right.length)

  //value at which we truncate sinc function (makes things faster)
  val sinc_truncate = 200

  //reconstructed input signal x
  def x(t : Double) : Double = {
    val mini = max(0, floor(Fs*t - sinc_truncate).toInt)
    val maxi = min(L-1, ceil(Fs*t + sinc_truncate).toInt)
      (mini to maxi).foldLeft(0.0){ (sum, i) => sum + left(i)*sinc(Fs*t - i) }
    //(0 until L).foldLeft(0.0){ (sum, i) => sum + left(i)*sinc(Fs*t - i) }
  }
  //reconstructed output signal y
  def y(t : Double) : Double = {
    val mini = max(0, floor(Fs*t - sinc_truncate).toInt)
    val maxi = min(L-1, ceil(Fs*t + sinc_truncate).toInt)
      (mini to maxi).foldLeft(0.0){ (sum, i) => sum + right(i)*sinc(Fs*t - i) }
    //(0 until L).foldLeft(0.0){ (sum, i) => sum + right(i)*sinc(Fs*t - i) }
  }

  println("Writing data to file data.csv")
  val tmin = 0.998
  val tmax = 1.022
  val filetfun = new java.io.FileWriter(fname)
  (tmin to tmax by 0.00005) foreach { t =>
    filetfun.write(t.toString.replace('E', 'e') + "\t" + x(t).toString.replace('E', 'e')  + "\t" + y(t).toString.replace('E', 'e') + "\n")
  }
  filetfun.close

}
