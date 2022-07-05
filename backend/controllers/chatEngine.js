const Message = require('../models/Message.js')
const PrivateMessage = require('../models/PrivateMessage.js')

module.exports = {
    // joinChat: async (req, res) => {
    //     console.log(userNickname + " : joined at groupID : " + groupID)
    //     socket.join(groupID)
    // },

    saveMessageToDB: (groupID, senderName, messageContent) => {
        let messageToSaveToDB = Message(
            {
                groupID: groupID,
                postedByUser: senderName,
                message: messageContent
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
        // console.log("trying to get convo")
        try {
            const { groupID } = req.params
            // console.log("req.params.groupID: " + req.params.groupID)
            console.log("chatEngine: trying to get convo at groupID: " + groupID)

            Message
                .find({ 'groupID': groupID })
                .select({ 
                    _id: 0,
                    postedByUser: 1,
                    message: 1                
                }) 
                .sort({ createdAt: 'asc' }) 
                .exec((err, retrievedMsgs) => {
                    if (err) {
                        console.log("chatEngine: Error in getConversationByGroupID: " + err)
                        return err
                    }
                    // console.log("chatEngine: retrievedConvo: " + retrievedMsgs)
                    return res.status(200).send({retrievedMsgs})
                })            
        } catch (error) {
            console.log(error)
            return res.status(500).json({ success: false, error })
        }
    }, 
    savePrivateMessageToDB: (senderName, receiverName, messageContent) => {
        let messageToSaveToDB = PrivateMessage(
            {
                senderName: senderName,
                receiverName: receiverName,
                messageContent: messageContent
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
    getPrivateConversationByUserNames: async (req, res) => {
        // console.log("trying to get convo")
        try {
            const { senderName, receiverName } = req.params
            console.log("chatEngine: getPrivateConversationByUserNames: " + senderName + " -> " + receiverName)

            PrivateMessage
                .find({
                    $or: [
                        {
                            'senderName': senderName, 
                            'receiverName':  receiverName
                        }, 
                        {
                            'senderName': receiverName, 
                            'receiverName':  senderName
                        }
                    ]
                })
                // .find({ 
                //     'senderName': senderName, 
                //     'receiverName':  receiverName
                // })
                .select({ 
                    _id: 0,
                    senderName: 1,
                    receiverName: 1,
                    messageContent: 1                
                }) 
                .sort({ createdAt: 'asc' }) 
                .exec((err, retrievedMsgs) => {
                    if (err) {
                        console.log("chatEngine: Error in getPrivateConversationByUserNames: " + err)
                        return err
                    }
                    // console.log("chatEngine: retrievedConvo: " + retrievedMsgs)
                    return res.status(200).send({retrievedMsgs})
                })            
        } catch (error) {
            console.log(error)
            return res.status(500).json({ success: false, error })
        }
    }
}