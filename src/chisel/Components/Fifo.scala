import chisel3._
import chisel3.stage._
import chisel3.util._

class Fifo(N: Int = 16, width: Int = 32) extends Module{

  val io = IO(new Bundle{
    val din = Input(Bits(width.W))
    val dout = Output(Bits(width.W))
    val wr_en = Input(Bits(1.W))
    val rd_en = Input(Bits(1.W))
    val empty = Output(Bits(1.W))
    val full = Output(Bits(1.W))
  })

  val nb_valuesReg = RegInit(0.U(log2Ceil(N+1).W))
  val read_addReg = RegInit(0.U(log2Ceil(N+1).W))
  val write_addReg = RegInit(0.U(log2Ceil(N+1).W))

  // Ne pas reset la memoire (seulement l'idx)
  //  val memoryReg = RegInit(VecInit(Seq.fill(N)(0.U(width.W))))
  //  val memoryReg = Reg(Vec(N, Bits(width.W)));
  val memoryReg = Mem(N, Bits(width.W));
  val doutBuffer = RegInit(0.U(width.W))

  when(io.wr_en === 1.B & io.rd_en === 0.B & (nb_valuesReg < N.U)){
    nb_valuesReg := nb_valuesReg + 1.U
  }.elsewhen(io.wr_en === 0.B & io.rd_en === 1.B & nb_valuesReg > 0.U){
    nb_valuesReg := nb_valuesReg - 1.U
  }

  when(io.wr_en === 1.U & ((nb_valuesReg < N.U)|(nb_valuesReg === N.U & io.rd_en === 1.B))){
    when(write_addReg === (N-1).U){
      write_addReg := 0.U
    }.otherwise{
      write_addReg := write_addReg + 1.U
    }
    memoryReg.write(write_addReg, io.din)
  }

  when(io.rd_en === 1.B & nb_valuesReg > 0.U){
    when(read_addReg === (N-1).U){
      read_addReg := 0.U
    }.otherwise{
      read_addReg := read_addReg + 1.U
    }
    // io.dout := memoryReg(read_addReg)
    doutBuffer := memoryReg.read(read_addReg)
  }.otherwise{
    // io.dout := 0.U
    doutBuffer := 0.U
  }
  io.dout := doutBuffer
  io.full := (nb_valuesReg === N.U)
  io.empty := (nb_valuesReg === 0.U)
}

object Fifo extends App {
  new (chisel3.stage.ChiselStage).execute(Array(), Seq(ChiselGeneratorAnnotation(() => new Fifo())))
}