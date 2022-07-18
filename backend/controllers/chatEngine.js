const Message = require('../models/Message.js')
const PrivateMessage = require('../models/PrivateMessage.js')
const userStore = require('./userStore.js')

module.exports = {
    // joinChat: async (req, res) => {
    //     console.log(userNickname + " : joined at groupID : " + groupID)
    //     socket.join(groupID)
    // },

    saveMessageToDB: (groupID, senderName, messageContent) => {
        let messageToSaveToDB = Message(
            {
                groupID,
                senderName,
                messageContent
            }
        )

        messageToSaveToDB.save(function (err) {
            if (err) {
                console.log("chatEngine: Error saving message to database: " + err)
                return err
            }
            console.log("chatEngine: Message saved to database")
        })
    },
    getConversationByGroupID: async (req, res) => {
        const { groupID } = req.params
        // console.log("req.params.groupID: " + req.params.groupID)
        console.log("chatEngine: trying to get convo at groupID: " + groupID)

        Message
            .find({ groupID })
            .select({ 
                _id: 0,
                senderName: 1,
                messageContent: 1                
            }) 
            .sort({ createdAt: 'asc' }) 
            .exec((err, retrievedMsgs) => {
                if (err) {
                    console.log("chatEngine: Error in getConversationByGroupID: " + err)
                    return res.status(400).send(err)
                } else {
                    // console.log("chatEngine: retrievedConvo: " + retrievedMsgs)
                    return res.status(200).send({retrievedMsgs})
                }
            })            

    }, 
    savePrivateMessageToDB: (senderName, senderID, receiverName, receiverID, messageContent) => {
        let messageToSaveToDB = PrivateMessage(
            {
                senderName,
                senderID,
                receiverName,
                receiverID,
                messageContent
            }
        )

        messageToSaveToDB.save(function (err) {
            if (err) {
                console.log("chatEngine: Error saving private message to database: " + err)
                return err
            }
            console.log("chatEngine: Private message saved to database")
        })
    }, 
    getPrivateConversationByUserIDs: async (req, res) => {
        const { senderID, receiverID } = req.params
        console.log("chatEngine: getPrivateConversationByUserIDs: " + senderID + " -> " + receiverID)

        let senderName = await userStore.getDisplayNameByUserIDfromDB(senderID);
        let receiverName = await userStore.getDisplayNameByUserIDfromDB(receiverID);
        console.log("senderName: " + senderName)
        console.log("receiverName: " + receiverName)


        PrivateMessage
            .find({
                $or: [
                    {
                        senderID, 
                        receiverID
                    }, 
                    {
                        'senderID': receiverID, 
                        'receiverID':  senderID
                    }
                ]
            })
            .select({ 
                _id: 0,
                senderID: 0,
                receiverID: 0,
            }) 
            .sort({ createdAt: 'asc' }) 
            .exec((err, retrievedMsgs) => {
                if (err) {
                    console.log("chatEngine: Error in getPrivateConversationByUserIDs: " + err)
                    res.status(500).json({ success: false, error })
                } else {
                    // console.log("chatEngine: retrievedConvo: " + retrievedMsgs)
                    res.status(200).send({retrievedMsgs})
                }
            })           
    }
}