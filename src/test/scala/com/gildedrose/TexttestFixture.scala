package com.gildedrose

object TexttestFixture {
  def main(args: Array[String]): Unit = {
    val items = Array[Item](
      new Item("+5 Dexterity Vest", 10, 20),
      new Item("Aged Brie", 2, 0),
      new Item("Elixir of the Mongoose", 5, 7),
      new Item("Sulfuras, Hand of Ragnaros", 0, 80),
      new Item("Sulfuras, Hand of Ragnaros", -1, 80),
      new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
      new Item("Backstage passes to a TAFKAL80ETC concert", 10, 49),
      new Item("Backstage passes to a TAFKAL80ETC concert", 5, 49),
      // this conjured item does not work properly yet
      new Item("Conjured Mana Cake", 3, 6)
    )
    val app = new GildedRose(items)
    val days = if (args.length > 0) args(0).toInt + 1 else 2
    
    def loop(daysLeft:Int, newItems:Array[Item]):Unit = {
      if(daysLeft >= days) ()
      else {
        println("-------- day " + daysLeft + " --------")
        println("name, sellIn, quality")  
        newItems.foreach{item => println(item.name + ", " + item.sellIn + ", " + item.quality)}  
        loop(daysLeft + 1, new GildedRose(items).updateQuality())
      }
    }
    loop(0,items)
  }
}
