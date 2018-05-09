import scala.collection.mutable.ListBuffer

def square(x: Double) {}   //call by value, eval function arguments before calling the function
def squarE(x: => Double) {} //call by name, eval function first, and then eval arguments if needed

def run(f: => Int) = f                  // call-by-name (lazy parameters)
def processInt(i: Int) = run(i+1)       // call-by-value

def double (nums: List[Int])           = for(i <- nums) yield i*2
def double2(nums: List[Int]):List[Int] = for(i <- nums) yield {i*2}
def double3(nums: List[Int]) = nums.map(_ * 2)

////// HIGHER ORDER FUNCTIONS
def sum(f: Int => Int): (Int, Int) => Int = {
  def sumf(a: Int, b:Int): Int = {...}
  sumf // sum() returns function that takes 2 intefers and returns integer
}
//same as:
def sum2(f: Int => Int)(a:Int, b:Int): Int = {...}
sum((x:Int) => x*x*x) //same as:   sum(x => x*x*x)
def cube(x: Int) = x*x*x
sum(x => x*x*x)(1,10) //sum of cubes from 1 to 10
sum(cube)(1,10) //same as above


//////
def time() = {
  println("Getting time in nano seconds")
  System.nanoTime
}
def delayed( t: => Long ) = {
  println("In delayed method")
  println("Param: " + t)
}
delayed(time())

def oncePerSecond(callback: () => Unit) {       // takes function as arg
  while (true) { callback(); Thread.sleep(1000) }
}
// pass in an anonymous function to the oncePerSecond function
//oncePerSecond(timefliesFUNCTION)
oncePerSecond( () => println("time flies like an arrow ..."))


 ////// VARARGS //////
def printStrings( args:String* ) = {        //String* = Array[String]     =  varargs
  var i : Int = 0           //    args.foreach(println)
  for( arg <- args ){
    println("Arg value[" + i + "] = " + arg )
    i = i + 1
  }
}
printStrings("Hello", "Scala", "Python")
//  use _* to adapt sequence (Array, List, Seq, Vector, etc.)
// operator tells the compiler to pass each element of the sequence to
// printStrings as a separate argument, instead of passing fruits as a single argument.
// This is also similar to using xargs on the Unix/Linux command line.
val fruits = List("apple", "banana", "cherry")
printStrings(fruits: _*)

////// RECURSION FUNCTIONS (fibo)      silnia
def factorial(n: BigInt): BigInt = {
  if (n == 0) 1
  else        n * factorial(n - 1)
}

////// NESTED FUNCTIONS
def factorial(i: Int): Int = {
  def fact(i: Int, accumulator: Int): Int = {
    if (i <= 1) accumulator
    else        fact(i - 1, i * accumulator)
  }
  fact(i, 1)
}

for (i <- 1 to 10)   println( "Factorial of " + i + ": = " + factorial(i) )
println( factorial(0) ) //1
println( factorial(1) ) //1
println( factorial(2) ) //2
println( factorial(3) ) //6

////// non tail-recursive
def sum(xs: List[Int]): Int = xs match {
    case x :: xs =>x + sum(xs)  // if there is an element, add it to the sum of the tail
    case Nil => 0 // if there are no elements, then the sum is 0
  }

////// tail-recursive solution
def sum2(xs: List[Int]): Int = {
  // @tailrec    import scala.annotation.tailrec
  def sumAccumulator(xs: List[Int], accum: Int): Int = {
    xs match {
      case Nil => accum
      case x :: tail => sumAccumulator(tail, accum + x)
      case label :: _ => throw new IllegalArgumentException("Unknown label" + label)
    }
  }
  sumAccumulator(xs, 0)   // productAccum(xs, 1)
}
def cumulativeTotalRec(nums: List[Int], totals: List[Int] = Nil): Seq[Int] = {
  nums match {
    case Nil => totals.reverse
    case n :: ns => cumulativeTotalRec(ns, (n + totals.headOption.getOrElse(0)) :: totals)
  }}

////////////////////// 3 best
def sumList(xs: List[Int]) : Int = {
  if (xs.isEmpty) 0
  else xs.head + sumList(xs.tail)
}
def cumulativeTotalStream(numbers: List[Int], total: Int = 0): Stream[Int] = {
  numbers match {
    case head :: tail =>  Stream.cons(total + head, cumulativeTotalStream(tail, total + head))
    case Nil =>           Stream.Empty
  }}


////////
@annotation.tailrec  // inna metoda: http://allaboutscala.com/tutorials/chapter-3-beginner-tutorial-using-functions-scala/scala-tutorial-learn-create-tail-recursive-function-scala-control-util-tailcalls/
def search(donutName: String, donuts: Array[String], index: Int): Option[Boolean] = {
  if(donuts.length == index) {
    None
  } else if(donuts(index) == donutName) {
    Some(true)
  } else {
    val nextIndex = index + 1
    search(donutName, donuts, nextIndex)
  }
}
val arrayDonuts: Array[String] = Array("Vanilla Donut", "Strawberry Donut", "Plain Donut", "Glazed Donut")
val found = search("Glazed Donut", arrayDonuts, 0)
println(s"Find Glazed Donut = $found")


//  Also you can avoid using recursion directly and use some basic
// abstractions instead:
val l = List(1, 3, 5, 11, -1, -3, -5)
l.foldLeft(0)(_ + _) // same as l.foldLeft(0)((a,b) => a + b)     // = 11
l.reduceLeft(_ + _)                                               // = 11
//  foldLeft is as reduce() in python. Also there is foldRight
// which is also known as accumulate (e.g. in SICP).
List(x1, ..., xn) reduceLeft op    // (...(x1 op x2) op x3) op ...) op xn
List(x1, ..., xn).foldLeft(z)(op)  // (...( z op x1) op x2) op ...) op xn
List(x1, ..., xn) reduceRight op   // x1 op (... (x{n-1} op xn) ...)
List(x1, ..., xn).foldRight(z)(op) // x1 op (... (    xn op  z) ...)

val numbers = List.range(1, 11)     // List[Int] = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
numbers.scanLeft(0)(_ + _).tail     // List[Int] = List(1, 3, 6, 10, 15, 21, 28, 36, 45, 55)
numbers.foldLeft(List[Int](0))((acc,e) => acc.head + e :: acc).reverse.tail   // jw.
numbers.foldLeft(List[Int](0))((acc,e) => acc.head + e :: acc) // List(55, 45, 36, 28, 21, 15, 10, 6, 3, 1, 0)

def cumulativeTotalComprehension(numbers: List[Int]): Seq[Int] = {
  var currentTotal = 0
  for (n <- numbers) yield {
    currentTotal += n
    currentTotal
  }}


///////  ANONYMOUS FUNCTIONS  //////
var inc = (x: Int) => x+1  // var z = inc(7) + 100
var mul = (x: Int, y: Int) => x*y  //println(mul(3,4))
var userDir = () => { System.getProperty("user.dir")}
def compose(g:Int=>Int, h:Int=>Int) = (x:Int) => g(h(x))    // pass in multiple blocks
val f = compose({_*2}, {_-1})       // f(0) = 0-1 =-1 , and then   -1*2 = -2

////// CURRYING FUNCTIONS
val zscore = (mean:Int, sd:Int) => (x:Int) => (x-mean)/sd   // zscore(1,2)(3)
def zscore2  (mean:Int, sd:Int) =  (x:Int) => (x-mean)/sd

def strcat2(s1: String) = (s2: String) => s1 + s2
def strcat(s1: String)(s2: String)    = s1 + s2    // uncurried    def f(a: Int, b: Int): Int
 val strcatPartial = strcat("pre-definiowany pierwszy") _      // partial function !! (starting with val)
println(strcatPartial("Drugi"))
println(strcat2("test1")("test2"))    // test1test2
println(strcat("test1")("test2"))     // test1test2

////// OTHER FUNCTIONS
def evenElems[T: ClassManifest](xs: Vector[T]): Array[T] = {
  val arr = new Array[T]((xs.length+1)/2)
  for (i <- 0 until xs.length by 2)
    arr(i/2) = xs(i)
  arr
}
evenElems(Vector(1,2,3,4,5))
evenElems(Vector("this", "is", "a", "test", "run", "last", "word"))

////// TYPED functions
def applyDiscount[T](discount: T): Unit = {
  discount match {
    case d: String => println(s"Looking for discount $d")
    case d: Int => print("Int $d")
    case _ => println("Unsupported)")
      //jesli ma cos zwracac to mozna na koncu kazdego case dodac np.  Seq[T](discount)
  }
}
applyDiscount[String]("COUPON_123")     // you need to call with [TYPE]
applyDiscount[Double](10)
applyDiscount(30.5)

private def applyForEach(inputs: Seq[Int], function: (Int) => Int): Seq[Int] = {
  val result = new ListBuffer[Int]()
  for (input <- inputs) {
    result += input
  }
  result
}
val primes = List(1,2,3,5,7,11,13,17,19,23)
val possiblePrimes = applyForEach(primes, { _*2 -1})

//////  REGEX  //////
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

// funkcje as parameter DEF and VAL
def totalCostWithDiscountFunctionParameter(donutType: String)(quantity: Int)(f: Double => Double): Double = {
  println(s"Calculating total cost for $quantity $donutType")
  val totalCost = 2.50 * quantity
  f(totalCost) }
// define and pass a def function to a higher order function  DEF =
def applyDiscount(totalCost: Double): Double = {
  val discount = 2 // assume you fetch discount from database
  totalCost - discount
}
println(s" ${totalCostWithDiscountFunctionParameter("Glazed Donut")(5)(applyDiscount(_))} ")

// VAL =>
val totalCostOf5Donuts = totalCostWithDiscountFunctionParameter("Glazed Donut")(5){totalCost =>
  val discount = 2 // assume you fetch discount from database
  totalCost - discount   }

val applyDiscountValueFunction = (totalCost: Double) => {
  val discount = 2 // assume you fetch discount from database
  totalCost - discount
}
println(s" ${totalCostWithDiscountFunctionParameter("Glazed Donut")(5)(applyDiscountValueFunction)} ")

val applyDiscountValueFunction1= (totalCost: Double) => { println() }     // without specifying return type
val applyDiscountValueFunction2: Double => Double = totalCost => { 0.45 } // with return type
val concatDonuts: (String, String)      => String = (a, b)    => a + b + " Donut "    // value function
//println(s"All donuts = ${donuts.fold("")(concatDonuts)}")


//////  PARTIAL FUNCTION  ///////
val isVeryTasty: PartialFunction[String, String] = {        // input: String, output: String
  case "Chocolate" | "Strawberry" => "Very tasty"
}
val unknownTaste: PartialFunction[String, String] = {
  case donut @ _ => s"Unknown taste for donut = $donut"     // case a if(a == "word")
}
println(s"Calling partial function isVeryTasty = ${isVeryTasty("Glazed Donut")}")
val donutTaste = isVeryTasty orElse unknownTaste      // mozna laczyc funkcje
println(donutTaste("Glazed Donut")) ;   println(donutTaste("Plain Donut")) ;   println(donutTaste("Chocolate Donut"))

val i : PartialFunction[Any, Unit] = {case x:Int => println("int found")}
val j : PartialFunction[Any, Unit] = {case x:Double => println("Double found")}
val * : PartialFunction[Any, Unit] = {case x=> println("Something else found")}
(i orElse j orElse *)(15.6)   // treat as function, f(x)
// inny przyklad orElse, chaining options
val propsTemplates = Option(System getProperty "MVN_CREATOR_TEMPLATES")
val envTemplates = Option(System getenv "MVN_CREATOR_TEMPLATES")
val defaultHome = Some(System getProperty "user.home")
propsTemplates.orElse(envTemplates).orElse(defaultHome).get

////// MATCHING with or
"word" match { case a @ ("word" | "hi") => println(a)}
1 match { case _:Int | _:Double => println("Found it")} //Fount it
1 match { case v @ ( _:Int | _:Double) => println(v)} //1.0
10 match { case i if i == 1 || i == 10 => "obviously this is a match"}
// matching with and
case object && {     def unapply[A](a: A)   = Some((a, a))  }
object StartsWith {  def unapply(s: String) = s.headOption  }
object EndsWith   {  def unapply(s: String) = s.reverse.headOption  }
"foo" match {  case StartsWith('f') && EndsWith('o') => println("f*o") }   // f*o

//ANDTHEN   f(x) andThen g(x) = g(f(x))       rezultat z pierwszej przekazany do drugiej
// println(s"Total cost of 5 donuts = ${ (applyDiscountValFunction andThen applyTaxValFunction)(totalCost) }")
//COMPOSE   f(x) compose g(x) = f(g(x))       rezultat z drugiej przekazany do pierwszej
// println(s"Total cost of 5 donuts = ${ (applyDiscountValFunction compose applyTaxValFunction)(totalCost) }")
//  The call-by-name function parameter exchangeRate: => Double  //${placeOrderWithParam(listOrders)(usdToGbp)
// will evaluate any exchangeRate function each time it is called.
//  This is in contrast to the function defined above which had a call-by-value
// exchangeRate: Double     {placeOrder(listOrders)(0.5)}
// function parameter  This meant that any exchange rate passed through would be evaluated only once.

def apply(f: Int => String, v: Int) = f(v)
def layout[A](x: A) = "[" + x.toString + "]"
println( apply( layout, 10) )   // gives  [10]

// The extraction method (mandatory)
def unapply(str: String): Option[(String, String)] = {
  val parts = str split "@"         // "string".split("\\s+")          "string,string".split(",").map(_.trim)
  if (parts.length == 2){      Some(parts(0), parts(1))   }
  else {      None    }
}
println ("Unapply method : " + unapply("Zara Ali"))
println ("Unapply method : " + unapply("Zara@gmail.com"))


///////////  EXTRACTORS  ////////////
object Extractor {
  def main(args: Array[String]) {
    val x = Extractor(5)
    x match {
      case Extractor(num) => println(x+" is bigger two times than "+num)
      case _ => println("i cannot calculate")
    }
  }
  def apply(x: Int) = x*2
  def unapply(z: Int): Option[Int] = if (z%2==0) Some(z/2) else None
}