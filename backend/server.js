require("./config/mongo.js")

const {MongoClient} = require("mongodb")
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)
client.connect()

const express = require('express')
const http = require('http')
const app = express()

const server = http.createServer(app)
const { Server } = require("socket.io")
const io = new Server(server)
// global.io = io

const Message = require('./models/Message.js')
const chatEngine = require('./controllers/chatEngine.js')

const chatEngineRouter = require('./routes/chatEngine-routes.js')
const port = "3010"

const userStore = require('./controllers/userStore.js')
const courseManager = require('./controllers/courseManager.js')

app.use(express.json())

let dbUser, dbCourse, userCollection

//---------------------------------------------------------------------------------------
server.listen(port, () => {
    console.log('Node app is running on port: ' + port)
})

dbUser = client.db("user")
dbCourse= client.db("course")
userCollection = dbUser.collection("userCollection")

app.get('/', (req, res) => {
    res.send('Server is running on port: ' + port)
})
//---------------------------------------------------------------------------------------
/*app.get("/getuserprofile/:userID", (req, res) => {
    userCollection.find({userID: req.params.userID}).toArray((err, userProfileResult) => {
      if (err) {
        console.error(err)
        res.status(500).json({ err: err })
        return
      }
      res.status(200).json(userProfileResult)
    })
})*/

app.get("/getuserprofile/:userID", userStore.getUserProfile)


/*app.get("/getstudentlist/:coursename", (req, res) => {
    var coursenamespace = req.params.coursename.substring(0,4)+ " "+req.params.coursename.substring(4,req.params.coursename.length)
    dbCourse.collection(coursenamespace).find({}).project({userID:1, displayName:1, _id:0}).toArray((err, resultstudent) => {
      if (err) {
        console.error(err)
        res.status(500).json({ err: err })
        return
      }
      res.status(200).json(resultstudent)
    })
})*/

app.get("/getstudentlist/:coursename", courseManager.getStudentList)

/*app.get("/getcourselist/:userID", (req, res) => {
    userCollection.find({userID: req.params.userID}).project({courselist:1, _id:0}).toArray((err, resultcourse) => {
      if (err) {
        console.error(err)
        res.status(500).json({ err: err })
        return
      }
      res.status(200).json(resultcourse)
    })
})*/

app.get("/getcourselist/:userID", userStore.getCourseList)


/*app.post("/addusertocourse", async (req, res) => {
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
})*/

app.post("/addusertocourse", courseManager.addUserToCourse)

/*app.post("/addcoursetouser", async (req, res) => {
    userCollection.updateOne({"userID": req.body.userID}, {$push:{"courselist":req.body.coursename}},(err, result)=>{
        if (err) {
            console.error(err)
            res.status(500).json({ err: err })
            return
        }
        res.status(200).json({ ok: true })
    });
})*/

app.post("/addcoursetouser", courseManager.addCourseToUser)

/*app.post("/createprofile", (req, res) => {
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
})*/

app.post("/createprofile", userStore.createProfile)

/*app.post("/block", (req, res) => {
    userCollection.updateOne({"userID": req.body.userID}, {$push:{"blockedUser":req.body.blockedUserAdd}},(err, result)=>{
        if (err) {
            console.error(err)
            res.status(500).json({ err: err })
            return
        }
        res.status(200).json({ ok: true })
    });
})*/

app.post("/block", userStore.block)

/*app.delete("/deleteuserfromcourse/:userID/:coursename", async (req, res) => {
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
})*/

app.delete("/deleteuserfromcourse/:userID/:coursename", courseManager.deleteUserFromCourse)

/*app.post("/deletecoursefromuser", async (req, res) => {
    try{
        await 
        userCollection.updateMany({"userID": req.body.userID},{$pull: {"courselist": req.body.coursename}})
        res.status(200).send("Course deleted successfully\n")
    }
    catch(err){
        console.log(err)
        res.status(400).send(err)
    }
})*/

app.post("/deletecoursefromuser", courseManager.deleteCourseFromUser)
//---------------------------------------------------------------------------------------

// todo: separate routes for each module 
// localhost:3031/chat/getConversationByGroupID/:groupID
// localhost:3031/chat/getPrivateConversationByUserNames/:senderName/:receiverName
// app.use('/chat', chatEngineRouter)

// routes for chatEngine
app.get('/getConversationByGroupID/:groupID', chatEngine.getConversationByGroupID)
app.get('/getPrivateConversationByUserNames/:senderName/:receiverName', chatEngine.getPrivateConversationByUserNames)

var usersSockets = {}

// socketio connection
io.on('connection', (socket) => {
    console.log('a user connected')

    socket.on('joinGroupChat', function (groupID, displayName) {
        console.log(displayName + " : joined at groupID : " + groupID)
        socket.join(groupID)
    })

    socket.on('groupMessage', (groupID, senderName, messageContent) => {
        console.log(senderName + " : " + messageContent)
        
        // save message to database 
        chatEngine.saveMessageToDB(groupID, senderName, messageContent)

        // emit the message to clients connected in the room
        let message = {
            "message": messageContent,
            "senderNickname": senderName
        }

        // send message to all users in groupID, including current user
        io.sockets.in(groupID).emit('groupMessage', message)
    })

    socket.on('joinPrivateChat', function (displayName) {
        console.log("Inside joinPrivateChat:")
        usersSockets[displayName] = socket.id
        console.log(displayName + " : initiated a private chat")
        console.log("usersSockets[displayName]: " + usersSockets[displayName])
        // socket.join(groupID)
    })

    socket.on('privateMessage', (senderName, receiverName, messageContent, isBlocked) => {
        if (isBlocked == 0) {
            console.log("Inside privateMessage:")
            console.log("usersSockets[senderName]: " + usersSockets[senderName])
            console.log("usersSockets[receiverName]: " + usersSockets[receiverName])

            console.log("PM: " + senderName + " -> " + receiverName + " : " + messageContent)

            chatEngine.savePrivateMessageToDB(senderName, receiverName, messageContent)

            // emit the message to clients connected in the room
            let message = {
                "message": messageContent,
                "senderNickname": senderName
            }

            let receiverSocketID = usersSockets[receiverName]
            // add check for senderSocketID (socket.id),
            // for try to send without join first
            if (receiverSocketID) {
                console.log("Both users are joined, emit message: ")
                // this only shows to other user 
                socket.to(receiverSocketID).emit("privateMessage", message)
            } else {
                // msg is shown to the user itself on the frontend
                console.log("Other user is not joined, do nothing on server: ")
                // console.log("Other user is not joined, emit msg to self: ")
                // socket.emit("privateMessage", message)
            }
        } else {
            console.log(receiverName + " has blocked " + senderName + " , can't send message")
        }
    })

    socket.on('disconnect', function () {
        console.log("a user disconnected")
    })
})
