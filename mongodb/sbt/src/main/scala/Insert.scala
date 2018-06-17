import com.mongodb.casbah.Imports._
import Common._

object Insert extends App {

    // create some Stock instances
    val apple = Stock("AAPL", 600)
    val google = Stock("GOOG", 650)
    val netflix = Stock("NFLX", 60)

    // save them to the mongodb database
    saveStock(apple)
    saveStock(google)
    saveStock(netflix)

    // our 'save' method
    def saveStock(stock: Stock) {
        val mongoObj = buildMongoDbObject(stock)
        MongoFactory.collection.save(mongoObj)
    }

}
