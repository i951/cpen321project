var express = require("express")

var chatEngine = require("../controllers/chatEngine.js")
const router = express.Router()

router
    .get('/getConversationByGroupID/:groupID', 
        chatEngine.getConversationByGroupID)
    .get('/getPrivateConversationByUserNames/:senderName/:receiverName', 
        chatEngine.getPrivateConversationByUserNames)

module.exports = router