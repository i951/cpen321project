const admin = require("firebase-admin");
const serviceAccount = require("../serviceKey.json");
admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });     

const {MongoClient} = require("mongodb");
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)
client.connect()

let dbUser, userCollection, dbCourse

dbUser = client.db("user")
dbCourse = client.db("course")
userCollection = dbUser.collection("userCollection")


module.exports = {
    // function to initiate the notification for when a user is added to a course
    _userAddedNotification: async (userID, courseID) => {
        // trying to get the the users in a course
        let students = await dbCourse.collection(courseID).find({}).project({ userID: 1, displayName: 1, _id: 0 }).toArray();

        var take, regToken;
        let otherstudents = students.filter(data => data.userID != userID);
        theTokens = [];
        otherstudents.forEach( async student => {
            console.log("Student: "+ student.displayName)
            take = await userCollection.findOne({ userID: student.userID });
            console.log("Take= "+ take.displayName)
            regToken = take.registrationToken;
            console.log("regToken: "+ regToken);
            
            const message = {
                notification: {
                    title: 'A New User Joined ' + courseID,
                    body: 'Say Hi to the new user who just joined the course ' + courseID,
                },
                token: regToken,
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
                    } else {
                        console.log('Successfully sent message to a user : ' + student.userID);
                    }
                });
        });
    },
    get userAddedNotification() {
        return this._userAddedNotification;
    },
    set userAddedNotification(value) {
        this._userAddedNotification = value;
    },

    newRegistrationToken: async (req,res) => {
        await userCollection.updateOne({userID: req.body.userID}, {$set: {registrationToken: req.body.registrationToken}}, 
            (err, result) => {
                if (err) {
                    console.log("Could not update token for: " + req.body.userID + ", with the error: " + err)
                    res.status(400).send(err)
                } else {
                    console.error("newRegistrationToken successful")
                    res.status(200).json({ok:true})
                }
            })
        
        // resultstudent.forEach(student => {
        //     if (student.userID != userID) {
        //         userAddedNotification(student.userID, courseID)
        //     }
        // }
        // )
    },
// displayName:0, userID:1, coopStatus:0, yearStanding:0, registrationToken:1, courselist:0, blockedUsers:0

    privateMessageNotification: async (senderName, receiverID) => {
        let resultstudent = await userCollection.findOne({ userID: receiverID }) 

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
            } else {
                console.log('Successfully sent message notification to a user : ' + resultstudent.userID);
            }
        }); 
    },

    groupMessageNotification: async (senderName, groupID) => {
        var take, userToken
        let resultstudents = await dbCourse.collection(groupID).find({}).project({ userID: 1, displayName: 1, _id: 0 }).toArray(); 
        
        resultstudents.forEach( async (student) => {
            take = await userCollection.findOne({ userID: student.userID });
            console.log("Take= "+ take.displayName)
            userToken = take.registrationToken;
            console.log("userToken: "+ userToken);

            const message = {
                notification: { 
                    title: "You've got a group message from " + senderName, 
                    body: "You've got a group message notification in the course " + groupID, 
                },
                token: userToken,
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
                        console.log('Group message notification failure, token: ' + failedTokens);
                    } else {
                        console.log('Successfully sent the group message notification to a user : ' + resultstudents.userID);
                    }
                }); 
        });      
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

};