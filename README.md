# Bank-Account-Typeclasses

## Bank Account management using scala type classes and Shapeless.

A simple Bank Account contains statements of type credit (Income)/ debit(expenses). The statement is generated from last balance by adding all credit transactions and subtracting all the debit transactions.
Many ways we can summarize the statements,


### Simple Functional way to generate summary is,

```scala
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

### Issues with the above approach

Is that for example if we want to add new transaction WithHeld (for example a fraudulent transaction should not be included in the summary, then we need to modify the generateSummary function to include the new case to ignore the transaction as below,

```scala
final case class WithHeld (override val price: Int, description: String) extends Transaction(price, description)
  def generateSummary(oldBalance: Int, txns: Transactions): Int = {
    var newBalance = oldBalance
    txns.items.foreach(item => {
      item match {
        case Income(price,_) => (newBalance += price)
        case Expenses(price, _) => (newBalance -= price)
        case WithHeld(price,_) => (newBalance)
      }
    })
    newBalance
  }
```

## Type classes

Instead of calculating the summary in one method for all transaction type, if we add behaviour to each transaction type on how to change balance, then for new type it would just be adding the behaviour for the new type and using Scala's implicit and Shapeless HList and Coproduct, we can easily process for all the transactions.


For Type classes, three separate components glued together,
    
    1. Api represents functionality to be implemented for the types.
    2. Instances of the type classes for the types which we care about
    3. Interface exposed to the user.
    

### API:

In BankAccount statement generation, we need functionality to accumulate each type of transaction to the current balance.

   input (Int, T) - (old balance, and the Type for which the balance to be accumulated).

   ```scala
   trait Accumulator[T] {
     def accumulate(input: (Int, T)): (Int)
   }
   ``` 

### Instances we care about:
```scala
  implicit val  incomeAccumulator = new Accumulator[Income] {
    override def accumulate(input: (Int, Income)) = {
      (input._1 + input._2.price)
    }
  }

  implicit val  expenseAccumulator = new Accumulator[Expenses] {
    override def accumulate(input: (Int, Expenses)) = {
      (input._1 - input._2.price)
    }
  }

```

### Interface Exposed to the user:

```scala
  def generateStatementSummary[A](value: (Int, A))(implicit enc: Accumulator[A]): Int =
    enc.accumulate(value)
```


### Calculating summary

To calculate Summary, simply calling

```scala
  val statements = Transactions(List(Income(3000, "Salary")))
  val oldBalance = 4000
  val newBalance = generateStatementSummary(oldBalance, statements)
  //newBalance would be 7000
```

### New Transaction type

If we have to add new transaction type WithHeld, then simply

```scala
//Defining new Type
final case class WithHeld (override val price: Int, description: String) extends Transaction(price, description)

//And adding behaviour to new type

  implicit val  withHeldAccumulator = new Accumulator[WithHeld] {
    override def accumulate(input: (Int, WithHeld)) = {
      (input._1)
    }
  }
  

```

Would be enough to calculate the statement summary generation and we don't need to change the implementation of generateStatementSummary api.

And now 
```scala
  val statements = Transactions(List(Income(3000, "Salary"), WithHeld(400, "Fradulent transaction, withheld"))
  val oldBalance = 4000
  val newBalance = generateStatementSummary(oldBalance, statements)
  //newBalance would be 7000
```

would use the WithHeld behaviour using the newly defined implicits and return the same balance.


### Credits to the below links:

    1. https://github.com/milessabin/shapeless 
    2. http://gigiigig.github.io/tlp-step-by-step/dependent-types.html
    3. http://danielwestheide.com/blog/2013/02/06/the-neophytes-guide-to-scala-part-12-type-classes.html
    4. https://github.com/underscoreio/shapeless-guide
    
