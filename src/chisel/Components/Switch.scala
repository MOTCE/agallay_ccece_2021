import chisel3._
import chisel3.stage.ChiselGeneratorAnnotation
import chisel3.util._

// N correspond à la "profondeur" des FIFOs

class Switch(messageW : Int = 8, destcodeW : Int = 8, N : Int = 32) extends Module{
  val io = IO(new Bundle{

    val message0 = Input(Bits(messageW.W))
    val message1 = Input(Bits(messageW.W))

    val destcode0 = Input(Bits(destcodeW.W))
    val destcode1 = Input(Bits(destcodeW.W))

    val valid0 = Input(Bits(1.W))
    val valid1 = Input(Bits(1.W))

    val nFullInUpper = Input(Bits(1.W))
    val nFullInLower = Input(Bits(1.W))

    val messageUpperOut = Output(Bits(messageW.W))
    val messageLowerOut = Output(Bits(messageW.W))

    val destcodeUpperOut = Output(Bits(destcodeW.W))
    val destcodeLowerOut = Output(Bits(destcodeW.W))

    val validUpperOut = Output(Bits(1.W))
    val validLowerOut = Output(Bits(1.W))

    val nFullOutUpper = Output(Bits(1.W))
    val nFullOutLower = Output(Bits(1.W))

  })

  val forkUpper = Module(new Fork(messageW, destcodeW))
  val forkLower = Module(new Fork(messageW, destcodeW))

  val fifoUpper0 = Module(new Fifo(N, messageW+destcodeW))
  val fifoUpper1 = Module(new Fifo(N, messageW+destcodeW))

  val fifoLower0 = Module(new Fifo(N, messageW+destcodeW))
  val fifoLower1 = Module(new Fifo(N, messageW+destcodeW))

  val selectorUpper = Module(new Selector(destcodeW, messageW))
  val selectorLower = Module(new Selector(destcodeW, messageW))

  io.nFullOutUpper := !(fifoUpper0.io.full | fifoUpper1.io.full)
  io.nFullOutLower := !(fifoLower0.io.full | fifoLower1.io.full)

  forkUpper.io.messageIn := io.message0
  forkUpper.io.destcodeIn := io.destcode0
  forkUpper.io.validIn := io.valid0

  forkLower.io.messageIn := io.message1
  forkLower.io.destcodeIn := io.destcode1
  forkLower.io.validIn := io.valid1

  fifoUpper0.io.din := Cat(forkUpper.io.destcodeOutUpper, forkUpper.io.messageOutUpper)
  fifoUpper0.io.wr_en := forkUpper.io.validOutUpper
  fifoUpper0.io.rd_en := selectorUpper.io.upperRe

  fifoUpper1.io.din := Cat(forkUpper.io.destcodeOutLower, forkUpper.io.messageOutLower)
  fifoUpper1.io.wr_en := forkUpper.io.validOutLower
  fifoUpper1.io.rd_en := selectorLower.io.upperRe

  fifoLower0.io.din := Cat(forkLower.io.destcodeOutUpper, forkLower.io.messageOutUpper)
  fifoLower0.io.wr_en := forkLower.io.validOutUpper
  fifoLower0.io.rd_en := selectorUpper.io.lowerRe

  fifoLower1.io.din := Cat(forkLower.io.destcodeOutLower, forkLower.io.messageOutLower)
  fifoLower1.io.wr_en := forkLower.io.validOutLower
  fifoLower1.io.rd_en := selectorLower.io.lowerRe

  selectorUpper.io.upperDin := fifoUpper0.io.dout
  selectorUpper.io.upperEmpty := fifoUpper0.io.empty
  selectorUpper.io.lowerDin := fifoLower0.io.dout
  selectorUpper.io.lowerEmpty := fifoLower0.io.empty
  selectorUpper.io.nFull := io.nFullInUpper

  io.messageUpperOut := selectorUpper.io.messageOut
  io.destcodeUpperOut := selectorUpper.io.destcodeOut
  io.validUpperOut := selectorUpper.io.validOut

  selectorLower.io.upperDin := fifoUpper1.io.dout
  selectorLower.io.upperEmpty := fifoUpper1.io.empty
  selectorLower.io.lowerDin := fifoLower1.io.dout
  selectorLower.io.lowerEmpty := fifoLower1.io.empty
  selectorLower.io.nFull := io.nFullInLower

  io.messageLowerOut := selectorLower.io.messageOut
  io.destcodeLowerOut := selectorLower.io.destcodeOut
  io.validLowerOut := selectorLower.io.validOut

}

object Switch extends App {
  new (chisel3.stage.ChiselStage).execute(Array(), Seq(ChiselGeneratorAnnotation(() => new Switch())))
}
