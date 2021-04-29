import chisel3._
import chisel3.iotesters._

object parameters_min_FIFO {
  def M(): Int = 8
  def messageW(): Int = 32
  def N(): Array[Int] = Array(8, 4, 2)
}

class Min_FIFO_param_tester(DUT: MultistageInterconnectionNetwork_FIFO_param) extends PeekPokeTester(DUT){


  for(i <- 0 until parameters_min_FIFO.M()){
    poke(DUT.io.validDin(i), 1.B)
    poke(DUT.io.nFullDin(i), 1.B)
    poke(DUT.io.messageDin(i), 20.U)
    poke(DUT.io.destcodeDin(i), 255.U)
  }

  println(DUT.switches(0).fifoUpper0.memoryReg.length.toString)
  println(DUT.switches(1).fifoUpper0.memoryReg.length.toString)
  println(DUT.switches(2).fifoUpper0.memoryReg.length.toString)
  println(DUT.switches(3).fifoUpper0.memoryReg.length.toString)
  println(DUT.switches(4).fifoUpper0.memoryReg.length.toString)
  println(DUT.switches(5).fifoUpper0.memoryReg.length.toString)
  println(DUT.switches(6).fifoUpper0.memoryReg.length.toString)
  println(DUT.switches(7).fifoUpper0.memoryReg.length.toString)
  println(DUT.switches(8).fifoUpper0.memoryReg.length.toString)
  println(DUT.switches(9).fifoUpper0.memoryReg.length.toString)
  println(DUT.switches(10).fifoUpper0.memoryReg.length.toString)
  println(DUT.switches(11).fifoUpper0.memoryReg.length.toString)

  step(100)
}

object Min_FIFO_param_testerWave extends App {
  chisel3.iotesters.Driver.execute(Array("--generate-vcd-output", "on", "--target-dir", "./min_dir"),
    () => new MultistageInterconnectionNetwork_FIFO_param(
      8, 32, parameters_min_FIFO.N()))(DUT => new Min_FIFO_param_tester(DUT))
}

object Min_FIFO_param_tester extends App {
  chisel3.iotesters.Driver(() => new MultistageInterconnectionNetwork_FIFO_param(
    parameters_min_FIFO.M(), parameters_min_FIFO.messageW(), parameters_min_FIFO.N())){
    DUT => new Min_FIFO_param_tester(DUT)
  }
}