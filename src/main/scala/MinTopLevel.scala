import chisel3._
import chisel3.stage.ChiselGeneratorAnnotation
import chisel3.util._

class MinTopLevel(M: Int, messageW: Int, N: Int) extends Module {
  val io = IO(new Bundle {
  })

  val multistageInterconnectionNetwork = Module(new MultistageInterconnectionNetwork(M, messageW, N));

  val IN_MESSAGE_REGS = for (i <- 0 until M) yield {
    val reg = RegInit(1.U(messageW.W))
    reg
  }
  val IN_DESTCODE_REGS = for (i <- 0 until M) yield {
    val reg = RegInit(0.U(M.W))
    reg
  }
  val IN_VALID_REGS = for (i <- 0 until M) yield {
    val reg = RegInit(1.U(1.W))
    reg
  }
  val IN_NFULL_REGS = for (i <- 0 until M) yield {
    val reg = RegInit(1.U(1.W))
    reg
  }
  val OUT_MESSAGE_REGS = for (i <- 0 until M) yield {
    val reg = RegInit(1.U(messageW.W))
    reg
  }
  val OUT_VALID_REGS = for (i <- 0 until M) yield {
    val reg = RegInit(1.U(1.W))
    reg
  }
  val OUT_NFULL_REGS = for (i <- 0 until M) yield {
    val reg = RegInit(1.U(1.W))
    reg
  }

  for(i <- 0 until M) yield {
    multistageInterconnectionNetwork.io.messageDin(i) := IN_MESSAGE_REGS(i)
    multistageInterconnectionNetwork.io.destcodeDin(i) := IN_DESTCODE_REGS(i)
    multistageInterconnectionNetwork.io.validDin(i) := IN_VALID_REGS(i)
    multistageInterconnectionNetwork.io.nFullDin(i) := IN_NFULL_REGS(i)

    OUT_MESSAGE_REGS(i) := multistageInterconnectionNetwork.io.messageDout(i)
    OUT_VALID_REGS(i) := multistageInterconnectionNetwork.io.validDout(i)
    OUT_NFULL_REGS(i) := multistageInterconnectionNetwork.io.nFullDout(i)
  }

}

object MinTopLevel extends App {
  new (chisel3.stage.ChiselStage).emitVerilog(
    new MinTopLevel(8, 8, 128),
    Array("--target-dir", "./min_toplevel_dir", "--compiler", "mverilog"))
}
