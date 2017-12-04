package account

import account.accumulate.Accumulator
import shapeless.{:+:, ::, CNil, Coproduct, Generic, HList, HNil, Inl, Inr, Lazy}

sealed abstract class StatementItem(val price: Int, description: String)
final case class Income(override val price: Int, description: String) extends StatementItem(price, description)
final case class Expenses (override val price: Int, description: String) extends StatementItem(price, description)
final case class WithHeld (override val price: Int, description: String) extends StatementItem(price, description)
final case class Statements(items: List[StatementItem])
final case class Account(s: Statements, name: String)

object StatementSummary {
  def generateStatementSummary[A](value: (Int, A))(implicit enc: Accumulator[A]): Int =
    enc.accumulate(value)

}
object BalanceSheet extends App {

  import instances.AccumulatorInstances._
  import StatementSummary._

  val statements = Statements(List(Income(3000, "Salary"), Expenses(25, "electricity"),
    Income(20, "water-bill"), WithHeld(4, "problem with transaction")))
  val account = Account(statements, "test")

  println(generateStatementSummary(1200, account.s))
}