import chisel3.iotesters.PeekPokeTester
import org.scalatest._
import chisel3._

class SandboxTester(DUT: Sandbox) extends PeekPokeTester(DUT){
  poke(DUT.io.in0, 1.U)
  poke(DUT.io.in1, 4.U)
  println("Résultat de cat: " + peek(DUT.io.dout).toString)
  val test1 = 1/4
  println("Test 1/4:" + test1.toString)

  for(i <- 0 until 1){
    println("zero")
  }

}

object SandboxTester extends App {
  // Fork avec messageW = 8 et destcodeW = 8 par défaut, cf main/scala/Fork.scala
  chisel3.iotesters.Driver(() => new Sandbox()){
    DUT => new SandboxTester(DUT)
  }
}