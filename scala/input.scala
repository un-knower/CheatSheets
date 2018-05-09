package ScalaCheatSheet

/**
  * Created by Krzysztof_Klapecki on 10/02/2017.
  */
object input {
  //reading input from user
  //val input_from_user = scala.io.StdIn.readLine()
  // val java_input_from_user = Console.readLine()
  import java.util.Scanner

  val input = "Joe 33 200.0"       //val input = readLine()
  val line = new Scanner(input)
  val name = line.next            // val n = name.toString
  val age = line.nextInt           //val b = age.asInstanceOf[Long]    // age.toString.toInt
  val weight = line.nextDouble     //val c = c.asInstanceOf[Double]    // c.toString.toDouble
  val (a, b, c) = scala.io.StdIn.readf3("{0} {1,number} {2,number}")
  val tokens = input.split(" ");
  val name1 = tokens(1) //.toInt
  print(Console.UNDERLINED)
  //println(f"$a%d $b pizzas coming up, $$$c%.2f.")
  print(Console.RESET)
  // using regex
  val ExpectedPattern = "(.*) (\\d+) (\\d*\\.?\\d*)".r
  val ExpectedPattern(a1, b1, c1) = input
  // writing to file
  import java.io._

  val writer = new PrintWriter(new File("test.txt"))
  writer.write("Hello Scala")
  writer.close()
  // reading from file
  import scala.io.Source

  Source.fromFile("test.txt").foreach(print)
  val fileContents = Source.fromFile("test.txt").getLines.mkString
  val fileContents2 = Source.fromFile("test.txt").getLines.toList;
  fileContents2.foreach(println)
  for (line <- Source.fromFile("test.txt").getLines()) {
    println(line)
  }

}