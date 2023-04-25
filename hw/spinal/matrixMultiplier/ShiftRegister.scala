package matrixMultiplier

import spinal.core._

import scala.language.postfixOps

case class ShiftRegisterConfig(
  val dataWidth: Int = 32,
  val dataDepth: Int = 16
)

case class ShiftRegister(config: ShiftRegisterConfig) extends Component {
  val io = new Bundle {
    val din = in UInt (config.dataWidth bits)
    val dout = out UInt (config.dataWidth bits)
  }.setName("")

  val regs = Array.fill(config.dataDepth)(Reg(UInt(config.dataWidth bits)) init 0)

  if (config.dataDepth > 0) {
    for (i <- 1 until config.dataDepth) regs(i) := regs(i - 1)
    regs(0) := io.din
    io.dout := regs(config.dataDepth - 1)
  } else {
    io.dout := io.din
  }
}
