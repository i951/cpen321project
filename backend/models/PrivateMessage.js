const mongoose = require('mongoose')
const {v4: uuidv4} = require("uuid")

const privateMessageSchema = new mongoose.Schema(
    {
        _id: {
            type: String,
            default: () => uuidv4().replace(/\-/g, ""),
        },
        senderName: String,
        receiverName: String,
        messageContent: mongoose.Schema.Types.Mixed,
    },
    {
        timestamps: true,
        collection: "privateMessages",
    }
)

module.exports = mongoose.model("PrivateMessage", privateMessageSchema)
