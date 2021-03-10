package com.gildedrose

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GildedRoseTest  extends AnyFlatSpec with Matchers {
      it should "not allow create an item with -1 sellIn and -1 quality" in {
        val items = Array[Item](new Item("foo", -1, -1))
        val app = new GildedRose(items)
        app.updateQuality()
        app.items(0).name should equal ("foo")
        //no validations on input
        app.items(0).sellIn should equal(-2)
        app.items(0).quality should equal(-1)
      }

      it should "not allow quality more than 50" in {
        val items = Array[Item](new Item("foo", 1, 100))
        val app = new GildedRose(items)
        app.updateQuality()
        app.items(0).name should equal ("foo")
        app.items(0).sellIn should equal(0)
        //no validations on input, it must fail
        app.items(0).quality should equal(99)
      }

      it should "lower sell and quality by one at EOD" in {
        val items = Array[Item](new Item("foo", 1, 10))
        val app = new GildedRose(items)
        app.updateQuality()
        app.items(0).name should equal ("foo")
        app.items(0).sellIn should equal(0)
        app.items(0).quality should equal(9)
      }

      it should "not sell of dec quality of Sulfuras" in {
        val items = Array[Item](new Item("Sulfuras", 1, 10))
        val app = new GildedRose(items)
        app.updateQuality()
        app.items(0).name should equal ("Sulfuras")
        //this is not a valid behavior it must fail
        app.items(0).sellIn should equal(0)
        app.items(0).quality should equal(9)
      }

      it should "degrade quality twice once sell date passed" in {
        val items = Array[Item](new Item("foo", 0, 10))
        val app = new GildedRose(items)
        app.updateQuality()
        app.items(0).name should equal ("foo")

        app.items(0).quality should equal(8)
        //this is not a valid behavior it must be 0
        app.items(0).sellIn should equal(-1)
      }

      it should "increate quality for Aged Brie" in {
        val items = Array[Item](new Item("Aged Brie", 8, 10))
        val app = new GildedRose(items)
        app.updateQuality()
        app.items(0).name should equal ("Aged Brie")

        app.items(0).sellIn should equal(7)
        app.items(0).quality should equal(11)
      }

      it should "increate quality twice for backstage passes; <= 10 days" in {
        val items = Array[Item](new Item("Backstage passes to a TAFKAL80ETC concert", 8, 10))
        val app = new GildedRose(items)
        app.updateQuality()
        app.items(0).name should equal ("Backstage passes to a TAFKAL80ETC concert")

        app.items(0).sellIn should equal(7)
        app.items(0).quality should equal(12)
      }

      it should "increate quality three for backstage passes; <= 5 days" in {
        val items = Array[Item](new Item("Backstage passes to a TAFKAL80ETC concert", 4, 10))
        val app = new GildedRose(items)
        app.updateQuality()
        app.items(0).name should equal ("Backstage passes to a TAFKAL80ETC concert")

        app.items(0).sellIn should equal(3)
        app.items(0).quality should equal(13)
      }

      it should "zero quality for backstage passes; == 0 days" in {
        val items = Array[Item](new Item("Backstage passes to a TAFKAL80ETC concert", 0, 10))
        val app = new GildedRose(items)
        app.updateQuality()
        app.items(0).name should equal ("Backstage passes to a TAFKAL80ETC concert")

        app.items(0).quality should equal(0)
        //invalid behaviour
        app.items(0).sellIn should equal(-1)
      }

      it should "zero quality for backstage passes; == -1 days" in {
        val items = Array[Item](new Item("Backstage passes to a TAFKAL80ETC concert", -1, 10))
        val app = new GildedRose(items)
        app.updateQuality()
        app.items(0).name should equal ("Backstage passes to a TAFKAL80ETC concert")

        app.items(0).quality should equal(0)
        //invalid behaviour for invalid input
        app.items(0).sellIn should equal(-2)
      }

      it should "degrade quality twice for Conjured items" in {
        val items = Array[Item](new Item("Conjured", 5, 10))
        val app = new GildedRose(items)
        app.updateQuality()
        app.items(0).name should equal ("Conjured")

        app.items(0).sellIn should equal(4)
        //new feature not avialable
        app.items(0).quality should equal(8)
      }
}