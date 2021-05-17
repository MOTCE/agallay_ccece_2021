import chisel3._
import chisel3.stage.ChiselGeneratorAnnotation
import chisel3.util._

class MultistageInterconnectionNetwork_FIFO_param(M: Int, messageW: Int, N_ARRAY: Array[Int]) extends Module {
  val io = IO(new Bundle{

    val messageDin = Input(Vec(M, Bits(messageW.W)))
    val destcodeDin = Input(Vec(M, Bits(M.W)))
    val validDin = Input(Vec(M, Bits(1.W)))
    val nFullDin = Input(Vec(M, Bits(1.W)))

    val messageDout = Output(Vec(M, Bits(messageW.W)))
    val validDout = Output(Vec(M, Bits(1.W)))
    val nFullDout = Output(Vec(M, Bits(1.W)))


  })

  val nSwitchPerStage = M/2
  val nStage = log2Ceil(M)
  val nSwitch = nSwitchPerStage * nStage
  val lastSwitchIdxOffset = (nStage - 1)*nSwitchPerStage

  val messageSignals = Wire(Vec((nStage+1)*nSwitchPerStage*2, Bits(messageW.W)))
  val destcodeSignals = Wire(Vec((nStage+1)*nSwitchPerStage*2, Bits(M.W)))
  val validSignals = Wire(Vec((nStage+1)*nSwitchPerStage*2, Bits(1.W)))

  val nFullSignals = Wire(Vec((nStage+1)*nSwitchPerStage*2, Bits(1.W)))


  val switches = for (i <- 0 until nSwitch) yield {
    val stage = i / nSwitchPerStage
    val dividingFactor = math.pow(2, stage).toInt
    if(stage > N_ARRAY.length - 1){
      val switch = Module(new Switch(messageW, (M/dividingFactor), N_ARRAY(N_ARRAY.length - 1)))
      switch
    }else{
      val switch =  Module(new Switch(messageW, (M/dividingFactor), N_ARRAY(stage)))
      switch
    }
  }

  for (idx <- 0 until nSwitchPerStage*2){
    messageSignals(idx) := io.messageDin(idx)
    destcodeSignals(idx) := io.destcodeDin(idx)
    validSignals(idx) := io.validDin(idx)

    io.messageDout(idx) := messageSignals(idx + nStage*nSwitchPerStage*2)
    io.validDout(idx) := validSignals(idx + nStage*nSwitchPerStage*2)

    io.nFullDout(idx) := nFullSignals(idx)

    nFullSignals(idx + nStage*nSwitchPerStage*2) := io.nFullDin(idx)
  }

  for (idx <- 0 until nSwitch){
    switches(idx).io.message0 := messageSignals(2*idx)
    switches(idx).io.message1 := messageSignals(2*idx + 1)
    switches(idx).io.destcode0 := destcodeSignals(2*idx)
    switches(idx).io.destcode1 := destcodeSignals(2*idx + 1)
    switches(idx).io.valid0 := validSignals(2*idx)
    switches(idx).io.valid1 := validSignals(2*idx + 1)

    nFullSignals(2*idx) := switches(idx).io.nFullOutUpper
    nFullSignals(2*idx + 1) := switches(idx).io.nFullOutLower
  }

  for (stage <- 0 until nStage){

    val nBlock = scala.math.pow(2, stage).toInt
    val nSwitchPerBlock = nSwitchPerStage / nBlock

    for (blockIdx <- 0 until nBlock){

      for(switchIdx <- 0 until nSwitchPerBlock){

        val switchGlobalIdx = switchIdx + nSwitchPerStage*stage + blockIdx*nSwitchPerBlock

        val offset = 2*nSwitchPerStage
        var upperOutIdx = 0
        var lowerOutIdx = 0

        if((switchIdx >= 0 && switchIdx < nSwitchPerBlock/2) || stage == nStage - 1){
          upperOutIdx = 2*switchGlobalIdx + offset
          lowerOutIdx = 2*switchGlobalIdx + nSwitchPerBlock + offset
        }else{
          upperOutIdx = 2*switchGlobalIdx + 1 - nSwitchPerBlock + offset
          lowerOutIdx = 2*switchGlobalIdx + 1 + offset
        }

        messageSignals(upperOutIdx) := switches(switchGlobalIdx).io.messageUpperOut
        messageSignals(lowerOutIdx) := switches(switchGlobalIdx).io.messageLowerOut

        destcodeSignals(upperOutIdx) := switches(switchGlobalIdx).io.destcodeUpperOut
        destcodeSignals(lowerOutIdx) := switches(switchGlobalIdx).io.destcodeLowerOut

        validSignals(upperOutIdx) := switches(switchGlobalIdx).io.validUpperOut
        validSignals(lowerOutIdx) := switches(switchGlobalIdx).io.validLowerOut

        switches(switchGlobalIdx).io.nFullInUpper := nFullSignals(upperOutIdx)
        switches(switchGlobalIdx).io.nFullInLower := nFullSignals(lowerOutIdx)

      }
    }
  }

}

object SingleMIN_FIFO_param extends App {

  val DEFAULT_N_PORT = 128
  val DEFAULT_INPUT_W = 32
  val DEFAULT_FIFO_DEPTH = 16

  val chiselStage = new chisel3.stage.ChiselStage()

  chiselStage.emitVerilog(
    new MultistageInterconnectionNetwork_FIFO_param(DEFAULT_N_PORT, DEFAULT_INPUT_W, Array[Int](  22, 21, 20, 19, 18, 17, 16)),
    Array("--target-dir", "./verilog/min_verilog_fifo_param"));

}

object MultipleMinVerilogProduce_FIFO_param extends App {

  val DEFAULT_N_PORT = 8
  val DEFAULT_INPUT_W = 32
  val DEFAULT_FIFO_DEPTH = 64

  val chiselStage = new chisel3.stage.ChiselStage()

  val N_PORT_ARRAY = Array(DEFAULT_N_PORT, 16, 32, 64, 128)
  val INPUT_W_ARRAY = Array(DEFAULT_INPUT_W, 64, 128, 256, 512)
  val FIFO_DEPTH_ARRAY = Array(DEFAULT_FIFO_DEPTH, 128, 256, 512, 1024)

  // Variation of Nb. Ports
  for (idx <- 0 until N_PORT_ARRAY.length){

    val TARGET_DIR = "./verilog/min_verilog_".concat(N_PORT_ARRAY(idx).toString).concat("_32_64")

    chiselStage.emitVerilog(
      new MultistageInterconnectionNetwork_FIFO_param(N_PORT_ARRAY(idx), DEFAULT_INPUT_W, Array[Int](DEFAULT_FIFO_DEPTH)),
      Array("--target-dir", TARGET_DIR));
  }

  // Variation of Port Width
  for (idx <- 0 until INPUT_W_ARRAY.length){

    val TARGET_DIR = "./verilog/min_verilog_8_".concat(INPUT_W_ARRAY(idx).toString).concat("_64")

    chiselStage.emitVerilog(
      new MultistageInterconnectionNetwork_FIFO_param(DEFAULT_N_PORT, INPUT_W_ARRAY(idx), Array[Int](DEFAULT_FIFO_DEPTH)),
      Array("--target-dir", TARGET_DIR));
  }

  // Variation of FIFO Depth
  for (idx <- 0 until FIFO_DEPTH_ARRAY.length){

    val TARGET_DIR = "./verilog/min_verilog_8_32_".concat(FIFO_DEPTH_ARRAY(idx).toString)

    chiselStage.emitVerilog(
      new MultistageInterconnectionNetwork_FIFO_param(DEFAULT_N_PORT, DEFAULT_INPUT_W, Array[Int](FIFO_DEPTH_ARRAY(idx))),
      Array("--target-dir", TARGET_DIR));
  }

}