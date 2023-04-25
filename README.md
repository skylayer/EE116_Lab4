# Systolic Array Matrix Multiplier

This repository contains the implementation of a Systolic Array Matrix Multiplier, written in SpinalHDL. The matrix multiplier leverages systolic array architecture to efficiently perform matrix multiplication.

## Features

- Configurable data width
- Configurable matrix dimensions (number of columns and rows)
- Uses systolic array architecture for efficient computation
- Input and output interfaces for matrix data
- Valid and Done signals for result validity and computation completion

## File Structure

- `MatrixMultiplier.scala`: Contains the main implementation of the Systolic Array Matrix Multiplier, ShiftRegister, and Processing Element (PE) classes.

## How to Use

1. Clone the repository.
2. Make sure you have installed SpinalHDL and its dependencies.
3. Modify the `SystolicArrayConfig`, `ShiftRegisterConfig`, and `PeConfig` classes in the `MatrixMultiplier.scala` file to customize the matrix multiplier's parameters.
4. To generate the Verilog code, run the `SystolicAarrayVerilog` object in the `MatrixMultiplier.scala` file.

## Example

The following example demonstrates how to use the Systolic Array Matrix Multiplier.

```scala
import spinal.core._
import matrixMultiplier._

object MySystolicArrayExample {
  def main(args: Array[String]): Unit = {
    val systolicArrayConfig = SystolicArrayConfig(
      xCols = 8,
      wCols = 8,
      wRows = 8,
      dataWidth = 32
    )

    val report = SpinalConfig.generateVerilog(SystolicArray(systolicArrayConfig))
    report.printPruned()
  }
}
```

## License
This project is licensed under the MIT License. See the LICENSE file for details.

## Contributing
Feel free to contribute by submitting pull requests or opening issues to discuss any potential improvements or fixes.

Please make sure your code follows the existing code style and provide detailed commit messages.

## Contact
For any questions or concerns, please open an issue on this repository.

## Disclaimer

Please note that this is a toy project and not intended for production use. Use at your own risk.
