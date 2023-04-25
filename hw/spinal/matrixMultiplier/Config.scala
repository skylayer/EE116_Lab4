package matrixMultiplier

import spinal.core._
import spinal.core.sim._

object Config {
  def spinal = SpinalConfig(
    targetDirectory = "hw/gen",
    defaultConfigForClockDomains = ClockDomainConfig(
      clockEdge = RISING,
      resetKind = ASYNC,
      resetActiveLevel = HIGH
    ),
    onlyStdLogicVectorAtTopLevelIo = true,
    nameWhenByFile = false,
    genLineComments = true,
    bitVectorWidthMax = Int.MaxValue,
    verbose = true
  )

  def sim = SimConfig.withConfig(spinal).withFstWave
}
