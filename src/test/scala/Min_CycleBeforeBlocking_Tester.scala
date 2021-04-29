import java.io._

import chisel3._
import chisel3.iotesters._


// Base width = 32

class Min_CycleBeforeBlocking_Tester(DUT: MultistageInterconnectionNetwork, DUT_N: Int, DUT_W: Int, DUT_D: Int) extends PeekPokeTester(DUT) {

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

  var stepCount = 0;

  while(!(peek(DUT.io.nFullDout).contains(ExpectedNotFullToStop))){
    step(1);
    stepCount += 1;
  }

  val file = new FileWriter("rapportCycle.txt", true);
  file.write(DUT_N.toString + ":" + DUT_W.toString + ":" + DUT_D.toString + " => " + stepCount.toString + " cycles \n");
  file.close();
}

class Min_CycleBeforeBlocking_Tester_stage(DUT: MultistageInterconnectionNetwork, DUT_N: Int, DUT_W: Int, DUT_D: Int) extends PeekPokeTester(DUT) {

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

  var stepCount = 0;

  var thirdLevelFull = false;
  var secondLevelFull = false;
  var firstLevelFull = false;

  while(!firstLevelFull){

    if(!thirdLevelFull){
      for(idx_lvl3 <- 0 until 4){
        if((DUT.switches(8 + idx_lvl3).io.nFullOutUpper == 0 || DUT.switches(8 + idx_lvl3).io.nFullOutLower == 0)){
          println("Switch " + (8 + idx_lvl3).toString + ": " + stepCount.toString);
          thirdLevelFull = true;
        }
      }
    }


    if(!secondLevelFull){
      for(idx_lvl2 <- 0 until 4){
        if((DUT.switches(4 + idx_lvl2).io.nFullOutUpper == 0 || DUT.switches(4 + idx_lvl2).io.nFullOutLower == 0)){
          println("Switch " + (4 + idx_lvl2).toString + ": " + stepCount.toString);
          secondLevelFull = true;
        }
      }
    }


    if(!firstLevelFull){
      for(idx_lvl1 <- 0 until 4){
        if((DUT.switches(idx_lvl1).io.nFullOutUpper == 0 || DUT.switches(idx_lvl1).io.nFullOutLower == 0)){
          println("Switch " + (idx_lvl1).toString + ": " + stepCount.toString);
          firstLevelFull = true;
        }
      }
    }

    step(1)
    stepCount += 1
  }

}

object Single_Min_BeforeBlocking_Tester extends App {
  chisel3.iotesters.Driver(() => new MultistageInterconnectionNetwork(
    8, 32, 16)){
    DUT => new Min_CycleBeforeBlocking_Tester_stage(DUT, 8, 32, 16)
}}

object Min_CycleBeforeBlocking_Tester extends App {

  val DEFAULT_N_PORT = 8
  val DEFAULT_INPUT_W = 32
  val DEFAULT_FIFO_DEPTH = 2

  val N_PORT_ARRAY = Array(DEFAULT_N_PORT, 16, 32, 64, 128)
  val INPUT_W_ARRAY = Array(DEFAULT_INPUT_W, 64, 128, 256, 512)
  val FIFO_DEPTH_ARRAY = Array(DEFAULT_FIFO_DEPTH, 4, 8, 16, 32, 64)

  for (nIdx<- 0 until N_PORT_ARRAY.length) {
    for (wIdx<- 0 until INPUT_W_ARRAY.length) {
      for (dIdx<- 0 until FIFO_DEPTH_ARRAY.length) {
        chisel3.iotesters.Driver(() => new MultistageInterconnectionNetwork(
          N_PORT_ARRAY(nIdx), INPUT_W_ARRAY(wIdx), FIFO_DEPTH_ARRAY(dIdx))){
          DUT => new Min_CycleBeforeBlocking_Tester(DUT, N_PORT_ARRAY(nIdx), INPUT_W_ARRAY(wIdx), FIFO_DEPTH_ARRAY(dIdx))
        }
      }
    }
  }
}