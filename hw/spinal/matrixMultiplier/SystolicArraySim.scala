package matrixMultiplier

import spinal.core._
import spinal.core.sim._

import scala.language.postfixOps


object SystolicArraySim extends App {
  private val systolicArrayConfig = SystolicArrayConfig(
    xCols = 100, wCols = 16, wRows = 16
  )

  Config.sim.compile(SystolicArray(systolicArrayConfig)).doSim { dut =>

    // Generate the x matrix
    println("<------------------ x matrix ------------------>")
    val xMat = Array.fill(systolicArrayConfig.wCols, systolicArrayConfig.xCols)(scala.util.Random.nextInt(0xff))
    for (rowId <- 0 until systolicArrayConfig.wCols) {
      for (colId <- 0 until systolicArrayConfig.xCols) {
        printf("%02X ", xMat(rowId)(colId))
      }
      println()
    }

    println()

    // Generate the w matrix
    println("<------------------ w matrix ------------------>")
    val wMat = Array.fill(systolicArrayConfig.wRows, systolicArrayConfig.wCols)(scala.util.Random.nextInt(0xff))
    for (rowId <- 0 until systolicArrayConfig.wRows) {
      for (colId <- 0 until systolicArrayConfig.wCols) {
        printf("%02X ", wMat(rowId)(colId))
      }
      println()
    }

    // Generate the y matrix
    println("<------------------ y matrix ------------------>")
    val yMat = Array.fill(systolicArrayConfig.wRows, systolicArrayConfig.xCols)(0)
    for (rowId <- 0 until systolicArrayConfig.wRows) {
      for (colId <- 0 until systolicArrayConfig.xCols) {
        for (k <- 0 until systolicArrayConfig.wCols) {
          yMat(rowId)(colId) += xMat(k)(colId) * wMat(rowId)(k)
        }
        printf("%05X ", yMat(rowId)(colId))
      }
      println()
    }

    // Serialize the w matrix
    var wMatSerialized = BigInt(0)
    for (rowId <- 0 until systolicArrayConfig.wRows) {
      for (colId <- 0 until systolicArrayConfig.wCols) {
        wMatSerialized |= BigInt(wMat(rowId)(colId)) << ((rowId * systolicArrayConfig.wCols + colId) * systolicArrayConfig.dataWidth)
      }
    }
    dut.io.W #= wMatSerialized

    // Fork a process to generate the reset and the clock on the dut
    val clockDomain = ClockDomain(dut.io.clk, dut.io.rst)
    clockDomain.forkStimulus(period = 10, sleepDuration = 50)
    clockDomain.forkSimSpeedPrinter()

    clockDomain.waitRisingEdge()
    println("Start simulation at time: " + simTime())

    fork {
      for (colId <- 0 until systolicArrayConfig.xCols) {
        var xMatSerialized = BigInt(0)
        for (rowId <- 0 until systolicArrayConfig.wCols) {
          xMatSerialized |= BigInt(xMat(rowId)(colId)) << (rowId * systolicArrayConfig.dataWidth)
        }
        dut.io.X #= xMatSerialized

        clockDomain.waitRisingEdge()
      }
    }

    // Wait for the result
    waitUntil(dut.io.valid.toBoolean)
    println("Valid signal is high at time: " + simTime())
    var outputCounter = 0
    val resultMat = Array.fill(systolicArrayConfig.wRows, systolicArrayConfig.xCols)(0)
    while (outputCounter < systolicArrayConfig.xCols) {
      clockDomain.waitRisingEdge()

      for (rowId <- 0 until systolicArrayConfig.wRows) {
        resultMat(rowId)(outputCounter) = ((dut.io.Y.toBigInt >> (rowId * systolicArrayConfig.dataWidth)) & ((BigInt(1) << systolicArrayConfig.dataWidth) - 1)).toInt
      }

      outputCounter += 1
    }

    // Print the result
    println("<------------------ result matrix ------------------>")
    for (rowId <- 0 until systolicArrayConfig.wRows) {
      for (colId <- 0 until systolicArrayConfig.xCols) {
        printf("%05X ", resultMat(rowId)(colId))
      }
      println()
    }

    // Check the result
    for (rowId <- 0 until systolicArrayConfig.wRows) {
      for (colId <- 0 until systolicArrayConfig.xCols) {
        assert(resultMat(rowId)(colId) == yMat(rowId)(colId), "Simulation failed!")
      }
    }
    println("Simulation success!")
  }
}
