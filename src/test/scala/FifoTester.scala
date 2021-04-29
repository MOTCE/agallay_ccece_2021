import chisel3._
import chisel3.iotesters._
import firrtl.CommonOptions

class FifoTester(DUT: Fifo) extends PeekPokeTester(DUT){

  poke(DUT.io.din, 255.U(32.W))
  println(DUT.memoryReg.length.toString())
  poke(DUT.io.wr_en, 1.B)
  poke(DUT.io.rd_en, 0.B)
  step(50)
  poke(DUT.io.wr_en, 0.B)
  poke(DUT.io.rd_en, 1.B)
  step(1)
  println(DUT.memoryReg.length.toString())

}

object FifoTester extends App {
  // Fork avec messageW = 8 et destcodeW = 8 par défaut, cf main/scala/Fork.scala
  chisel3.iotesters.Driver(() => new Fifo()){
    DUT => new FifoTester(DUT)
  }
}

object FifoTesterWave extends App {

  // Permet la génération d'un fichier VCD exploitable avec GTKWave
  chisel3.iotesters.Driver.execute(Array("--generate-vcd-output", "on"), () => new Fifo())(DUT => new FifoTester(DUT))
}