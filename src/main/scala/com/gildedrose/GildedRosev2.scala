package com.gildedrose

import util.chaining.scalaUtilChainingOps
import Item._

/**
 * Though the other version is good but selecting a stretegy from a Map 
 * It is not composable code, I'll attempt to do more composable code here.
 * 
 * We can make the code generic and make it work for other similar kind of situation
 * But for simplicity all types are based on Item
 * 
 * The solution is based on creating combinators
 * And in the end composing to create the final combinator
 * What I means is create simple small functions, compose to solve a bigger problem
 * here Combinators, because we need functions which return functions.
 * Composing these functions will return a new function :-)
 * 
 * This solution is more FP way to do things :-)
 * 
 * I liked this one :-)
 */
case class ItemState[A](run:Item => Option[(A,Item)]):
  import ItemState._
  def flatMap[B](f:A => ItemState[B]):ItemState[B] = ItemState(
    i => run(i).fold(None)((a, nextItem) => f(a).run(nextItem))
  )
  def map[B](f:A => B):ItemState[B] = this.flatMap(a => pure(f(a)))
  def or(other:ItemState[A]):ItemState[A] = ItemState(
    item => run(item).fold(other.run(item))(r => Some(r))
  )

object ItemState:
  //primitive combinators
  def zero[A]:ItemState[A] = ItemState(_ => None)
  def pure[A](a:A):ItemState[A] = ItemState{s => Some((a, s))}
  def get:ItemState[Item] = ItemState{s => Some((s, s))}
  def set(s:Item):ItemState[Unit] = ItemState(_ => Some(((), s)))
  def sat(pred:Item => Boolean, f:Item => Item):ItemState[Item] = 
    for
      item <- get
      _ <- if(pred(item)) set(f(item)) else zero
      newItem <- get
    yield newItem
  def nameSat(name:String, f:Item => Item):ItemState[Item] = sat(_.name == name, f)
  //This is similar to imlicit class in scala2
  extension [A](a:ItemState[A]) def |>(b:ItemState[A]):ItemState[A] = a.or(b)

  //derived combinators with business implementation
  def simple:ItemState[Item] =
    sat(_ => true, decrSellIn(_)
      .pipe(i => if(i.sellIn >= 0 ) decrQuality(i) else (decrQuality compose decrQuality)(i)))
  def qualityIncrementer:String => ItemState[Item] = nameSat(_, decrSellIn(_).pipe(incrQuality))
  def neverToSold:String => ItemState[Item] = nameSat(_, identity)
  def backstagePasses:String => ItemState[Item] = 
    nameSat(_, decrSellIn(_)
      .pipe(i => if(i.sellIn > 10) incrQuality(i) else i)
      .pipe(i => if(i.sellIn > 5 && i.sellIn <= 10) (incrQuality compose incrQuality)(i) else i)
      .pipe(i => if(i.sellIn > 0 && i.sellIn <= 5) (incrQuality compose incrQuality compose incrQuality)(i) else i)
      .pipe(i => if(i.sellIn <= 0) resetQuality(i) else i))
  def conjured:String => ItemState[Item] = 
    nameSat(_, decrSellIn(_)
      .pipe(decrQuality)
      .pipe(decrQuality))

  //below is where actually all combinators used
  //entry point or main combinator
  def mainCombinator:ItemState[Item] = 
    (qualityIncrementer("Aged Brie") |>
      backstagePasses("Backstage passes to a TAFKAL80ETC concert") |>
      neverToSold("Sulfuras, Hand of Ragnaros") |>
      conjured("Conjured Mana Cake") |>
      simple)

  