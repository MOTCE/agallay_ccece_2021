import chisel3._
import chisel3.iotesters._

class ForkTester(DUT: Fork) extends PeekPokeTester(DUT){

  println("------------------")

  val message = 255.U(8.W)
  poke(DUT.io.messageIn, message)
  println("Message en sortie avec 255 en entrée: " + peek(DUT.io.messageOutUpper).toString + ". Message attendu: " + message.toString)
  if(expect(DUT.io.messageOutUpper, message)){
    println("SUCCÈS")
  }else{
    println("ERREUR")
  }

  println("------------------")

  val destcode_01 = 15.U(8.W)
  poke(DUT.io.destcodeIn, destcode_01)
  println("Destcode en sortie upper: " + peek(DUT.io.destcodeOutUpper).toString + ". Attendu: " + destcode_01(3, 0).toString)
  println("Destcode en sortie lower: " + peek(DUT.io.destcodeOutLower).toString + ". Attendu: " + destcode_01(7, 4).toString)

  if(expect(DUT.io.destcodeOutUpper, destcode_01(3, 0)) && expect(DUT.io.destcodeOutLower, destcode_01(7, 4))){
    println("SUCCÈS")
  }else{
    println("ERREUR")
  }

  println("------------------")

  poke(DUT.io.validIn, 1.B)
  println("Valid en sortie upper: " + peek(DUT.io.validOutUpper).toString + ". Attendu: " + 1)
  println("Valid en sortie lower: " + peek(DUT.io.validOutLower).toString + ". Attendu: " + 0)

  if(expect(DUT.io.validOutUpper, 1.B) && expect(DUT.io.validOutLower, 0.B)){
    println("SUCCÈS")
  }else{
    println("ERREUR")
  }

  println("------------------")

}

object ForkTester extends App {
  // Fork avec messageW = 8 et destcodeW = 8 par défaut, cf main/scala/Fork.scala
  chisel3.iotesters.Driver(() => new Fork()){
    DUT => new ForkTester(DUT)
  }
}