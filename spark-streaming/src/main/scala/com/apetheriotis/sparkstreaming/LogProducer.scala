package com.apetheriotis.sparkstreaming

import kafka.producer.ProducerConfig
import scala.math._
import scala.collection.mutable.ListBuffer

object FFT {


  def main(args: Array[String]) {

    var resourcesWeighted = new ListBuffer[Complex]()
    for (a <- 1 to 2) {
      resourcesWeighted.+=(Complex(a))
    }
    for (a <- 1 to 2) {
      resourcesWeighted.+=(Complex(a))
    }
    for (a <- 1 to 2) {
      resourcesWeighted.+=(Complex(a))
    }
    for (a <- 1 to 2) {
      resourcesWeighted.+=(Complex(a))
    }
    for (a <- 1 to 2) {
      resourcesWeighted.+=(Complex(a))
    }
    for (a <- 1 to 2) {
      resourcesWeighted.+=(Complex(a))
    }
    for (a <- 1 to 2) {
      resourcesWeighted.+=(Complex(a))
    }
    for (a <- 1 to 2) {
      resourcesWeighted.+=(Complex(a))
    }

    resourcesWeighted.toList.foreach(x => println(x.re))
    println("=--")
    //    trans.foreach(println)

    val trans = fft(resourcesWeighted.toList)

    trans.foreach(x => println(sqrt(math.sqrt(x.re) + math.sqrt(x.im))))
    println("=--")

    trans.foreach(x => println(2*log(10)*sqrt(x.re * x.re + x.im * x.im)))
    //    trans.foreach( x =>   println( (sqrt(x.re*x.re + x.im*x.im)))*(1/8)   ))

    for (a <- 0 to 7) {
      //      println(trans(a) * (1 / 8))
      //      println(20 * log10(sqrt(math.sqrt(trans(a).re) + math.sqrt(trans(a).im))))
      //      println(sqrt(math.sqrt(trans(a).re) + math.sqrt(trans(a).im)))
    }

  }


  case class Complex(re: Double, im: Double = 0.0) {
    def +(x: Complex): Complex = Complex((this.re + x.re), (this.im + x.im))

    def -(x: Complex): Complex = Complex((this.re - x.re), (this.im - x.im))

    def *(x: Complex): Complex = Complex(this.re * x.re - this.im * x.im, this.re * x.im + this.im * x.re)
  }

  def transformReal(input: IndexedSeq[Double]) = {
    val data = padder(input.map(i => Complex(i)).toList)
    val outComplex = fft(data)
    outComplex.map(c => math.sqrt((c.re * c.re) + (c.im * c.im))).take((data.length / 2) + 1).toIndexedSeq // Magnitude Output
  }

  def powerSpectrum(input: IndexedSeq[Double]) = {
    val data = padder(input.map(i => Complex(i)).toList)
    val outComplex = fft(data)
    val out = outComplex.map(c => math.sqrt((c.re * c.re) + (c.im * c.im))).take((data.length / 2) + 1).toIndexedSeq
    out.map(i => (i * i) / data.length) // Power Spectral Density Output
  }

  def padder(data: List[Complex]): List[Complex] = {
    def check(num: Int): Boolean = if ((num.&(num - 1)) == 0) true else false
    def pad(i: Int): Int = {
      check(i) match {
        case true => i
        case false => pad(i + 1)
      }
    }
    if (check(data.length) == true) data else data.padTo(pad(data.length), Complex(0))
  }

  def fft(f: List[Complex]): List[Complex] = {
    f.size match {
      case 0 => Nil
      case 1 => f
      case n => {
        val c: Double => Complex = phi => Complex(cos(phi), sin(phi))
        val e = fft(f.zipWithIndex.filter(_._2 % 2 == 0).map(_._1))
        val o = fft(f.zipWithIndex.filter(_._2 % 2 != 0).map(_._1))
        def it(in: List[(Int, Complex)], k: Int = 0): List[(Int, Complex)] = {
          k < (n / 2) match {
            case true => it((k + n / 2, e(k) - o(k) * c(-2 * Pi * k / n)) ::(k, e(k) + o(k) * c(-2 * Pi * k / n)) :: in, k + 1)
            case false => in
          }
        }
        it(List[(Int, Complex)]()).sortWith((x, y) => x._1 < y._1).map(_._2)
      }
    }
  }

}
