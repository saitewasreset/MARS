# MARS for BUAA Compiler Technology Course

原仓库地址：<https://github.com/dpetersanderson/MARS>

Java 版本要求：>= 17

新增指令数统计、FinalCycles计算功能，并修改 CLI 选项为现代 UNIX 风格，使用 Maven 构建系统进行构建，生成可独立运行的 JAR 文件。

## 指令统计

既可通过 GUI 直观查看指令统计信息，也可 CLI 运行并将指令信息写入 JSON 文件中，便于自动化分析和统计。

- 通过 GUI 查看：Tools --> Instruction Statistics，点击“Connect to MIPS”，运行汇编代码即可查看相关统计信息。
- 通过 CLI 输出：使用 `--instruction-statistics=FILE` 选项。若汇编代码无误，运行成功，则将 JSON 格式的统计信息写入 `FILE`
  ，否则不创建 `FILE`文件。JSON 的模式详见：
  `io.github.dpetersanderson.mars.CliInstructionStatisticsListener.StatisticsResult`。

各种指令的权重定义在 `io.github.dpetersanderson.mars.InstructionCategory`。

## 命令行选项

```text
MARS 2026-BUAA  Copyright 2003-2014 Pete Sanderson and Kenneth Vollmar

Usage:
Mars [-abdhpV] [--delayed-branching] [--instruction-count]
     [--messages-to-stderr] [--no-copyright] [--no-pseudo]
     [--self-modifying-code] [--start-at-main] [--warnings-are-errors]
     [--assemble-error-exit-code=CODE] [--display-format=FORMAT]
     [--instruction-statistics=FILE] [--max-steps=COUNT]
     [--memory-configuration=NAME] [--simulate-error-exit-code=CODE]
     [--memory=START-END]... [--register=REGISTER]... [--dump=SEGMENT_OR_RANGE 
     FORMAT FILE SEGMENT_OR_RANGE FORMAT FILE SEGMENT_OR_RANGE FORMAT FILE]...
     [FILE...]

Description:
Assemble and run MIPS programs. With no arguments, MARS starts the GUI.

Parameters:
      [FILE...]              Assembly source files.

Options:
  -a, --assemble-only        Assemble only; do not simulate.
      --assemble-error-exit-code=CODE
                             Exit with CODE if an assembly error occurs.
      --simulate-error-exit-code=CODE
                             Exit with CODE if a simulation error occurs.
      --display-format=FORMAT
                             Display memory and registers as hex, dec, or
                               ascii. Default: hex.
  -b, --brief                Do not display register names or memory addresses
                               with values.
  -d, --debug                Display MARS debugging statements.
      --delayed-branching    Enable MIPS delayed branching.
      --dump=SEGMENT_OR_RANGE FORMAT FILE SEGMENT_OR_RANGE FORMAT FILE
        SEGMENT_OR_RANGE FORMAT FILE
                             Dump memory to a file. May be repeated.
      --memory-configuration=NAME
                             Use memory configuration Default,
                               CompactDataAtZero, or CompactTextAtZero.
      --messages-to-stderr   Write MARS messages to standard error.
      --no-copyright         Do not display the copyright notice.
      --no-pseudo            Do not allow pseudo-instructions or extended
                               formats.
  -p, --project              Assemble all assembly files in the first input
                               file's directory.
      --start-at-main        Start execution at global label main, if defined.
      --self-modifying-code  Allow writes and branches to text or data segments.
      --warnings-are-errors  Treat assembler warnings as errors.
      --instruction-count    Display the count of executed basic instructions.
      --instruction-statistics=FILE
                             Write instruction count and weighted cycles by
                               category to a JSON file (default:
                               instruction_statistics.json).
      --max-steps=COUNT      Maximum simulation step count. Non-positive values
                               mean no maximum.
      --register=REGISTER    Display a register after simulation. May be
                               repeated.
      --memory=START-END     Display a memory range after simulation. May be
                               repeated.
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
```