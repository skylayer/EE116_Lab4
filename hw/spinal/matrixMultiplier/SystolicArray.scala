package matrixMultiplier

import spinal.core._

import scala.language.postfixOps

case class SystolicArrayConfig(
  xCols: Int = 8,
  wCols: Int = 8,
  wRows: Int = 8,
  dataWidth: Int = 32
)

case class SystolicArray(config: SystolicArrayConfig) extends Component {
  val io = new Bundle {
    val clk = in Bool()
    val rst = in Bool()
    val X = in Bits (config.dataWidth * config.wCols bits)
    val W = in Bits (config.dataWidth * config.wCols * config.wRows bits)
    val Y = out Bits (config.dataWidth * config.wRows bits)
    val valid = out Bool()
    val done = out Bool()
  }.setName("")

  val globalClockDomain = ClockDomain(
    clock = io.clk,
    reset = io.rst,
  )

  val computingArea = new ClockingArea(globalClockDomain) {
    val peArray = Array.fill(config.wRows, config.wCols)(new Pe(PeConfig(dataWidth = config.dataWidth)))

    for (rowId <- 0 until config.wRows) {
      for (colId <- 0 until config.wCols) {
        // Input
        peArray(rowId)(colId).io.w := io.W.subdivideIn(config.dataWidth bits)(rowId * config.wCols + colId).asUInt

        // Output
        if (rowId + 1 < config.wRows)
          peArray(rowId + 1)(colId).io.xIn := peArray(rowId)(colId).io.xOut
        if (colId + 1 < config.wCols)
          peArray(rowId)(colId + 1).io.yIn := peArray(rowId)(colId).io.yOut
      }
    }
  }

  val inputArea = new ClockingArea(globalClockDomain) {
    for (colId <- 0 until config.wCols) {
      val queue = ShiftRegister(ShiftRegisterConfig(config.dataWidth, colId))
      computingArea.peArray(0)(colId).io.xIn := queue.io.dout
      queue.io.din := io.X.subdivideIn(config.dataWidth bits)(colId).asUInt
    }
    for (rowId <- 0 until config.wRows)
      computingArea.peArray(rowId)(0).io.yIn := 0
  }


  val outputArea = new ClockingArea(globalClockDomain) {
    for (rowId <- 0 until config.wRows) {
      val queue = ShiftRegister(ShiftRegisterConfig(config.dataWidth, config.wRows - rowId - 1))
      io.Y.subdivideIn(config.dataWidth bits)(rowId) := queue.io.dout.asBits
      queue.io.din := computingArea.peArray(rowId)(config.wCols - 1).io.yOut
    }

    {
      val valid_queue = ShiftRegister(ShiftRegisterConfig(1, config.wRows + config.wCols))
      io.valid := valid_queue.io.dout.asBool
      valid_queue.io.din := 1
    }

    {
      val done_queue = ShiftRegister(ShiftRegisterConfig(1, config.xCols + config.wRows + config.wCols))
      io.done := done_queue.io.dout.asBool
      done_queue.io.din := 1
    }
  }
}

object SystolicAarrayVerilog {
  def main(args: Array[String]): Unit = {
    val report = Config.spinal.generateVerilog(SystolicArray(SystolicArrayConfig(16, 16, 16)))
    report.printPruned()
  }
}
