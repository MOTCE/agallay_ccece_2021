import java.io._
import chisel3._
import chisel3.iotesters._
import scala._

class MinFifoDepthOptimumTester(DUT: MinParamPeekable, DUT_N: Int, DUT_W: Int, DUT_D_ARRAY: Array[Int], CAN_SEND_DATA: Bits) extends PeekPokeTester(DUT) {
  val DestinationCode = Math.pow(2, DUT_N).toInt - 1;
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

  val file = new FileWriter("optimumMin" + DUT_N + "x" + DUT_N + ".txt", false);
  var MIN_settings = "";
  MIN_settings += DUT_N.toString + ":" + DUT_W.toString + ":";

  for(dut_d <- DUT_D_ARRAY){
    MIN_settings += dut_d + ":";
  }

  file.write(MIN_settings); 

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

object MinFifoDepthOptimumTester extends App {

  val DEFAULT_N_PORT = 8
  val DEFAULT_INPUT_W = 32
  val DEFAULT_FIFO_DEPTH = 16

  //  val N_PORT_ARRAY = Array(DEFAULT_N_PORT, 16, 32, 64, 128)
  //  val INPUT_W_ARRAY = Array(DEFAULT_INPUT_W)
  //  val FIFO_DEPTH_ARRAY = Array(DEFAULT_FIFO_DEPTH, 32, 64, 128, 256)

  chisel3.iotesters.Driver(() => new MinParamPeekable(
    4, DEFAULT_INPUT_W, Array(17, 16))) {
    DUT => new MinFifoDepthOptimumTester(DUT, 4, DEFAULT_INPUT_W, Array(17, 16), 1.B)
  }

  chisel3.iotesters.Driver(() => new MinParamPeekable(
    8, DEFAULT_INPUT_W, Array(18, 17, 16))) {
    DUT => new MinFifoDepthOptimumTester(DUT, 8, DEFAULT_INPUT_W, Array(18, 17, 16), 1.B)
  }

  chisel3.iotesters.Driver(() => new MinParamPeekable(
    16, DEFAULT_INPUT_W, Array(19, 18, 17, 16))) {
    DUT => new MinFifoDepthOptimumTester(DUT, 16, DEFAULT_INPUT_W, Array(19, 18, 17, 16), 1.B)
  }
}