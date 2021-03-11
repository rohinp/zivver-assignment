package com.gildedrose

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GildedRoseTest  extends AnyFlatSpec with Matchers {
      it should "not allow quality more than 50" in {
        val items = Array[Item](new Item("foo", 1, 100))
        val app = new GildedRose(items)
        val result = app.updateQuality()
        result(0).name should equal ("foo")
        result(0).sellIn should equal(0)
        //no validations on input, it must fail, quality cant be more than 50
        result(0).quality should equal(99)
      }

      it should "lower sell and quality by one at EOD" in {
        val items = Array[Item](new Item("foo", 1, 10))
        val app = new GildedRose(items)
        val result = app.updateQuality()
        result(0).name should equal ("foo")
        result(0).sellIn should equal(0)
        result(0).quality should equal(9)
      }

      it should "Sulfuras with invalid values" in {
        val items = Array[Item](new Item("Sulfuras, Hand of Ragnaros", 0, 0))
        val app = new GildedRose(items)
        val result = app.updateQuality()
        result(0).name should equal ("Sulfuras, Hand of Ragnaros")
        result(0).sellIn should equal(0)
        result(0).quality should equal(0)
      }

      it should "not sell of dec quality of Sulfuras" in {
        val items = Array[Item](new Item("Sulfuras, Hand of Ragnaros", 1, 10))
        val app = new GildedRose(items)
        val result = app.updateQuality()
        result(0).name should equal ("Sulfuras, Hand of Ragnaros")
        result(0).sellIn should equal(1)
        result(0).quality should equal(10)
      }

      it should "degrade quality twice once sell date passed" in {
        val items = Array[Item](new Item("foo", 0, 10))
        val app = new GildedRose(items)
        val result = app.updateQuality()
        result(0).name should equal ("foo")

        result(0).quality should equal(8)
        result(0).sellIn should equal(-1)
      }

      it should "increate quality for Aged Brie" in {
        val items = Array[Item](new Item("Aged Brie", 8, 10))
        val app = new GildedRose(items)
        val result = app.updateQuality()
        result(0).name should equal ("Aged Brie")

        result(0).sellIn should equal(7)
        result(0).quality should equal(11)
      }

      it should "increate quality twice for backstage passes; <= 10 days" in {
        val items = Array[Item](new Item("Backstage passes to a TAFKAL80ETC concert", 8, 10))
        val app = new GildedRose(items)
        val result = app.updateQuality()
        result(0).name should equal ("Backstage passes to a TAFKAL80ETC concert")

        result(0).sellIn should equal(7)
        result(0).quality should equal(12)
      }

      it should "increate quality three for backstage passes; <= 5 days" in {
        val items = Array[Item](new Item("Backstage passes to a TAFKAL80ETC concert", 4, 10))
        val app = new GildedRose(items)
        val result = app.updateQuality()
        result(0).name should equal ("Backstage passes to a TAFKAL80ETC concert")

        result(0).sellIn should equal(3)
        result(0).quality should equal(13)
      }

      it should "zero quality for backstage passes; == 0 days" in {
        val items = Array[Item](new Item("Backstage passes to a TAFKAL80ETC concert", 0, 10))
        val app = new GildedRose(items)
        val result = app.updateQuality()
        result(0).name should equal ("Backstage passes to a TAFKAL80ETC concert")

        result(0).quality should equal(0)
        result(0).sellIn should equal(-1)
      }

      it should "zero quality for backstage passes; == -1 days" in {
        val items = Array[Item](new Item("Backstage passes to a TAFKAL80ETC concert", -1, 10))
        val app = new GildedRose(items)
        val result = app.updateQuality()
        result(0).name should equal ("Backstage passes to a TAFKAL80ETC concert")

        result(0).quality should equal(0)
        result(0).sellIn should equal(-2)
      }

      it should "degrade quality twice for Conjured items" in {
        val items = Array[Item](new Item("Conjured Mana Cake", 5, 10))
        val app = new GildedRose(items)
        val result = app.updateQuality()
        result(0).name should equal ("Conjured Mana Cake")

        result(0).sellIn should equal(4)
        //new feature not avialable
        result(0).quality should equal(8)
      }
}