import chisel3._
import chisel3.iotesters._
import firrtl.CommonOptions

class SwitchTester(DUT: Switch) extends PeekPokeTester(DUT){
  poke(DUT.io.nFullInUpper, 1.B)
  poke(DUT.io.nFullInLower, 1.B)

  poke(DUT.io.message0, "b00000001".U)
  poke(DUT.io.destcode0, 255.U)
  poke(DUT.io.valid0, 1.B)

  poke(DUT.io.message1, "b10000000".U)
  poke(DUT.io.destcode1, 255.U)
  poke(DUT.io.valid1, 1.B)

  step(10)
}

object SwitchTester extends App {
  chisel3.iotesters.Driver(() => new Switch()){
    DUT => new SwitchTester(DUT)
  }
}

object SwitchTesterWave extends App {
  chisel3.iotesters.Driver.execute(Array("--generate-vcd-output", "on", "--target-dir", "./switch_dir"),
    () => new Switch())(DUT => new SwitchTester(DUT))
}