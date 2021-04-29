import java.io._

import chisel3._
import chisel3.iotesters._


// Base width = 32

class MinParamPeekable_CBB_Tester(DUT: MinParamPeekable, DUT_N: Int, DUT_W: Int, DUT_D_ARRAY: Array[Int], CAN_SEND_DATA: Bits) extends PeekPokeTester(DUT) {

  val DestinationCode = 255.U;
  val DummyValue = 18.U;
  val ValidBit = 1.B;
  val NotFullDinBit = CAN_SEND_DATA;
  val ExpectedNotFullToStop = 0;

  for(i <- 0 until DUT_N){
    poke(DUT.io.validDin(i), ValidBit)
    poke(DUT.io.nFullDin(i), NotFullDinBit)
    poke(DUT.io.messageDin(i), DummyValue)
    poke(DUT.io.destcodeDin(i), DestinationCode)
  }

  val file = new FileWriter("rapportCycleFIFOParam" + CAN_SEND_DATA.toString + ".txt", true);
  file.write(DUT_N.toString + ":" + DUT_W.toString + ":" + DUT_D_ARRAY(0).toString + ":" + DUT_D_ARRAY(1) + "\n");

  var stepCount = 0;
  var canProceed = true;

  while(!(peek(DUT.io.nFullDout).contains(ExpectedNotFullToStop))){
    file.write("-------------------- \n")
    file.write("Cycle " + stepCount.toString + "\n");
    file.write(peek(DUT.io.fifoNValues).toString + "\n");
    file.write("-------------------- \n")
    step(1)
    stepCount += 1
  }
  file.write("-------------------- \n")
  file.write("Cycle " + stepCount.toString + "\n");
  file.write(peek(DUT.io.fifoNValues).toString + "\n");
  file.write("-------------------- \n")

  file.write("Cycles av. blocage: " + (stepCount - 1).toString + " cycles \n");
  file.write("-------------------- \n")
  file.close();

}



object MinParamPeekable_CBB_Tester_openData extends App {

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
  //    } q
  //  }

  chisel3.iotesters.Driver(() => new MinParamPeekable(
    4, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH))) {
    DUT => new MinParamPeekable_CBB_Tester(DUT, 4, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH, DEFAULT_FIFO_DEPTH), 1.B)
  }

  chisel3.iotesters.Driver(() => new MinParamPeekable(
    8, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH))) {
    DUT => new MinParamPeekable_CBB_Tester(DUT, 8, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH, DEFAULT_FIFO_DEPTH), 1.B)
  }

  chisel3.iotesters.Driver(() => new MinParamPeekable(
    16, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH))) {
    DUT => new MinParamPeekable_CBB_Tester(DUT, 16, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH, DEFAULT_FIFO_DEPTH), 1.B)
  }
}

object MinParamPeekable_CBB_Tester_closeData extends App {

  val DEFAULT_N_PORT = 8
  val DEFAULT_INPUT_W = 32
  val DEFAULT_FIFO_DEPTH = 16

//  val N_PORT_ARRAY = Array(DEFAULT_N_PORT, 16, 32, 64, 128)
//  val INPUT_W_ARRAY = Array(DEFAULT_INPUT_W)
//  val FIFO_DEPTH_ARRAY = Array(DEFAULT_FIFO_DEPTH, 32, 64, 128, 256)

  chisel3.iotesters.Driver(() => new MinParamPeekable(
    4, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH))) {
    DUT => new MinParamPeekable_CBB_Tester(DUT, 4, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH, DEFAULT_FIFO_DEPTH), 0.B)
  }

  chisel3.iotesters.Driver(() => new MinParamPeekable(
    8, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH))) {
    DUT => new MinParamPeekable_CBB_Tester(DUT, 8, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH, DEFAULT_FIFO_DEPTH), 0.B)
  }

  chisel3.iotesters.Driver(() => new MinParamPeekable(
    16, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH))) {
    DUT => new MinParamPeekable_CBB_Tester(DUT, 16, DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH, DEFAULT_FIFO_DEPTH), 0.B)
  }
}



