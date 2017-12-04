package account.accumulate

trait Accumulator[T] {
  def accumulate(input: (Int, T)): (Int)
}
