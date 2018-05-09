val text : String = "non-changable"        #final in java, cannot be changed
var change : String = "changable"

def sayHello(name : String) : String = {
  s"Hello $name!"                             # 's' allows to place variables with $
}  # no need to RETURN
# allows multiple params list

def sayHello(name: String)(myself: String) = {        # val result = sayHello("kris")("kross")
  s"Hello $name! My name is $myself"
}

def sayHello(name: String)(IMPLICIT whoAreYou : () => String) = {    # 2nd param is a func that takes no input and returns String
  s"Hello $name! My name is ${whoAreYou()}"
}                
IMPLICIT def provideName() = { "Scala" }
val fast = sayHello("test")(providename)              # with IMPLICIT this (providename) is not required
val faster = sayHello("test") { () => "Anonymous" }

# implicits -> looks only in scope
def sayHello(name: String)(implicit myself: String) = {
  s"Hello $name! My name is $myself"
}
implicit val myString = "implicits"
val fast = sayHello("test")           # compliler will take word "implicits" and use it in the function, as matches params list

##############################################################################################################
class fastTrack(val name: String, var myself: String) {
  def sayHello(name: String)(myself: String) = {
    s"Hello $name! My name is $myself"
  }
  val greeting = sayHello(name)(myself)         # this is constructor
}
val fast = new fastTrack("test", "me")
println(fast.name)    # getters
fast.myself = "fast"  # setters
#############################################################################################################
case class person(fname: String, lname: String)   # case gives: serializable, toString, hashCode, equality, dont require NEW 
val me = person("name", "surname")
println(me.fname)
#############################################################################################################
# pattern matching (similar to switch in other lang)

abstract class Person(fname: String, lname: String) {
  def fullName = {s"$fname-$lname"}
}
case class Student(fname: String, lname: String, id: Int) extends Person(fname, lname)
val me = Student("John", "Rambo", 30)

def getFullID[T <: Person](something: T) = {                              # T - template, generic, java uses <T>, scala [T]
  something match {                                                       # < - means T must be derivative of Person
    case Student(fname, lname, id) => s"$fname-$lname-$id"
#   case std: Student => s"${std.fname)-${std.lname}-${std.id}"
    case p: Person => p.fullName
  }
}
getFullID(me)
############################################################################################################
implicit class stringUtils(myString: String) {        # can take any String and convert to instance of myself (class)
  def scalaWordCount() = {
    val split = myString.split"\\s+"
    val grouped = split.groupBy(word => word)                         # Map[String, Array[String]]
    val countPerKey = grouped.mapValues(group => group.length)        # .mapValues  maps on Value = Array[String]
    countPerKey                                                       # Map[String, Long]
  }
}
"Spark collections mimic Scala collections".scalaWordCount()
#############################################################################################################
val myList = List("Spark", "mimics", "Scala", "collections")
val mapped = myList.map(s => s.toUpperCase)                 # s: String  possible too.  mapped: List[String] = List[SPARK,...]
#############################################################################################################
val flatMapped = myList.flatMap { s =>
  val filters = List("mimics", "collections")
  if (filters.contains(s))
    None              # Option[T]
  else
    Some(s)           # Option[T]
############################################################# LOGGING
import org.apache.log4j.{Level, Logger, LogManager} 
var logowanie = Logger.getLogger("log4j.logger.myLogger")
l.setLevel(org.apache.log4j.Level.INFO)
// https://www.mapr.com/blog/how-log-apache-spark
object Holder extends Serializable {   @transient lazy val log = Logger.getLogger(getClass.getName)    }

val someRdd = spark.parallelize(List(1, 2, 3))
   someRdd.map {
     element =>
       Holder.log.info (s"$element will be processed")
       element + 1
    }
###########################################
#Linux shell script
  #!/bin/sh
exec scala "$0" "$@" // exec scala -savecompiled "$0" "$@"  , creates and uses JAR 
!#
object HelloWorld {
  def main(args: Array[String]) {
    println("Hello, world! " + args.toList)
  }
}
HelloWorld.main(args)
Console.println("Hello, world!")
argv.toList foreach Console.println
  ###########################################
  import sys.process._
  val result = "ls -al" !   //returns code, e.g. 0
  val result = "ls -al" !!  //returns content of command
  val result = "ls -al" #| "grep Foo" !     // pipeline usage
  val result = "ps auxw" #| "grep http" #| "wc -l" !
  val numProcs = ("ps auxw" #| "wc -l").!!.trim
  val r = Seq("/bin/sh", "-c", "ls | grep .scala").!!  //To execute a series of commands in a shell
