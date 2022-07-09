const {MongoClient} = require("mongodb")
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)
client.connect()

const authUtils = require('../utils/authUtils.js')

let dbUser, userCollection

dbUser = client.db("user")
userCollection = dbUser.collection("userCollection")

module.exports ={
    signup: async (req, res) => {
      try {
          let email = req.body.email;
          let password = req.body.password;
          let username = req.body.username;
          authUtils.signUserUp(email,password,username)
          return res.status(200).json({ success: true})
      } catch (error) {
          console.log(error)
          return res.status(400).json({ success: false, error })
      }
      
    },
    confirmSignUp: async (req, res) => {
      try {
          let username = req.body.username;
          let confirmationCode = req.body.confirmationCode;
          authUtils.confrimSignUP(username,confirmationCode)
          return res.status(200).json({ success: true})
      } catch (error) {
          console.log(error)
          return res.status(400).json({ success: false, error })
      }
      
    },

   login: async (req, res) => {
      try {
          let username = req.body.username;
          let password = req.body.password;
          authUtils.login(username, password)
          return res.status(200).json({ success: true})
      } catch (error) {
          console.log(error)
          return res.status(400).json({ success: false, error })
      }
      
    },

    resendConfirmationCode: async (req, res) => {
      try {
          let username = req.body.username;
          authUtils.resendConfrimationCode(username)
          return res.status(200).json({ success: true})
      } catch (error) {
          console.log(error)
          return res.status(400).json({ success: false, error })
      }
      
    },
    getUserProfile: async (req, res) => {
        try {
            userCollection.find({userID: req.params.userID}).toArray((err, userProfileResult) => {
                if (err) {
                  console.error(err)
                  res.status(500).json({ err: err })
                  return
                }
                res.status(200).json(userProfileResult)
              })
        } catch (error) {
            console.log(error)
            return res.status(500).json({ success: false, error })
        }
        
    },

    getCourseList: async (req, res) => {
        try {
            userCollection.find({userID: req.params.userID}).project({courselist:1, _id:0}).toArray((err, resultcourse) => {
                if (err) {
                  console.error(err)
                  res.status(500).json({ err: err })
                  return
                }
                res.status(200).json(resultcourse)
              })
        } catch (error) {
            console.log(error)
            return res.status(500).json({ success: false, error })
        }
        
    },

    createProfile : (req, res) => {
        var courselistarr = [];
        var blockeruserarr = [];
      userCollection.insertOne(
        {
          displayName: req.body.displayName,
          userID: req.body.userID,
          coopStatus: req.body.coopStatus,
          yearStanding: req.body.yearStanding,
          registrationToken: req.body.registrationToken,
          courselist: courselistarr,
          blockedUser: blockeruserarr,
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
    },

    block: (req, res) => {
        userCollection.updateOne({"userID": req.body.userID}, {$push:{"blockedUser":req.body.blockedUserAdd}},(err, result)=>{
            if (err) {
                console.error(err)
                res.status(500).json({ err: err })
                return
            }
            res.status(200).json({ ok: true })
        });
    }, 

    getDisplayNamebyUserID: async (userID) => {
      try {
        let retrievedUser = await userCollection.findOne({"userID": userID})
        return retrievedUser.displayName
      } catch (error) {
          console.log(error)
          return error
      }
      
  }

}
