import Array._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

// --------------------- VARIABLES
val height = 1.9d
val name: String = "James"
val piSinglePrecision: Float = 3.14159265f
val bigNumber: Long = 1234567890
val smallNumber: Byte = 127

// --------------------- PRINT
println(f"$name%s is $height%2.2f meters tall")       //James is 1.90 meters tall
println(f"Zero padding on the left: $numberOne%05d")  // 00001
println(s"Use s prefix $numberOne $truth")
println(s"1 + 1 = ${1 + 1}")
println(raw"Result = \n a \n b")

// ---------------------- REGEX
val UltimateAnswer: String = "To life everything is 42"
val pattern = """.* ([\d]+).*""".r        //search for first number
val pattern(result_goes_here) = UltimateAnswer
val answer = result_goes_here.toInt

import scala.util.matching.Regex
val str = "Scala is scalable and cool"
val pattern2 = "Scala".r
val pattern = new Regex("(S|s)cala")

println(pattern2.findAllIn(str).mkString(","))
println(pattern.findAllIn(str).mkString(":::::"))
println(pattern.replaceFirstIn(str, "Java"))
// https://www.tutorialspoint.com/scala/scala_regular_expressions.htm
val quote = """I don't like to commit myself about heaven and hell - you see, I have friends in both places."""
val expr = "e".r
expr.replaceAllIn(quote, "**")  //same as
quote.replaceAll("e", "**")     //same as
expr.replaceAllIn(quote, s => if(util.Random.nextBoolean) "?" else "*")    // raz ? raz *
expr.replaceAllIn(quote, m => m.start.toString)  //I don't lik11 to commit mys26lf about h37av40n and h48ll - you s5960, I hav68 fri73nds in both plac90s.
expr.replaceSomeIn(quote,m => if(m.start>50) None else Some("-")) //I don't lik- to commit mys-lf about h-av-n and h-ll - you see, I have friends in both places.



// --------------------- BOOLEAN
val imposs = isGreater & isLesser     // both expressions are evaluated !
val imposs = isGreater && isLesser     // left expression is evaulated first, better performance
val x : Boolean =  val1 == val2       // compares values and assigns true


// --------------------- MATCHING
val number : Int = 3
number match {
  case 1 => println("one")
  case _ => println("none")
}


// --------------------- LOOPS
do { println(x); x+=1 } while (x <= 10)



// ----------------------- TUPLE ------- (can hold 22 objects with different types),  immutable lists, can hold different types
val t = (1, "hello", Console)
val (a,b,c) = t
val suma =  t._1  +  t._2  +  t._3 
t.productIterator.foreach{ i => println("Value = " + i )}

// can be created with k/v pair with ->
val pair = "key" -> "value"
println(pair._2)    // value



// ------------------- LISTS ----------------------, can not hold items of different types
list.head   //returns 1st item
list.tail   //returns list of remaining items List[String] = List[a,b,c....]

// iterating through a list
for (ship <- shipList) { println(ship) }

val backwardShips = shipList.map( (ship: String) => { ship.reverse} )   // reverse each ship

val numberedList = List(1,2,3,4,5)
val sum = numberedList.reduce( (x: Int, y: Int) => x + y)   //15
val noFives = numberedList.filter( (x: Int) => x != 5)
val noThree = numberedList.filter( _ != 3)

// concatenating lists, distinct, contains
val lotsOfNumbersDuplicates = list1 ++ list2
val distinctValues = lotsOfNumbersDuplicates.distinct  // List[Int] = List....
val hasThree = distinctValues.contains(3)   // true

val dim: List[List[Int]] =
List(
  List(1, 0, 0),
  List(0, 1, 0),
  List(0, 0, 1)
)
val list = 1 :: 2 :: 3 :: Nil   // List(1,2,3)
val fruits = "apples" :: ("oranges" :: ("pears" :: Nil))   //new ::(0, new ::(1,List.empty))
val fruit2 = fruits.map(name => <li>{name}</li>)        //List(<li>Fred</li>, <li>Joe</li>, <li>Bob</li>)
val nums = 1 :: (2 :: (3 :: (4 :: Nil)))
val emptyy = Nil
val x4 = List[Number](1, 2.0, 33d, 0x1)
val dimm = (1 :: (0 :: (0 :: Nil))) ::
  (0 :: (1 :: (0 :: Nil))) ::
  (0 :: (0 :: (1 :: Nil))) :: Nil
// List1.concat(List2)      List1:::List2     List1 .:::(List2)       c = List.concat(List1, List2)      c = a ++ b
val list3 = List.range(1, 10,2)
val x5 = List.fill(3)("foo")
val squares = List.tabulate(6)(n => n * n)
val mul = List.tabulate( 4,5 )( _ * _ )   // List(List(0, 0, 0, 0, 0), List(0, 1, 2, 3, 4), List(0, 2, 4, 6, 8), List(0, 3, 6, 9, 12))
val y7: Seq[Any] = 0 :: x4  // dodaje PRZED x4, czyli jako zerowy element


val xs = List(1,2,3,4,5,6,7,8,9,10,11,12) ; val ys = List(13,14,15)
val g = xs.grouped(3)
g.next()
g.next()
val s = xs.sliding(3)
s.next()
s.next()
val it = xs.iterator
it.next()
it.next()
val x = List(1,2,3)
val y = List(4,5,6)
List.concat(x,y).zipWithIndex           // List[(Int, Int)] = List((1,0), (2,1), (3,2), (4,3), (5,4), (6,5))


// ------------------- MAPS ---- (dictionaries)
val shipMap = Map("key1" -> "val1",  "key2" -> "val2")
print(shipMap.contains("key1"))   //false
val ship1 = util.Try(shipMap("key1")) getOrElse "Unknown"

var A:Map[Char,Int] = Map() // empty map
A += ( 'K' -> 1)
val colors = Map("red" -> "#FF0000", "azure" -> "#F0FFFF")
val movies = Map('a' -> 1, 'b' -> 10, 'c' -> 100)
Map(("x",1), ("y",2)).apply("x")    //.get("x) returns Some(1)
Map(("x",1), ("y",2)).get("x")
Map(("z",1)) + ("zz"->2)
// concatenating    Map1 ++ Map2            Map1.++(Map2)
// iterate over Map
for ((k,v) <- colors) printf("key: %s, value: %s\n", k, v)
colors foreach (x => println (x._1 + "-->" + x._2)) // movies.keys.foreach( (movie) => if (movies.contains(movie)) similarItems += (movie -> true))
                                                    // for ((movie1, rating1) <- movies) { if (movies.contains(movie1)) similarItems += (movie1 -> true) }
colors foreach {case (key, value) => println (key + "-->" + value)}
colors map { case (k,v) => s"$k is $v" }
(xs zip ys) map { case (x,y) => x*y }
val myMap = Map("I" -> 1, "V" -> 5, "X" -> 10)  // create a map
myMap("I")      // => 1
myMap("A")      // => java.util.NoSuchElementException
myMap get "A"   // => None
myMap get "I"   // => Some(1)



// -------------- SETS
var set1 : Set[Int] = Set()
Set('a', 'b', 'c', 'a')('b')  //Sets are Iterables that contain no duplicate elements
Set()('a')
Seq(1,2,3,4,5).+:("6")
val dupa = Seq.fill(5) { "s" }
val dupa1= Seq.iterate(1, 5) { i => i *3 }
// concatenate --->       Set1 ++ Set2        // Set1.++(Set2)
// intersect  ---->       Set1.& Set2         Set1.intersect(Set2)
val fruit = Set("apple", "banana", "berry", "cherry") & Set("cherry")
fruit.+("dupa")
Map.empty ++ List(("a", 1), ("b", 2), ("c", 3))  //  Map((a,1), (b,2), (c,3))




////// FOR COMPREHENSION //////
for ((e, count) <- xs.zipWithIndex) { println(s"$count is $e") }
for {i <- 1 to 10
     if i % 2 == 0
     j = i } yield j    //same as:   for (i <- 1 to 10; if i % 2 == 0) yield i           IndexSeq(2,4,6,8,10)
for {x <- 1 to 10 if x == 1 || x == 10 } yield x    //IndexSeq(1,10)
for (x <- xs if x%2 == 0) yield x*10 //same as
xs.filter(_%2 == 0).map(_*10)
for ((x,y) <- xs zip ys) yield x*y //same as
(xs zip ys) map { case (x,y) => x*y } 	//for comprehension: destructuring bind
for (x <- xs; y <- ys) yield x*y  // same as:
xs flatMap {x => ys map {y => x*y}} // 	for comprehension: cross product
for (x <- 1 to 2 ; y <- 3 to 4) yield x + y // Vector(4, 5, 5, 6)        //same as:
for (x <- 1 to 2 ; y <- 3 to 4) yield (x,y) // Vector((1,3), (1,4), (2,3), (2,4))        //same as:
(1 to 2) flatMap (x => (3 to 4) map (y => (x, y)))
for (x <- xs; y <- ys) {   println("%d/%d = %.1f".format(x,y, x*y)) } 	//for comprehension: imperative-ish sprintf-style
for (i <- 1 to 5) { println(i) } 	//for comprehension: iterate including the upper bound

for (x <- e1) yield e2          //is translated to e1.map(x => e2)
for (x <- e1 if f) yield e2     //is translated to for (x <- e1.filter(x => f)) yield e2
for (x <- e1; y <- e2) yield e3 //is translated to e1.flatMap(x => for (y <- e2) yield e3)

for {
  i <- 1 until n
  j <- 1 until i
  if isPrime(i + j)
} yield (i, j)
// same as:
for (i <- 1 until n; j <- 1 until i if isPrime(i + j))
  yield (i, j)
// same as
(1 until n).flatMap(i => (1 until i).filter(j => isPrime(i + j)).map(j => (i, j)))

//Multiple assignments
val (myVar1: Int, myVar2: String) = Pair(40, "Foo")



////// ORDERING //////
object DescendingAlphabetOrdering extends Ordering[String]{
  def compare(element1: String, element2: String) = element2.compareTo(element1)
}
val sortedSet6: mutable.SortedSet[String] = mutable.SortedSet("jeden", "dwa", "trzy")(DescendingAlphabetOrdering)

import math.Ordering
def msort[T](xs: List[T])(implicit ord: Ordering) = {...}
msort(fruit.toList)(Ordering.String)
msort(fruit.toList)


////////////////////////  ARRAY   ///////////////////////////
// compabitle with Seq,stores a fixed-size sequential collection of elements of the same type.
val a1 = Array(1,2,3)
val a2 = a1.map(_ * 3)
val seq1: Seq[Int] = a1          // WrappedArray
val a3 = seq1.toArray
var z:ArrayBuffer[String] = new ArrayBuffer[String](3)  //var z = new Array[String](3), can hold 3 elements  STRINGARRAY
z(0)="pierwszy" ; z(1)="drugi"
var myMatrix = ofDim[Int](3,3)  // multidimensional      print(myMartix(i)(j))
val arrayrange = range(10,20,2)  // https://www.tutorialspoint.com/scala/scala_arrays.htm

// to make is expandable
var fruits1 = ArrayBuffer[String]()
fruits1 += "Apple"
fruits1 += "Banana"
fruits1 += "Orange"
// ArrayBuffer MUTABLE, to samo z ListBuffer
val buf = scala.collection.mutable.ArrayBuffer.empty[Int]
buf += 1
buf += 10
buf.toArray





///////////////////////  OPTIONS  ///////////////////////// zero or one element of a given type.
def show(x: Option[String]) = x match {
  case Some(s) => s
  case None => "?"
    // case 0 | "" => false
    // case _ => true
}
// mozna tez bardziej elegancko uzyc MAP, zeby wyfiltrowac NONE      glazedDonutTaste.map(taste => println(s"glazedDonutTaste = $taste"))
val capitals = Map("France" -> "Paris", "Japan" -> "Tokyo")
println("show(capitals.get( \"Japan\")) : " + show(capitals.get( "Japan")) )
println("show(capitals.get( \"Niger\")) : " + show(capitals.get( "Niger")) )
Option[Int](5).getOrElse("Nige")
def getMapValue(s: String): String = capitals.get(s).map("Value found: " + _).getOrElse("No value found")


// convert String to Int
def toInt(s: String) : Option[Int] = {
  try { Some(s.toInt) }       //  Some(Integer.parseInt(in.trim))
  catch {case e: Exception => None}
}
// 0th method
toInt("10").getOrElse(0)       // bo inaczej rezultatem bedzie Some(10). Mozna tez toInt("string") match { case Some(s) => println(s" to jest $s")
// 1st method
toInt("1").foreach( i => println(s"Got an $i"))
// 2nd method
toInt("1") match {
  case Some(i) => println(i)           // => i
  case None => println("didnt work")  // => 0
}
// 3rd method
val bag = List("1", "2", "foo", "3", "bar")
val sum = bag.flatMap(toInt).sum            // bedzie ok, bo toInt zmieni na null jak trzeba
val sum2= bag.map(toInt)            // List[Option[Int]] = List(Some(1), Some(2), None, Some(3), None)
val sum3= bag.map(toInt).flatten  // = bag.flatMap(toInt)     // List[Int] = List(1, 2, 3)
val sum4= bag.map(toInt).collect{case Some(i) => i}

@throws(classOf[NumberFormatException])     // declare that your method can throw an exception
def toIntt(s: String) = s.toInt




/////////////////////// STREAM ///////////////////////////////////////////////////////
println("**** STREAM a stream can be infinitely long")
val streamm = 1 #:: 2 #:: 3 #:: Stream.empty     //.foreach(print _)    // stream(0)=1   stream(1)=2
val fibs = FibForm(1,1).take(7).toList
Stream.cons(1, Stream.cons(2, Stream.empty)) foreach println   // 1 2
def FibForm(a: Int, b: Int): Stream[Int] = a #:: FibForm(b, a+b)
def i(x:Int,y:Int):Stream[Int]       = (x*y) #:: i(x+1,y*2)
i(2,3) take 3 foreach println   // 6, 18, 48
def make : Stream[Int] = Stream.cons(util.Random.nextInt(10), make)
val infinite = make
def make2(i:Int) : Stream[String] = {             // konczaca sie warunkiem
  if(i==0) Stream.empty
  else Stream.cons(i + 5 toString, make2(i-1)) }
val finite = make2(5)

Stream.from (10, 3) take 3 foreach println //10, 13, 16
(1 until 4).toStream foreach println      //1,2,3
Stream.continually(7) take 49 reduceLeft {_ + _}   // 7*49 = 343
Stream.fill(6)(1 to 2 toStream) foreach println  // stream of 6 streams
Stream.fill(6)(1 to 2 toStream).flatten take 6 foreach println  // flatten, each is separate 1 2 1 2 1 2
(1 until 20 by 3).toStream foreach println //same
range(1, 20, 3) foreach println             //same
Stream.cons

////////////////////  ITERATE  ///////////////////////////////
iterate(3,100000){i => i-10} take 5 foreach println _  // infinite stream   3, -7, -17 , -27, -37
iterate(3,5){i => i} foreach println           //restricted to 5 elements


////////////////////  VECTOR  ////////////////////////////////////////
println("**** VECTOR allow accessing any element of the list in “effectively” constant time")
val wektorek = Vector.empty
val wektorek2= wektorek  :+ 1 :+ 2
val wektorek3= 100 +: wektorek2
wektorek3(0)  // Vector(100, 1, 2)

////////////////////// STACK //////////////////////////////////
val stack = scala.collection.immutable.Stack.empty
val hasOne= stack.push(1)
hasOne.top
hasOne.pop
// A push on an immutable stack is the same as a :: on a list and a pop on a stack is the same as a tail on a list.

///////////////////// QUEUES  //////////////////////////////////  A Queue is just like a stack except that it is first-in-first-out rather than last-in-first-out.
val empty = scala.collection.immutable.Queue[Int]()
val has1 = empty.enqueue(1)       // add elem to queue
val has123 = has1.enqueue(List(2,3))
val (element, has23) = has123.dequeue //zwraca tuple
// Queues mutable , cab use +=, ++=
val queue = new scala.collection.mutable.Queue[String]
queue += "a"
//queue ++= "bcde"
queue ++= List("b", "c")
queue.dequeue() // zwraca a
queue



//////////////////////////  HASHMAP  ///////////////////////////////
val haszMap = mutable.HashMap.empty[Int, String]
val emptyMap: mutable.HashMap[String,String] = mutable.HashMap.empty[String,String]
haszMap += (1 -> "this is test")
haszMap += (3 -> "another linke")
haszMap(3)
haszMap.contains(2)
val hashMap1: mutable.HashMap[String, String] = mutable.HashMap(("PD","Plain Donut"),("SD","Strawberry Donut"),("CD","Chocolate Donut"))
println(s"Element by key VD = ${hashMap1("SD")}")
hashMap1 ++= hashMap1//println("\nStep 5: How to add elements from a HashMap to an existing HashMap using ++=")


def lazyMap[T, U](coll: Iterable[T], f: T => U) = new Iterable[U] {
  def iterator = coll.iterator map f
}

//HashSet
val hashSet1: mutable.HashSet[String] = mutable.HashSet("Plain Donut","Strawberry Donut","Chocolate Donut")
hashSet1 ++= hashSet1[String]("Vanilla Donut", "Glazed Donut")

////////////////////////////// VIEWS ////////////////////////////////
val v = Vector(1 to 10: _*)
v.map(_ * 2).map(_ + 1)   //this creates extra intermediate data structure vectors along the way
// A more general way to avoid the intermediate results is by turning
// the vector first into a view, then applying all transformations to the view,
// and finally forcing the view to a vector:
v.view          //gives you a SeqView, i.e. a lazily evaluated Seq.
v.view.map(_ * 2).map(_ + 1)  // first, Int, shows the type of the view’s elements. The second, Vector[Int] shows you the type constructor you get back when forcing the view
v.view.map(_ * 2).map(_ + 1).force

val keys = List(1,2,3)
val values=List("a","b","c")
Map(keys.zip(values).toArray: _*) //option1
(keys zip values) toMap           //option2

def isPalindrome(x: String) = { x == x.reverse }
def findPalindrome(s: Seq[String]) = s.find(isPalindrome)
val words = Seq("lato", "zaaz", "maam")
findPalindrome(words.take(10000)) //needs to create representation of 1000000 elements
findPalindrome(words.view.take(100000))  // excellent results

val arrr = (0 to 9).toArray
val subarrr = arrr.view.slice(2,4)

//////////////////////////  ITERATORS //////////////////////////////
// while (it.hasNext)    // same as     for (elem <- it) println(elem)
// println(it.next())    // same as           it foreach println

val it1 = Iterator("a", "sample", "iter")
//val it2 = it1.map(_.length)
//it2.foreach(println)

val it3 = it1.dropWhile(_.length >1)
it3.hasNext
it3.next()

val (wordss, ns) = Iterator("a", "number", "of", "words", "", "empty").duplicate
val (xxx, yyy) = ns.duplicate
ns.toList
val shorts = wordss.filter(_.length < 3).toList
val count = ns.map(_.length).sum
xxx.map(_.length).sum
// iterator.foreach(println)

//////////////////// BUFFERED ITERATORS //////////////////////////////
//"------Using a buffered iterator, skipping empty words can be written as follows.")
def skipEmptyWords(it: BufferedIterator[String]) =
  while (it.head.isEmpty) { it.next() }
//skipEmptyWords(yyy.buffered)
val xx = Iterator(1,2,3,4)
val bxx= xx.buffered
bxx.head
bxx.head
bxx.next
bxx.head

def collapse(it: Iterator[Int]) = {
  val (zeros, rest) = it.span(_ == 0)
  println(zeros.toList)
  println(rest.toList)
   }
collapse(Iterator(0, 0, 0, 1, 2, 3,0, 4 ,0 ,5 ,0 ,6,0,0,0,0,8))   // 2 listy,   List(0,0,0)    List(1,2,3,...)


/////////////////////  TRAITS  //////////////////////////
trait Equal {
  def isEqual(x: Any): Boolean
  def isNotEqual(x: Any): Boolean = !isEqual(x)
}

class Point(xc: Int, yc: Int) extends Equal {
  var x: Int = xc
  var y: Int = yc

  def isEqual(obj: Any) = obj.isInstanceOf[Point] && obj.asInstanceOf[Point].x == y
}

///////////////////  ENUMERATION /////////////
object Donut extends Enumeration {
  type Donut = Value
  val Glazed = Value(0, "Glazed")
  val Straw  = Value(1, "Straw")
}
Donut.values
object Options extends Enumeration {
  val ONE, TWO, THREE, FOUR = Value }
Options.values.filter(_ => util.Random.nextBoolean).mkString(",")   // TWO, FOUR


  ///////////////////  ASSERT  /////////////////////
var called = 0
assert(called ==1,  {print("Called is not 0       ")})
require(called ==1, {print("Called is not 0       ")})
assume(called ==1,  {print("Called is not 0       ")})

// StringBuilder
val bufstr = new StringBuilder
bufstr += 'a'
bufstr ++= "bcde"
bufstr.toString()