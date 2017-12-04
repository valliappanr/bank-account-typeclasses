package account.instances

import account.{Expenses, Income, WithHeld}
import account.accumulate.Accumulator
import shapeless.{:+:, ::, CNil, Coproduct, Generic, HList, HNil, Inl, Inr, Lazy}


object AccumulatorInstances {
  def createAccumulator[A](func: ((Int, A)) => Int): Accumulator[A] =
    new Accumulator[A] {
      def accumulate(value: (Int, A)): Int = func(value)
    }
  implicit val hNilAccumulator = new Accumulator[HNil] {
    override def accumulate(input: (Int, HNil)) = {
      input._1
    }
  }
  implicit def hlistEncoder[H, T <: HList](
                                            implicit hEncoder: Lazy[Accumulator[H]],
                                            tEncoder: Accumulator[T]
                                          ): Accumulator[H :: T] = createAccumulator {
    case (value, h :: t) => {
      val result = hEncoder.value.accumulate((value, h))
      tEncoder.accumulate((result, t))
    }
  }

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

  implicit val  withHeldAccumulator = new Accumulator[WithHeld] {
    override def accumulate(input: (Int, WithHeld)) = {
      (input._1)
    }
  }

  implicit def genericEncoder[A, R](
                                     implicit
                                     gen: Generic.Aux[A, R],
                                     enc:   Lazy[Accumulator[R]]
                                   ): Accumulator[A] =
    createAccumulator ((a) => enc.value.accumulate((a._1, gen.to(a._2))))

  implicit val cnilEncoder: Accumulator[CNil] =
    createAccumulator(cnil => throw new Exception("Inconceivable!"))
  implicit def coproductEncoder[H, T <: Coproduct](
                                                    implicit
                                                    hEncoder: Accumulator[H],
                                                    tEncoder: Accumulator[T]): Accumulator[H :+: T] =
    createAccumulator {
      case (value, Inl(h)) => {
        hEncoder.accumulate((value, h))
      }
      case (value, Inr(t)) => {
        tEncoder.accumulate(value, t)
      }
    }
}