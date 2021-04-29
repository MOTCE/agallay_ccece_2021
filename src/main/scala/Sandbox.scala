import chisel3._
import chisel3.stage.ChiselGeneratorAnnotation
import chisel3.util._

class Sandbox extends Module{
  val io = IO(new Bundle{
    val in0 = Input(Bits(4.W))
    val in1 = Input(Bits(4.W))
    val dout = Output(Bits(8.W))
  })

  io.dout := io.in0

}

object Sandbox extends App {
  new (chisel3.stage.ChiselStage).execute(Array(), Seq(ChiselGeneratorAnnotation(() => new Sandbox())))
}
