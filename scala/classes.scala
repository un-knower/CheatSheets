val al = new Person("Alvin", "Alexander", 20)
println(al)
val fred = new Person("Fred", "Flinstone")
println(fred)
val barney = new Person("Barney")
println(barney)

class Person(val firstName: String, val lastName: String, val age: Int) {
  def this(firstName: String) {     // constructor
    this(firstName, "", 0);
    println("\nNo last name or age given.")
  }
  def this(firstName: String, lastName: String) {
    this(firstName, lastName, 0);
    println("\nNo age given.")
  }
  override def toString: String = {
    return "%s %s, age %d".format(firstName, lastName, age)
  }
}
val a = new Person("aaa","last")
println(a)


///////////  IMPLICIT CLASS  ///////////////
implicit class BetterString(val s: String) {
  def increment: String = s.map(c => (c + 1).toChar)
  def toInt(radix: Int): Int = Integer.parseInt(s, radix)
  def hideAll: String = s.replaceAll(".", "*")      //replace all chars with *
  def plusOne = "d".toInt(2) + 1
  def asBoolean = s match {
    case "0" | "zero" | "" | " " => false
    case _ => true
  }
}
// from now on, every string will have additional method, mozna tez zrobic implicit class i nie potrzeba tej funkcji
// to ponizej to w starszych wersjach potrzebne
/*implicit*/ def stg(s: String): BetterString = new BetterString(s)
"fooo".increment
"1000".toInt(2)
// inny przyklad
// put in OBJECT StringUtils , a pozniej mozna uzyc import com.alvin.utils.StringUtils._
// put in PACKAGE OBJECT utils, pozniej improt co.alvin.utils._"
implicit class IntWithTimes(x: Int) {
  def times[A](f: => A): Unit = {
    def loop(current: Int): Unit =
      if(current > 0) {
        f
        loop(current - 1)
      }
    loop(x)    }     }
5 times print("Hi, ")            // Hi, Hi, Hi, Hi, Hi

class DonutString(s: String) {
  def isFavoriteDonut: Boolean = s == "Glazed Donut" }
object DonutConverstions {
  implicit def stringToDonutString(s: String): DonutString = new DonutString(s) }
import DonutConverstions._
val glazedDonut = "Glazed Donut"
val vanillaDonut = "Vanilla Donut"
println(s"Is Glazed Donut my favorite Donut = ${glazedDonut.isFavoriteDonut}")
println(s"Is Vanilla Donut my favorite Donut = ${vanillaDonut.isFavoriteDonut}")


// aliasing the Tuple2 type and giving it a more meaningful name of
// CartItem which is essentially a pair of Donut item with the quantity being bought.
case class Donut(name: String, price: Double, productCode: Option[Long] = None)
type CartItem[Donut, Int] = Tuple2[Donut, Int]
val cartItem = new CartItem(vanillaDonut, 4)    // cartItem._1,  cartItem._2
// mozna tez zrobic nowa case class
case class ShoppingCartItem(donut: Donut, quantity: Int)
val shoppingItem: ShoppingCartItem = ShoppingCartItem(Donut("Glazed Donut", 2.50), 10)
println(s"shoppingItem donut = ${shoppingItem.donut}")


///////  INHERITANCE  /////////
abstract class Donutt(name: String) {
  def printName(): Unit
}
class VanillaDonutt(name: String) extends Donutt(name) {
  override def printName(): Unit = println(name)
}
object VanillaDonuttOBJ {
  def apply(name: String): Donutt = {
    new VanillaDonutt(name)
  }
}
val vanillaDonutt: Donutt = VanillaDonuttOBJ("Vanilla Donut") //vanillaDonutt.printName
val vanilla2: Donutt = new VanillaDonutt("name")              //vanilla2.printName
object VanillaDonutttOBJ {
  def apply(name: String): Donutt = {
    name match {
      //case "Glazed Donut" => new GlazedDonutt(name)
      case "Vanilla Donut" => new VanillaDonutt(name)
      //case _ => new Donutt(name)
  }
}
  class ShoppingCart[D <: Donutt](donuts: Seq[D]) {
    def printCartItems: Unit = donuts.foreach(_.printName)
  }
  val shoppingCart: ShoppingCart[Donutt] = new ShoppingCart(Seq[Donutt](vanillaDonutt, vanillaDonutt))
  shoppingCart.printCartItems

}

////// adding comments number 2
object Helpers {
  implicit class IntWithTimes(x: Int) {
    def times[A](f: => A): Unit = {
      def loop(current: Int): Unit =
        if(current > 0) {
          f
          loop(current - 1)
        }
      loop(x)
    }
  }
}
5 times println("hi")

////// adding new datatypes seamlessly
import java.math.BigInteger
class BigInt(val bigInteger: BigInteger) extends java.lang.Number {
  def + (that: BigInt) = new BigInt(this.bigInteger add that.bigInteger)
  def - (that: BigInt) = new BigInt(this.bigInteger subtract that.bigInteger)
}


import java.io.File
class MultipleAssignments {   //When multiple fields depend on the same temporary variables the fields can be assigned together from one block by returning a tuple and using Scala's matching to expand the tuple during assignment.  See previous topics on assignment for details
  val (tmp,count) = {
    val file = new File("tmp")
    if(!file.exists()) {
      file.mkdirs()
    }
    val tmp = file.getAbsolutePath()
    val count = file.listFiles.length
    (tmp, count)
  }
  val mvn_repo = {
    val home = new File(System.getProperty("user.home"))
    new File(home, ".m2").getPath()
  }
}