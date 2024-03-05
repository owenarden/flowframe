import org.apache.spark.sql._
object LambdaTest {

   def compareTwo(a : Int, b : Int) : Boolean ={
        a == b
   }
  
   def testWrapper(compareTwoArg: (Int, Int) => Boolean) : Boolean = {
        val result = compareTwoArg(1, 1)
        result
   }

   def apply(spark:SparkSession): Unit = {
        val res = testWrapper(compareTwo)
   }

}