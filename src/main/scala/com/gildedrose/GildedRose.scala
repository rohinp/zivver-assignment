package com.gildedrose
import util.chaining.scalaUtilChainingOps
import Item._
import ItemState._

type SellInAndQuality = (Int,Int)

//This is like a streatygy pattern implementation or just a function :-)
sealed trait ItemUpdateStrategy extends (Item => Item)

case object OrdinaryItem extends ItemUpdateStrategy:
  def apply(item:Item):Item = 
    decrSellIn(item)
      .pipe(i => if(i.sellIn >= 0 ) decrQuality(i) else (decrQuality compose decrQuality)(i))

case object QualityIncrementerItem extends ItemUpdateStrategy:
  def apply(item:Item):Item = decrSellIn(item).pipe(incrQuality)

case object NeverSoldItem extends ItemUpdateStrategy:
  def apply(item:Item):Item = item

case object BackstagePassesItem extends ItemUpdateStrategy:
  def apply(item:Item):Item =
    decrSellIn(item)
      .pipe(i => if(i.sellIn > 10) incrQuality(i) else i)
      .pipe(i => if(i.sellIn > 5 && i.sellIn <= 10) (incrQuality compose incrQuality)(i) else i)
      .pipe(i => if(i.sellIn > 0 && i.sellIn <= 5) (incrQuality compose incrQuality compose incrQuality)(i) else i)
      .pipe(i => if(i.sellIn <= 0) resetQuality(i) else i)

case object ConjuredItems extends ItemUpdateStrategy:
  def apply(item:Item):Item = 
    decrSellIn(item)
    .pipe(decrQuality)
    .pipe(decrQuality)

def selectStrategy(item:Item):ItemUpdateStrategy =
  Map(
    "Aged Brie" -> QualityIncrementerItem,
    "Backstage passes to a TAFKAL80ETC concert" -> BackstagePassesItem,
    "Sulfuras, Hand of Ragnaros" -> NeverSoldItem,
    "Conjured Mana Cake" -> ConjuredItems
  ).get(item.name)
    .fold(OrdinaryItem)(identity)


class GildedRose(val items: Array[Item]):

  def updateQuality():Array[Item] = 
    for
      i <- items
      s <- Array(selectStrategy(i))
    yield s(i)

  def updateQualityV2():Array[Item] = 
    for
      i <- items
    yield mainCombinator.run(i).fold(i)(_._2)
