import chisel3._
import chisel3.iotesters._
import firrtl.CommonOptions

class SelectorTester(DUT: Selector) extends PeekPokeTester(DUT) {

  poke(DUT.io.upperEmpty, 0.B)
  poke(DUT.io.lowerEmpty, 0.B)
  poke(DUT.io.nFull, 1.B)
  step(1)
  poke(DUT.io.upperDin, 8.U)
  poke(DUT.io.lowerDin, 1.U)
  step(1)
  step(1)
  poke(DUT.io.nFull, 0.B)
  step(1)
}

object SelectorTester extends App {
  // Génération d'un Selector avec destcodeWidth, messageWidth = 8, cf main/scala/Selector.scala
  chisel3.iotesters.Driver(() => new Selector()){
    DUT => new SelectorTester(DUT)
  }
}

object SelectorTesterWave extends App {
  // Permet la génération d'un fichier VCD exploitable avec GTKWave
  chisel3.iotesters.Driver.execute(Array("--generate-vcd-output", "on", "--target-dir", "./selector_dir"), () => new Selector())(DUT => new SelectorTester(DUT))
}