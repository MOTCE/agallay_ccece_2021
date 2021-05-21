import java.io._
import chisel3._
import chisel3.iotesters._
import chisel3.util._

class Min_CycleBeforeBlocking_Tester(DUT: MinParamPeekable, DUT_N: Int, DUT_W: Int, DUT_D_ARRAY: Array[Int], CAN_SEND_DATA: Bits, DESTINATION_FOLDER: String) extends PeekPokeTester(DUT) {

  // Broadcast destination code
  var BitsString = "b";
  for(i <- 0 until DUT_N){
    BitsString = BitsString + "1";
  }
  val DestinationCode = BitsString.U;

  val DummyValue = 18.U;
  val ValidBit = 1.B;
  val NotFullDinBit = CAN_SEND_DATA;
  val ExpectedNotFullToStop = 0;

  // Computing capacity for analysis purposes
  var capacity = 0;
  for(i <- 0 until DUT_D_ARRAY.length){
    capacity = capacity + 2*DUT_N*DUT_D_ARRAY(i)
  }
  if(DUT_D_ARRAY.length < log2Ceil(DUT_N)){
    val difference = log2Ceil(DUT_N) - DUT_D_ARRAY.length;
    capacity = capacity + difference*DUT_D_ARRAY(DUT_D_ARRAY.length - 1)*DUT_N*2;
  }
  val nbRegister = log2Ceil(DUT_N)*DUT_N*2
  capacity = capacity + nbRegister

  // Poking MIN's inputs
  for(i <- 0 until DUT_N){
    poke(DUT.io.validDin(i), ValidBit)
    poke(DUT.io.nFullDin(i), NotFullDinBit)
    poke(DUT.io.messageDin(i), DummyValue)
    poke(DUT.io.destcodeDin(i), DestinationCode)
  }

  // Computing filename
  var filename = "rapportsCyclesFifo\\" + DESTINATION_FOLDER + "\\optimumMin" + DUT_N + "x" + DUT_N + "-";
  for(depthIdx <- 0 until DUT_D_ARRAY.length){
    if(depthIdx != DUT_D_ARRAY.length - 1){
      filename += DUT_D_ARRAY(depthIdx) + "-";
    }else {
      filename += DUT_D_ARRAY(depthIdx);
    }
  }
  filename += ".txt";

  val file = new FileWriter(filename, false);
  var MIN_settings = "";
  MIN_settings += DUT_N.toString + ":" + DUT_W.toString + ":";

  file.write(MIN_settings);

  var stepCount = 0;

  // Stepping simulation until deadlock occurs
  while(!(peek(DUT.io.nFullDout).contains(ExpectedNotFullToStop))){
//    Uncomment this section to obtain the number of values inside FIFOs on each clock cycle
//    file.write("-------------------- \n")
//    file.write("Cycle " + stepCount.toString + "\n");
//    file.write(peek(DUT.io.fifoNValues).toString + "\n");
//    file.write("-------------------- \n")
    step(1)
    stepCount += 1
  }

  file.write("-------------------- \n")
  file.write("Cycle " + stepCount.toString + "\n");
  file.write(peek(DUT.io.fifoNValues).toString + "\n");
  file.write("-------------------- \n")

  file.write("Cycles before deadlock: " + (stepCount - 1).toString + " cycles \n");
  file.write("-------------------- \n")
  file.write("Number of FIFOs' registers: " + nbRegister.toString + "\n");
  file.write("Capacity: " + capacity.toString + "\n");
  file.close();
}

object Deadlock_Single_Depth extends App {

  val DEFAULT_N_PORT = 4
  val DEFAULT_INPUT_W = 32
  val DEFAULT_FIFO_DEPTH = 16

  val N_PORT_ARRAY = Array(DEFAULT_N_PORT, 8, 16, 32, 64, 128, 256, 512)

  for (nIdx<- 0 until N_PORT_ARRAY.length) {
    chisel3.iotesters.Driver(() => new MinParamPeekable(
      N_PORT_ARRAY(nIdx), DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH))) {
      DUT => new Min_CycleBeforeBlocking_Tester(DUT, N_PORT_ARRAY(nIdx), DEFAULT_INPUT_W, Array(DEFAULT_FIFO_DEPTH), 1.B, "profondeurFixe")
    }
  }

}

object Deadlock_Multiple_Depth extends App {

  val DEFAULT_N_PORT = 4
  val DEFAULT_INPUT_W = 32
  val DEFAULT_FIFO_DEPTH = 16

  val N_PORT_ARRAY = Array(DEFAULT_N_PORT, 8, 16, 32, 64, 128, 256, 512)

  for (nIdx<- 0 until N_PORT_ARRAY.length) {

    val nStage = log2Ceil(N_PORT_ARRAY(nIdx))
    var FIFO_DEPTH_ARRAY = Array[Int]()

    for(i <- 0 until nStage){
      FIFO_DEPTH_ARRAY = FIFO_DEPTH_ARRAY :+ DEFAULT_FIFO_DEPTH + nStage - i - 1
      println(FIFO_DEPTH_ARRAY(i))
    }

    chisel3.iotesters.Driver(() => new MinParamPeekable(
      N_PORT_ARRAY(nIdx), DEFAULT_INPUT_W, FIFO_DEPTH_ARRAY)) {
      DUT => new Min_CycleBeforeBlocking_Tester(DUT, N_PORT_ARRAY(nIdx), DEFAULT_INPUT_W, FIFO_DEPTH_ARRAY, 1.B, "profondeurMultiple")
    }
  }

}

object Effective_Capacity_Single_Depth extends App {
  val N_PORT_ARRAY = Array(4, 8, 16, 32, 64, 128);
  val FIFO_DEPTH_ARRAY = Array(4, 8, 32, 64, 128);
  val DEFAULT_INPUT_W = 32;

  for(nIdx <- 0 until N_PORT_ARRAY.length){;
    for(depthIdx <- 0 until FIFO_DEPTH_ARRAY.length){
      chisel3.iotesters.Driver(() => new MinParamPeekable(
        N_PORT_ARRAY(nIdx), DEFAULT_INPUT_W, Array(FIFO_DEPTH_ARRAY(depthIdx)))) {
        DUT => new Min_CycleBeforeBlocking_Tester(DUT, N_PORT_ARRAY(nIdx), DEFAULT_INPUT_W, Array(FIFO_DEPTH_ARRAY(depthIdx)), 1.B, "profondeurFixe")
      }
    }
  }
}

object Effective_Capacity_Multiple_Depth extends App {
  val N_PORT_ARRAY = Array(4, 8, 16, 32, 64, 128);
  val FIFO_DEPTH_ARRAY = Array(4, 8, 32, 64, 128);
  val DEFAULT_INPUT_W = 32;

  for(nIdx <- 0 until N_PORT_ARRAY.length){
    val nStage = log2Ceil(N_PORT_ARRAY(nIdx));
    for(depthIdx <- 0 until FIFO_DEPTH_ARRAY.length){

      var fifoDepthArray = Array[Int]();

      for(stageIdx <- 1 to nStage) {
        fifoDepthArray = fifoDepthArray :+ FIFO_DEPTH_ARRAY(depthIdx) + (nStage - stageIdx);
      }

      chisel3.iotesters.Driver(() => new MinParamPeekable(
        N_PORT_ARRAY(nIdx), DEFAULT_INPUT_W, fifoDepthArray)) {
        DUT => new Min_CycleBeforeBlocking_Tester(DUT, N_PORT_ARRAY(nIdx), DEFAULT_INPUT_W, fifoDepthArray, 1.B, "profondeurMultiple")
      }
    }
  }
}
