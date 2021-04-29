import java.io._

import chisel3.{Bits, _}
import chisel3.iotesters._
import chisel3.util.experimental.BoringUtils
import chisel3.util.log2Ceil


// Base width = 32

class Min_FIFO_param_CycleBeforeBlocking_Tester(DUT: MultistageInterconnectionNetwork_FIFO_param, DUT_N: Int, DUT_W: Int, DUT_D_ARRAY: Array[Int]) extends PeekPokeTester(DUT) {

  val DestinationCode = 255.U;
  val DummyValue = 18.U;
  val ValidBit = 1.B;
  val NotFullDinBit = 1.B;
  val ExpectedNotFullToStop = 0;

  for(i <- 0 until DUT_N){
    poke(DUT.io.validDin(i), ValidBit)
    poke(DUT.io.nFullDin(i), NotFullDinBit)
    poke(DUT.io.messageDin(i), DummyValue)
    poke(DUT.io.destcodeDin(i), DestinationCode)
  }

  val file = new FileWriter("rapportCycleFIFOParam.txt", true);
  file.write(DUT_N.toString + ":" + DUT_W.toString + ":" + DUT_D_ARRAY(0).toString + ":" + DUT_D_ARRAY(1) + "\n");

  var stepCount = 0;

  while(!(peek(DUT.io.nFullDout).contains(ExpectedNotFullToStop))){
    step(1);
    file.write("-------------------- \n")
    file.write("Cycle " + stepCount.toString + "\n");
    for(switchIdx <- 0 until DUT.switches.length){
      file.write("Sw " + switchIdx.toString + "\n");
      file.write(0 + " " + DUT.switches(switchIdx).fifoUpper0.nb_valuesReg + "\n");
      file.write(1 + " " + DUT.switches(switchIdx).fifoUpper1.nb_valuesReg + "\n");
      file.write(2 + " " + DUT.switches(switchIdx).fifoLower0.nb_valuesReg + "\n");
      file.write(3 + " " + DUT.switches(switchIdx).fifoLower1.nb_valuesReg + "\n");
    }
    file.write("-------------------- \n")
    stepCount += 1;
  }

  file.write("Cycles av. blocage: " + stepCount.toString + " cycles \n");
  file.write("-------------------- \n")
  file.close();
}

object Min_FIFO_param_CycleBeforeBlocking_Tester extends App {

  val DEFAULT_N_PORT = 8
  val DEFAULT_INPUT_W = 32
  val DEFAULT_FIFO_DEPTH = 16
  //
  //  val N_PORT_ARRAY = Array(DEFAULT_N_PORT, 16, 32, 64, 128)
  //  val INPUT_W_ARRAY = Array(DEFAULT_INPUT_W)
  //  val FIFO_DEPTH_ARRAY = Array(DEFAULT_FIFO_DEPTH, 32, 64, 128, 256)
  //
  //  for (nIdx<- 0 until N_PORT_ARRAY.length) {
  //    for (wIdx<- 0 until INPUT_W_ARRAY.length) {
  //      for (dIdx<- 0 until FIFO_DEPTH_ARRAY.length) {
  //        chisel3.iotesters.Driver(() => new MultistageInterconnectionNetwork_FIFO_param(
  //          N_PORT_ARRAY(nIdx), INPUT_W_ARRAY(wIdx), Array(FIFO_DEPTH_ARRAY(dIdx), DEFAULT_FIFO_DEPTH))){
  //          DUT => new Min_FIFO_param_CycleBeforeBlocking_Tester(DUT, N_PORT_ARRAY(nIdx), INPUT_W_ARRAY(wIdx), Array(FIFO_DEPTH_ARRAY(dIdx), DEFAULT_FIFO_DEPTH))
  //        }
  //      }
  //    }
  //  }

  chisel3.iotesters.Driver(() => new MultistageInterconnectionNetwork_FIFO_param(
    DEFAULT_N_PORT, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH))) {
    DUT => new Min_FIFO_param_CycleBeforeBlocking_Tester(DUT, DEFAULT_N_PORT, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH, DEFAULT_FIFO_DEPTH))
  }
}

