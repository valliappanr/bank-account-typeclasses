# bank-account-typeclasses
Bank Account management using scala type classes and Shapeless.

A simple Bank Account contains statements of type credit (Income)/ debit(expenses). The statement is generated from last balance by adding all credit transactions and subtracting all the debit transactions.
Many ways we can summarize the statements,

Simple Functional way to generate summary is,

```
sealed abstract class Transaction(val price: Int, description: String)
final case class Income(override val price: Int, description: String) extends Transaction(price, description)
final case class Expenses (override val price: Int, description: String) extends Transaction(price, description)
final case class Transactions(items: List[Transaction])

  def generateSummary(oldBalance: Int, txns: Transactions): Int = {
    var newBalance = oldBalance
    txns.items.foreach(item => {
      item match {
        case Income(price,_) => (newBalance += price)
        case Expenses(price, _) => (newBalance -= price)
      }
    })
    newBalance
  }
```

Problem with above approach is that for example if we want to add new transaction WithHeld (for example a fraudulent transaction 
