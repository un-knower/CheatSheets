import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.MongoConnection

object MongoFactory {
    private val SERVER = "localhost"
    private val PORT   = 27017
    private val DATABASE = "portfolio"
    private val COLLECTION = "stocks"
    val connection = MongoConnection(SERVER)
    val collection = connection(DATABASE)(COLLECTION)
}
