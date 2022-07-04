var express = require("express")
var app = express()
var http = require('http');

const {MongoClient} = require("mongodb")
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)

app.use(express.json())

let dbUser, dbCourse, userCollection

async function run(){
    try{
        await client.connect()
        console.log("Successfully connected to the database")
        var server = app.listen(8081, function (){
            var host = server.address().address
            var port = server.address().port
            console.log("Example app running at http://%s:%s", host, port)
        })
    }
    catch(err){
        console.log(err)
        await client.close()
    }

    dbUser = client.db("user")
    dbCourse= client.db("course")
    userCollection = dbUser.collection("userCollection")
}
run()

app.get("/getuserprofile/:userID", (req, res) => {
    userCollection.find({userID: req.params.userID}).toArray((err, userProfileResult) => {
      if (err) {
        console.error(err)
        res.status(500).json({ err: err })
        return
      }
      res.status(200).json(userProfileResult)
    })
})


app.get("/getstudentlist/:coursename", (req, res) => {
    var coursenamespace = req.params.coursename.substring(0,4)+ " "+req.params.coursename.substring(4,req.params.coursename.length)
    dbCourse.collection(coursenamespace).find({}).project({userID:1, displayName:1, _id:0}).toArray((err, resultstudent) => {
      if (err) {
        console.error(err)
        res.status(500).json({ err: err })
        return
      }
      res.status(200).json(resultstudent)
    })
})

app.get("/getcourselist/:userID", (req, res) => {
    userCollection.find({userID: req.params.userID}).project({courselist:1, _id:0}).toArray((err, resultcourse) => {
      if (err) {
        console.error(err)
        res.status(500).json({ err: err })
        return
      }
      res.status(200).json(resultcourse)
    })
})

app.post("/addusertocourse", async (req, res) => {
    try{
        await dbCourse.collection(req.body.coursename).insertOne({
            displayName: req.body.displayName,
            userID: req.body.userID,
          })
        res.status(200).send("User added successfully\n")
    }
    catch(err){
        console.log(err)
        res.send(400).send(err)
    }
})

app.post("/addcoursetouser", async (req, res) => {
    userCollection.updateOne({"userID": req.body.userID}, {$push:{"courselist":req.body.coursename}},(err, result)=>{
        if (err) {
            console.error(err)
            res.status(500).json({ err: err })
            return
        }
        res.status(200).json({ ok: true })
    });
})

app.post("/createprofile", (req, res) => {
    var courselistarr = [];
    var blockeruserarr = [];
  userCollection.insertOne(
    {
      displayName: req.body.displayName,
      userID: req.body.userID,
      coopStatus: req.body.coopStatus,
      yearStanding: req.body.yearStanding,
      courselist: courselistarr,
      blockedUser: blockeruserarr
    },
    (err, result) => {
      if (err) {
        console.error(err)
        res.status(500).json({ err: err })
        return
      }
      res.status(200).json({ ok: true })
    }
  )
})

app.post("/block", (req, res) => {
    userCollection.updateOne({"userID": req.body.userID}, {$push:{"blockedUser":req.body.blockedUserAdd}},(err, result)=>{
        if (err) {
            console.error(err)
            res.status(500).json({ err: err })
            return
        }
        res.status(200).json({ ok: true })
    });
})

app.delete("/deleteuserfromcourse/:userID/:coursename", async (req, res) => {
    try{
      var coursenamespace = req.params.coursename.substring(0,4)+ " "+req.params.coursename.substring(4,req.params.coursename.length)
        await 
        dbCourse.collection(coursenamespace).deleteOne({"userID": req.params.userID})
        res.status(200).send("User deleted successfully\n")
    }
    catch(err){
        console.log(err)
        res.status(400).send(err)
    }
})

app.post("/deletecoursefromuser", async (req, res) => {
    try{
        await 
        userCollection.updateMany({"userID": req.body.userID},{$pull: {"courselist": req.body.coursename}})
        res.status(200).send("Course deleted successfully\n")
    }
    catch(err){
        console.log(err)
        res.status(400).send(err)
    }
})

