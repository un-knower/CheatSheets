#Dockerfile
FROM ubuntu
MAINTAINER Firstname Lastname your@email.com
RUN 
       apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0RBB10 && 
       echo "deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen" | sudo tee /etc/apt/sources.list.d/mongodb.list && 
       apt-get update && 
       apt-get install -y mongodb-org
VOLUME ["/data/db"]
WORKDIR /data
EXPOSE 27017
CMD ["mongod"]
# sudo docker build -t mongodb


docker run --name some-mongo -v /my/own/datadir:/data/db -d mongo
# our server
docker run -it -d mongo
docker run --name mongo-dev -d -v //c/Users/x://data/db -p 27017:27017 mongodb
# our client to connect to server
docker run –it –link=<container server ID>:<new_alias> mongo /bin/bash
mongo 172.17.0.2:27017 
docker restart mongo-dev


MONGO, document oriented, stores doc. in BSON format

Doc-orient:  key value pair, but it is semi structured like XML, JSON, BSON. These structures are considered as documents.

RDBMS			MongoDB
Database		Database - physical container for collections
Table			Collection - group of documents (RMDBS - table)
Tuple/Row		Document
column			Field
Table Join		Embedded Documents
Primary Key		Primary Key (Default key _id provided by mongodb itself)
				Schema-less
				Easy to scale
				Do joins while write, not on read


# REPLACE, to replace document with  _id  one need to pass definition of new one (without "set")
db.mycol.save(  {    "_id" : ObjectId(5983548781331adf45ec7), "title":"Tutorials",  "by":"Tutorials Point"   }   )

## CREATE COLLECTION  // creates automatically when you insert document
db.createCollection("mycol", { capped : true, autoIndexId : true, size : 6142800, max : 10000 } )

## INSERT
use test   # will not autocomplete "test" until something is inserted
db.test.insert( { "address" : { "street" : "sezame", "number" : "1", "coord" : [54.34, 23,23] } }
for (var i = 1; i <= 15; i++) {
 db.test.insert( { x : i , y : "name " + i,  qty: i*10 }  )
}
db.test.insert( { "sku" : "US", "item" : {"qty" : 6, "price" : 2.65 } } )

QUERY, SEARCH
db.test.find().pretty()   → .will return all !!!
db.restaurants.find() → returns all collection
db.test.find({},{"title":1,_id:0}).limit(2).skip(1) // find ALL, hide  _id column, show only "title" column, display 2,3el
db.test.find( {"name":"kris"} )
db.test.find( {"address.street" : "sezame"} )
db.test.find( {"address.number" : { $gt: 10, $lt : 30 } } )   #less than
db.test.find( {"address.number" : 1,  "address.street" : "sezame" } )                 # logical AND
db.test.find( $and: [ {"address.number" : 1,  "address.street" : "sezame" } ] )   # logical AND
db.test.find( $or: [ {"address.number" : 1}  , {"address.street" : "sezame" } ] )   # logical OR
db.test.find({"likes": {$gt:10}, $or: [{"by": "tutorials point"}, {"title": "MongoDB Overview"}]})  # … AND (... OR …)

db.test.find().sort( {qty:1} )
db.test.find().sort( {"address.number" : 1,  "address.street" : "sezame" } )    # ascending,   desc: -1
db.test.find({},{"title":1,_id:0}).sort({"title":-1})

# SORT
s1 = {$sort : {qty:1, y:-1}}
# INDEX
sort can use index when new document is not created, i.e. cmd  $group, $sort, $project not used!

# LIMIT, will stop pipeline after limit reached
s1 = [ {$match : { "x" : { "$gte" : 2, "$lt" : 100 } } }, { $limit:4 } ]

# SKIP, helpful to remove outliers! ,   paging possible with $skip/$limit
# sorting must be fixed by coherent key, not changing as we godb.test
skip = {$skip : 1}
lim = {$limit : 2}
db.test.aggregate(lim)			# first limit
db.test.aggregate(skip, lim)	# then skip   MULTIPLE STAGES, sep by comma
s1.$skip = 2					# then increase skip


# UPDATE
db.test.update(
    { "name" : "kris" },
    {
      $set: { "cuisine": "American (New)" },
      $currentDate: { "lastModified": true }
    },
    { multi : true }                 # by defaults update just one document
)



https://docs.mongodb.com/manual/reference/sql-aggregation-comparison/
https://www.tutorialspoint.com/mongodb/mongodb_aggregation.htm


# REMOVE   //SQL truncate
db.test.remove ( {"name" : "kris"} , {justone : true} )   # by default ALL, so we need to specify to delete one
db.restaurants.remove ( {} )  # remove all documents from collection
db.restaurants.drop()            # drops collection including indexes


# AGGREGATE  tutorialpointlink
db.restaurants.aggregate(
   [
     { $match: { "borough": "Queens", "cuisine": "Brazilian" } },
     { $group: { "_id": "$address.zipcode" , "count": { $sum: 1 } } }
   ]
);

// result:    { "_id" : "90210", "count" : 969 }

# MATCH (FILTER), it's a pipeline operator,  find is not
s1 = {$match : { "qty" : 50, "y" : "name 5" } }
s1 = {$match : { "x" : { "$gte" : 2, "$lt" : 5 } } }

# GROUP
s1 = {$group: { _id: "$sku", "sumaaa": {$sum:1}, "avg": {$avg: "$qty"} , "total": {$sum: "$sale"} } }
s1 = {$group: { _id: "$sku", "sumaaa": {$sum:1}, "avg": {$avg: "$item.qty"} } }  # nested
db.test.aggregate(s1)

# addToSet ensures result array contains distinct members only once, uses more CPU/RAM
s1 = {$group: { _id: "$qty", skus: {$addToSet: "$y"} } }
# push - same as addToSet but we get repetitions, used LESS cpu/ram
s1 = {$group: { _id: "$qty", skus: {$push: "$y"} } }
# sample  first/last, remember about SORTING !!!
s1 = {$group: { _id: "$qty", sample: {$first: "$qty"} } }
s1 = {$group: { _id: "$sku", docId: {$first: "$_id"} } };
s1 = {$group: { _id: { "q" : "$quarter" , "y" : "year" } } }
// s2 = {$sort : { "_id.y" : 1 }}    // sorting by year


# UNWIND, takes input TABLE [...] and create several documents as output
# result is not array any more, it is a new output,  ["a", "b", "c"], becomes 3 separate documents
s1 = {$unwind : "$item"}		# reverse :  GROUP...addToSet + push....

# PROJECT
s1 = {$project : { _id: 0, tags: 1, "name" : 1, "Book name long name we use" : "$name" }}
s1.$project._id=0		# do not display _id

we should trim info passed between stages, e.g.
db.test.aggregate(
		{$project: { _id:0, tags1, by:1}},
		{$sort: { by:1}},
		{$project: {tags:1}} ) 			# no need to pass _id between stages

# MULTIPLY
s1 = {$project : { "_id" : 1 , "math" : { "$multiply" : [ "$_id" , 1000 ] } } }
// returns    [ { "_id" :1,  "math":1000}]		# mod, divide cannot do that

# CONCAT, SUBSTR
s1 = {$project : {cited: {$concat : ["$name", " - written by " , "$by"]}}}
s1 = {$project : {startswith: {$substr : ["$by", 0 ,1 ]}}}

# QUARTER Q1
s1 = {$project: {q: {$divide : [{$month: "$d"}, 4]} , d:1 , _id:0 } }
quarterCalc = {$cond :  [ { $gt: [ { $month : $d}, 9 ]}, "Q4" , ....]}
"data" = ISODate("2012-07-26T05:24:02.820Z")

# IF NULL
s1 = {$project : {weight : {$ifNull: ["$weight", 1]}}}


################
#### MAP REDUCE
################
function myMap(){
	var total = this.price + this.shipping;
	var car   = this.category.name;
	if ( total > 10) { emit(cat, total); }	// emit(this.by, 1);
}

function myReduce(key, values) {
	val reducedValue = Array.sum(values);	// javascript  Array.sum([1,2,3])
	return reducedValue;
}

// finalize (optional) gets called only once per key with a final reduced value across all documents for this key
function finalize(key, reducedValue){
	var result = key + ' wrote ' + reducedValue + ' book ' + (reducedValue >1 ? 's' : '');
	return result;
}

preview = { "out" : { "inline" : 1 }}		// results are computed in memory and returned as single value
db.test.mapReduce(map,reduce, {"out": {"replace": "collection1"}})
db.test.mapReduce(map,reduce, {"out": {"merge"  : "collection1"}}) // append
db.test.mapReduce(map,reduce, {"out": {"reduce" : "collection1"}}) // takes existing value and uses to compute new
// but we need to limit output, not to add same thing again
var o1 = {out: {merge: "booksByAuthor"}, query: {by: /^[a-g]/}};
var o2 = {out: {merge: "booksByAuthor"}, query: {by: /^[h-o]/}};
var o1 = {out: {merge: "booksByAuthor"}, query: {d: {$lt : ISODate("2000-01-01T00:00:00Z")}}}
var o2 = {out: {merge: "booksByAuthor"}, query: {d: {$gte : ISODate("2000-01-01T00:00:00Z")}}}

var finalizeOptions = { out : {"inline":1} , finalize: finalize} //name of function from above
db.test.mapReduce(myMap, myReduce, finalizeOptions)


# inny przyklad
var mapBook = function(){
	var value = {fullName:null, titles: {}, bio:null};
	value.titles[this.name] = this.d;
	emit(this.by, value);
}

var reduceBook = function(key, values){
	var reduced = {fullName:null, titles: {}, bio:null};
	values.forEach(function(item){
		for (name in item.titles){
			reduced.titles[name] = item.titles[name];
		}
		if (item.bio) {
			reduced.bio = item.bio;
		}
		if (item.fullName) {
			reduced.fullName = item.fullName;
		}
	});
	return reduced;
}

var mapAuthor = funcion(){
	var value = {fullname: null, titles: {}, bio:null};
	var fullName = this.first + ' ' + this.last;
	var shortBio = this.bio.split(/\W+/).slice(0,9).join(' ');
	value.fullName = fullName;
	value.bio = shortBio;
	emit (this.first, value);
}

// consider using jsMode,  which will prevent from converting intermediate results to/from JSON/BSON
{jsMode: true, out: {...}}

// sharded collection (must be enabled)
{out: {reduce: 'publicity', sharded: true}}

// aggregate-on-write
db.test.save(
	{_id: 'c1'},
	{$inc: {count:1, total:3}},
	{upsert: true})


################
######## OPERATORS
################
{$eq : [v1, v2]}    $ne, $lt, $gt, $lte, $gte
{$cmp}   -1 for v1<v2,  0 v1=v2,  1 for v1>v2	// case Sensitive
{$add : [v1,v2,v3,....]}   $MULTIPLY		// 3 or more
{$subtract : [v1, v2]}   $divide,   $mod	// only 2 values
{$toLower}   {$toUpper}
{$strcasecmp: [s1, s2]}		// case inSensiTive
{$cond: [expr, valueIfTrue, valueIfFalse] }




#################
#### CREATE INDEX
#################
db.restaurants.createIndex( { "cuisine": 1 } )     // ascending
db.restaurants.ensureIndex( { "cuisine": 1 } )     // ascending
db.restaurants.createIndex( { "cuisine": 1, "address.zipcode": -1 } )    // compound, multiple fields

remember to index BEFORE  $group, $project, $unwind

Mongo Python

http://api.mongodb.com/python/current/tutorial.html
https://www.tutorialspoint.com/mongodb/mongodb_replication.htm

Mongo replication
Sharding (horizontal scaling) is the process of storing data records across multiple machines and it is MongoDB"s approach to meeting the demands of data growth. As the size of the data increases, a single machine may not be sufficient to store the data nor provide an acceptable read and write throughput.

Modelling referenced (manual) relationship:
>var result = db.users.findOne({"name":"Tom Benzamin"},{"address_ids":1})   //wyciagamy liste adresow (_Id)
>var addresses = db.address.find({"_id":{"$in":result["address_ids"]}})	//wyciagamy detale danego adresu
