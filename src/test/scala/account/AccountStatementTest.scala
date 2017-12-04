package account

import org.scalatest.{FeatureSpec, Matchers}
import instances.AccumulatorInstances._
import StatementSummary._


class AccountStatementTest extends FeatureSpec with Matchers {

  feature("Bank Account Statement generation test") {
    scenario("Income type should add credit to the account") {
      //Given
      val incomeRevenue = Transactions(List(Income(3000, "Salary for December 2017")))
      val lastStatementBalnace = 4000
      //when
      val newBalance = generateStatementSummary(lastStatementBalnace, incomeRevenue)
      //then
      newBalance shouldBe 7000
    }
    scenario("Expense type should deduct credit to the account") {
      //Given
      val incomeRevenue = Transactions(List(Expenses(40, "Telephone bill")))
      val lastStatementBalnace = 4000
      //when
      val newBalance = generateStatementSummary(lastStatementBalnace, incomeRevenue)
      //then
      newBalance shouldBe 3960
    }
    scenario("WithHeld type should not change the balance") {
      //Given
      val incomeRevenue = Transactions(List(WithHeld(40, "transaction withheld for problems")))
      val lastStatementBalnace = 4000
      //when
      val newBalance = generateStatementSummary(lastStatementBalnace, incomeRevenue)
      //then
      newBalance shouldBe 4000
    }

  }

}
