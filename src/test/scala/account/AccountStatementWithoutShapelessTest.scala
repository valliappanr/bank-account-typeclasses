package account

import org.scalatest.{FeatureSpec, Matchers}

import BankAccountWithoutShapeless._

class AccountStatementWithoutShapelessTest extends FeatureSpec with Matchers {

  feature("Account statement using pattern matching") {
    scenario("adding income txn to old balance should add credit to balance") {
      //Given
      val txns = Transactions(List(Income(40, "interest")))
      val oldBalance = 4000
      //when
      val newBalance = generateSummary(oldBalance, txns)
      //then
      newBalance shouldBe 4040
    }
    scenario("adding expenses txn to old balance should deduct value to old balance") {
      //Given
      val txns = Transactions(List(Expenses(40, "electricity")))
      val oldBalance = 4000
      //when
      val newBalance = generateSummary(oldBalance, txns)
      //then
      newBalance shouldBe 3960
    }

  }
}
