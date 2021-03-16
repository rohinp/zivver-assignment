package com.bulkread

trait Monoid[A] {
  def zero:A
  def combine(a:A,b:A):A
}

object Monoid:
  extension [A:Monoid] (a:A) def |+|(b:A):A = summon[Monoid[A]].combine(a,b)
  def zero[A:Monoid]:A = summon[Monoid[A]].zero