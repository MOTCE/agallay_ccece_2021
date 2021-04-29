import chisel3._
import chisel3.iotesters._



object parameters {
  def M(): Int = 8
  def messageW(): Int = 32
  def N(): Int = 16
}

class MinTester(DUT: MultistageInterconnectionNetwork) extends PeekPokeTester(DUT){


  for(i <- 0 until parameters_min_FIFO.M()){
    poke(DUT.io.validDin(i), 1.B)
    poke(DUT.io.nFullDin(i), 1.B)
    poke(DUT.io.messageDin(i), 20.U)
    poke(DUT.io.destcodeDin(i), 255.U)
  }

  step(100)
}

object MinTesterWave extends App {
  chisel3.iotesters.Driver.execute(Array("--generate-vcd-output", "on", "--target-dir", "./min_dir"),
    () => new MultistageInterconnectionNetwork(
      8, 32, parameters.N()))(DUT => new MinTester(DUT))
}

object MinTester extends App {
  chisel3.iotesters.Driver(() => new MultistageInterconnectionNetwork(
    parameters.M(), parameters.messageW(), parameters.N())){
    DUT => new MinTester(DUT)
  }
}