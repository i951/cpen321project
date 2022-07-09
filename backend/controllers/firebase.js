const admin = require("firebase-admin");
const serviceAccount = require("../serviceKey.json");
admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });     

// const { client } = require("./db/dbConnector.js");
// let dbUser, dbCourse, userCollection
// dbUser = client.db("user")
// dbCourse = client.db("course")
// userCollection = dbUser.collection("userCollection")

const {MongoClient} = require("mongodb")
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)
client.connect()

let dbUser, userCollection

dbUser = client.db("user")
dbCourse = client.db("course")
userCollection = dbUser.collection("userCollection")

// await client.connect(); //not needed

// send message to firebase 
// const sendMessageToFirebase = (senderName, receiverName, messageContent) => {
//     const db = admin.firestore(); // do we need to store the notifications we send?
//     const messageToSaveToDB = PrivateMessage(
//         {
//             senderName: senderName,
//             receiverName: receiverName,
//             messageContent: messageContent
//         }
//     )
//     db.collection('privateMessages').add(messageToSaveToDB)
// }


module.exports = {
    _userAddedNotification: async (userID, courseID) => {


        // #TODO: check the await thing here please as well
        try {
            let otherstudents = await dbCourse.collection(courseID).find({}).project({ userID: 1, displayName: 1, _id: 0 }).toArray();

            otherstudents.remove(userID);
            theTokens = [];
            otherstudents.forEach(student => {
                regToken = (userCollection.findOne({ userID: student.userID }).registrationToken);
                const message = {
                    notification: {
                        title: 'A New User Joined ' + courseID,
                        body: 'Say Hi to the new user who just joined the course' + courseID,
                    },
                    token: tokss,
                };

                admin.messaging().send(message)
                    .then((response) => {
                        if (response.failureCount > 0) {
                            const failedTokens = [];
                            response.responses.forEach((resp, idx) => {
                                if (!resp.success) {
                                    failedTokens.push(registrationTokens[idx]);
                                }
                            });
                            console.log('List of tokens that caused failures: ' + failedTokens);
                        }
                        else {
                            console.log('Successfully sent message to a user : ' + student.userID);
                        }
                    });
            });

        }

        catch (err) {
            console.log("For sending user added notification, the err:" + err);
            // res.status(400).send(err)
        }




    },
    get userAddedNotification() {
        return this._userAddedNotification;
    },
    set userAddedNotification(value) {
        this._userAddedNotification = value;
    },

    newRegistrationToken : async (req,res) => {
       try{
        await userCollection.updateOne({userID: req.body.userID}, {$set: {registrationToken: req.body.registrationToken}})
            if (err) {
                console.error(err)
                res.status(500).json({ err: err })
                return
            }
            res.status(200).json({ok:true})
            // resultstudent.forEach(student => {
            //     if (student.userID != userID) {
            //         userAddedNotification(student.userID, courseID)
            //     }
            // }
            // )
        }
        catch (err) {
            console.log("Could not update token for" + req.body.userID + " with the error " + err)
            // res.status(400).send(err)
        }
       
    },
// displayName:0, userID:1, coopStatus:0, yearStanding:0, registrationToken:1, courselist:0, blockedUser:0

    privateMessageNotification : async (senderName, receiverID) => {

        try {

            // #TODO: clean this await thing
            let resultstudent = await userCollection.findOne({ userID: receiverID }) 
        

            // regToken = (userCollection.findOne({ userID: receiverID }).registrationToken)
            const message = {
                notification: { 
                    title: 'Private Message from ' + senderName, 
                    body: "You've got a private message from " + senderName, 
                },
                token: resultstudent.registrationToken,
            };


            admin.messaging().send(message)
            .then((response) => {
                if (response.failureCount > 0) {
                    const failedTokens = [];
                    response.responses.forEach((resp, idx) => {
                        if (!resp.success) {
                            failedTokens.push(registrationTokens[idx]);
                        }
                    });
                    console.log('Private message notification failure, token: ' + failedTokens);
                }
                else{
                    console.log('Successfully sent message notification to a user : ' + resultstudent.userID);
                }
            }); 
       }
       catch (err) {
           console.log("Failure to send private message notification, the err: " + err)
           // res.status(400).send(err)
       }
    },

    groupMessageNotification : async (senderName, groupID) => {

    }


    // testMessageSyntax : (thetoken) => {
        
    //     thetoken.forEach(tokss => {
    //     const message = {
    //         notification: { 
    //             title: 'notification works', 
    //             body: 'my bodddyyyyyy big big body' 
    //         },
    //         token: tokss,
        
    //     };
    

    //     admin.messaging().send(message)
    //         .then((response) => {
    //             if (response.failureCount > 0) {
    //                 const failedTokens = [];
    //                 response.responses.forEach((resp, idx) => {
    //                     if (!resp.success) {
    //                         failedTokens.push(registrationTokens[idx]);
    //                     }
    //                 });
    //                 console.log('List of tokens that caused failures: ' + failedTokens);
    //             }
    //             else{
    //                 console.log('Successfully sent message to all tokens');
    //             }
    //         }); 
    //     });
        
    
    // },

    // sendMessageNotification : (stff,stuff2) = {
    // need to do this for message notifications
    // },
};