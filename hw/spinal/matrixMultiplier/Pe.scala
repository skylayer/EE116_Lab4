package matrixMultiplier

import spinal.core._

import scala.language.postfixOps

case class PeConfig(dataWidth: Int = 32)

case class Pe(config: PeConfig) extends Component {
  val io = new Bundle {
    val w = in UInt (config.dataWidth bits)
    val xIn = in UInt (config.dataWidth bits)
    val yIn = in UInt (config.dataWidth bits)
    val xOut = out UInt (config.dataWidth bits)
    val yOut = out UInt (config.dataWidth bits)
  }.setName("")

  val xReg = Reg(UInt(config.dataWidth bits)) init (0)
  val yReg = Reg(UInt(config.dataWidth bits)) init (0)

  xReg := io.xIn
  yReg := (io.xIn * io.w + io.yIn).resize(config.dataWidth bits)

  io.xOut := xReg
  io.yOut := yReg
}

object PeVerilog {
  def main(args: Array[String]): Unit = {
    Config.spinal.generateVerilog(Pe(PeConfig()))
  }
}
