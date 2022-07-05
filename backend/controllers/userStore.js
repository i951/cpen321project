const {MongoClient} = require("mongodb")
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)
client.connect()

let dbUser, userCollection

dbUser = client.db("user")
userCollection = dbUser.collection("userCollection")

module.exports ={
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
          courselist: courselistarr,
          blockedUser: blockeruserarr
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
    }



}
