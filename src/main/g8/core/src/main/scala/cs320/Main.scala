package cs320

import org.jline.reader.{LineReaderBuilder, EndOfFileException, UserInterruptException}
import org.jline.terminal.TerminalBuilder

import scala.Console.{MAGENTA => M, CYAN => C, RESET}

object Main {

  val name = "identifiers"

  def main(args: Array[String]): Unit = {
    val terminal = TerminalBuilder.builder.build()
    val reader = LineReaderBuilder.builder.terminal(terminal).build()
    def strs: LazyList[String] = (
      try {
        reader.readLine(s"\n$M$name>$RESET ")
      } catch {
        case _: EndOfFileException | _: UserInterruptException => ":q"
      }
    ) #:: strs

    println(s"Welcome to the $M$name$RESET REPL.")

    for (str <- strs.takeWhile(s => !eof(s)) if str.nonEmpty) {
      val opt = lift {
        val expr = Expr(str)
        println(s"  ${C}Parsed:$RESET $expr")
        expr
      }

      for (expr <- opt) {
        lift {
          val freeIds = Implementation.freeIds(expr)
          println(s"  ${C}freeIds:$RESET ${freeIds.mkString(", ")}")
        }

        lift {
          val bindingIds = Implementation.bindingIds(expr)
          println(s"  ${C}freeIds:$RESET ${bindingIds.mkString(", ")}")
        }

        lift {
          val boundIds = Implementation.boundIds(expr)
          println(s"  ${C}boundIds:$RESET ${boundIds.mkString(", ")}")
        }
      }
    }
  }

  def eof(str: String): Boolean = str == ":quit" || str == ":q"

  def lift[T](res: => T): Option[T] = try {
    Some(res)
  } catch {
    case e: Throwable =>
      e.printStackTrace()
      None
  }
}
