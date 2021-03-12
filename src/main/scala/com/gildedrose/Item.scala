package com.gildedrose
import util.chaining.scalaUtilChainingOps
/**
 * This is something I would really like to change 
 * 
 * 1. Would try never use var in my code
 * 2. Would generally model domain/state using ADT 
 * 
 * As the assignment does not allows to will keep it as it is
 */
class Item(val name: String, var sellIn: Int, var quality: Int){}

/**
 * Companion object containing smart constructors or builders
 */
object Item:
  def decrSellIn:Item => Item = x => 
    new Item(x.name, x.sellIn - 1, x.quality) 
  def incrQuality:Item => Item = x => 
    new Item(x.name, x.sellIn, if(x.quality >= 50) 50 else x.quality + 1)
  def decrQuality:Item => Item = x => 
    new Item(x.name, x.sellIn, if(x.quality <= 0) 0 else x.quality - 1)
  def resetQuality:Item => Item = x => 
    new Item(x.name, x.sellIn, 0) 
