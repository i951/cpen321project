const Message = require('../models/Message.js');

module.exports = {
    getConversationByRoomID: async (req, res) => {
        console.log("trying to get convo")

        try {
            const { roomID } = req.params
            console.log("trying to get convo at roomID: " + roomID)

            Message
                .find({ 'roomID': roomID })
                .select({ 
                    _id: 0,
                    postedByUser: 1,
                    message: 1                
                }) // exclude _id from result
                .sort({ createdAt: 'asc' }) // ascending order
                .exec((err, retrievedMsgs) => {
                    if (err) {
                        console.log("Error in getConversationByRoomID: " + err)
                        return err
                    }
                    console.log("retrievedConvo: " + retrievedMsgs)
                    return res.status(200).send({retrievedMsgs})
                })            
        } catch (error) {
            console.log(error)
            return res.status(500).json({ success: false, error })
        }
    }
    // move sendMessage here
}