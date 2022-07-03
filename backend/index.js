const express = require('express')
const http = require('http')

const app = express()
const server = http.createServer(app)
const { Server } = require("socket.io");
const io = new Server(server);

require("./config/mongo.js")

const Message = require('./models/Message.js');
const chatEngine = require('./controllers/chatEngine.js')

server.listen(3010, () => {
    console.log('Node app is running on port 3010')
})

app.get('/', (req, res) => {
    res.send('Chat Server is running on port 3010')
});

// routes
app.get('/getConversationByRoomID/:roomID', chatEngine.getConversationByRoomID)

// socketio connection
io.on('connection', (socket) => {
    console.log('a user connected')

    socket.on('joinChat', function (groupID, userNickname) {
        console.log(userNickname + " : joined at roomID : " + groupID);
        socket.join(groupID)
        // socket.broadcast.emit('userjoinedthechat', userNickname + " : has joined the chat ");
    })

    // socket.on('getConversationByRoomId', (groupID) => {
    //     // on frontend need to modify the Message class OR need to process the message here (like in sendMessage)
    // })

    // create a function sendMessage in chatEngine.js and move below there
    socket.on('message', (groupID, senderName, messageContent) => {
        //log the message in console 
        console.log(senderName + " : " + messageContent)
        
        // save message to database 
        let messageToSaveToDB = Message(
            {
                roomID: groupID,
                postedByUser: senderName,
                message: messageContent
            }
        )

        messageToSaveToDB.save(function (err) {
            if (err) {
                console.log("Error saving message to database: " + err)
                return err
            }
            console.log("Message saved to database")
        })

        // emit the message to clients connected in the room
        let message = {
            "message": messageContent,
            "senderNickname": senderName
        }
        // send the message to all users including the sender  using io.emit  
        // io.emit('message', message)

        // send message to all users in groupID, including current user
        io.sockets.in(groupID).emit('message', message)
    })

    socket.on('disconnect', function () {
        // console.log(userNickname + ' has left ')
        console.log("user disconnected")
        socket.broadcast.emit("userdisconnect", ' user has left')
    })

})

