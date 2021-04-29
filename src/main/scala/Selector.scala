import chisel3._
import chisel3.stage.ChiselGeneratorAnnotation
import chisel3.util._

class Selector(destcodeWidth: Int = 8, messageWidth: Int = 8) extends Module {
  val io = IO(new Bundle{

    val nFull = Input(Bits(1.W))
    val upperDin = Input(Bits((destcodeWidth+messageWidth).W))
    val lowerDin = Input(Bits((destcodeWidth+messageWidth).W))

    val upperEmpty = Input(Bits(1.W))
    val lowerEmpty = Input(Bits(1.W))

    val upperRe = Output(Bits(1.W))
    val lowerRe = Output(Bits(1.W))

    val validOut = Output(Bits(1.W))
    val destcodeOut = Output(Bits(destcodeWidth.W))
    val messageOut = Output(Bits(messageWidth.W))

  })


  val priorityReg = RegInit(1.U(1.W))
  val validReg = RegInit(0.U(1.W))
  val upperPortReadReg = Reg(Bits(1.W))

  val validOutReg = RegInit(0.U(1.W))

  val re = Wire(Bool())

  val upperReWire = Wire(UInt(1.W))
  val lowerReWire = Wire(UInt(1.W))

  when(io.upperEmpty === 0.B & io.lowerEmpty === 0.B & io.nFull === 1.B){
    priorityReg := !priorityReg
  }

  upperPortReadReg := upperReWire

  upperReWire := io.nFull & !io.upperEmpty & (!priorityReg | io.lowerEmpty)
  lowerReWire := io.nFull & !io.lowerEmpty & (priorityReg | io.upperEmpty)

  re := upperReWire | lowerReWire

  io.destcodeOut := Mux(io.nFull.asBool(), Mux(upperPortReadReg.asBool(), io.upperDin(destcodeWidth + messageWidth - 1, messageWidth), io.lowerDin(destcodeWidth + messageWidth - 1, messageWidth)), 0.U)
  io.messageOut := Mux(io.nFull.asBool(), Mux(upperPortReadReg.asBool(), io.upperDin(messageWidth - 1, 0), io.lowerDin(messageWidth - 1, 0)), 0.U)

  validOutReg := re
  io.validOut := validOutReg

  io.upperRe := upperReWire
  io.lowerRe := lowerReWire
  
}

object Selector extends App {
  new (chisel3.stage.ChiselStage).execute(Array(), Seq(ChiselGeneratorAnnotation(() => new Selector())))
}