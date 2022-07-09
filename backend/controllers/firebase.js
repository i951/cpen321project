// const { initializeApp } = require('firebase-admin/app');

// const admin = initializeApp();

// const functions = require("firebase-functions");
// const admin = require("firebase-admin");
// admin.initializeApp({ credential: admin.credential.applicationDefault() });
    // credential: applicationDefault(),
    //databaseURL: 'https://<DATABASE_NAME>.firebaseio.com' dont think we need it at the moment
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
    userAddedNotification: (userID, courseID) => {

        try {
             dbCourse.collection(courseID).find({}).project({ userID: 1, displayName: 1, _id: 0 }).toArray((err, otherstudents) => {
                if (err) {
                    console.error(err)
                    res.status(500).json({ err: err })
                    return
                }
            })

            otherstudents.remove(userID)
            theTokens = []
            otherstudents.forEach(student => {
                regToken = (userCollection.findOne({ userID: student.userID }).registrationToken)
                const message = {
                    notification: { 
                        title: 'A New User Joined ' + courseID, 
                        body: 'Say Hi to the new user who just joined the course' + courseID, 
                    },
                    token: tokss, // what is tokss ? @abhishek
                
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
                    else{
                        console.log('Successfully sent message to a user : ' + student.userID);
                    }
                }); 
            });
            






            
        

            // These registration tokens come from the client FCM SDKs.
            // const registrationTokens = theTokens;

            // const message = {
            //     notification: { 
            //         title: 'New user in' + courseID, 
            //         body: 'Say Hi to '+ userID.displayName + ' who just joined the course' + courseID 
            //     },
            //     tokens: registrationTokens,
            // };

            // app.getMessaging().sendMulticast(message)
            //     .then((response) => {
            //         if (response.failureCount > 0) {
            //             const failedTokens = [];
            //             response.responses.forEach((resp, idx) => {
            //                 if (!resp.success) {
            //                     failedTokens.push(registrationTokens[idx]);
            //                 }
            //             });
            //             console.log('List of tokens that caused failures: ' + failedTokens);
            //         }
            //         else{
            //             console.log('Successfully sent message to all tokens');
            //         }
            //     });

        }

        catch (err) {
            console.log("For sending user added notification, the err:" + err)
            // res.status(400).send(err)
        }




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

    testMessageSyntax : (thetoken) => {
        
        thetoken.forEach(tokss => {
        const message = {
            notification: { 
                title: 'notification works', 
                body: 'my bodddyyyyyy big big body' 
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
                else{
                    console.log('Successfully sent message to all tokens');
                }
            }); 
        });
        
    
    },

    // sendMessageNotification : (stff,stuff2) = {
    // need to do this for message notifications
    // },
    
    // rn, tokens are not saved for the users, results in error: "Exactly one of topic, token or condition is required"
    sendPrivateMessageNotification: (senderName, receiverID) => {

        try {
            let regToken = (userCollection.findOne({ userID: receiverID }).registrationToken)
                const message = {
                    notification: { 
                        title: 'New message from ' + senderName, 
                        body: 'You have a new message', 
                    },
                    token: regToken, // is this correct? @abhishek
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
                    else{
                        console.log('Successfully sent message to a user : ' + student.userID);
                    }
                });
        }

        catch (err) {
            console.log("sendPrivateMessageNotification error: " + err)
            // res.status(400).send(err)
        }

    },
};