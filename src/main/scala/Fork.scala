import chisel3._
import chisel3.util._
import chisel3.stage.ChiselGeneratorAnnotation

class Fork(messageW: Int = 8, destcodeW: Int = 8) extends Module{

  val io = IO(new Bundle {
    val messageIn = Input(Bits(messageW.W))
    val destcodeIn = Input(Bits(destcodeW.W))
    val validIn = Input(Bits(1.W))

    val validOutUpper = Output(Bits(1.W))
    val validOutLower = Output(Bits(1.W))

    val messageOutUpper = Output(Bits(messageW.W))
    val messageOutLower = Output(Bits(messageW.W))

    val destcodeOutUpper = Output(Bits((destcodeW/2).W))
    val destcodeOutLower = Output(Bits((destcodeW/2).W))

  })

  io.validOutUpper := io.validIn & io.destcodeIn(destcodeW/2 - 1, 0).orR
  io.validOutLower := io.validIn & io.destcodeIn(destcodeW - 1, destcodeW/2).orR

  io.messageOutUpper := io.messageIn
  io.messageOutLower := io.messageIn

  io.destcodeOutUpper := io.destcodeIn(destcodeW/2 - 1, 0)
  io.destcodeOutLower := io.destcodeIn(destcodeW - 1, destcodeW/2)

}

object Fork extends App {
  new (chisel3.stage.ChiselStage).execute(Array(), Seq(ChiselGeneratorAnnotation(() => new Fork())))
}