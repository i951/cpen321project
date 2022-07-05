const {MongoClient} = require("mongodb")
const uri = "mongodb://localhost:27017"
const client = new MongoClient(uri)
client.connect()

let dbUser, dbCourse, userCollection

dbUser = client.db("user")
dbCourse= client.db("course")
userCollection = dbUser.collection("userCollection")

module.exports = {
    getStudentList : async (req, res) => {
        try {
            var coursenamespace = req.params.coursename.substring(0,4)+ " "+req.params.coursename.substring(4,req.params.coursename.length)
            dbCourse.collection(coursenamespace).find({}).project({userID:1, displayName:1, _id:0}).toArray((err, resultstudent) => {
              if (err) {
                console.error(err)
                res.status(500).json({ err: err })
                return
              }
              res.status(200).json(resultstudent)
            })
        } catch (error) {
            console.log(error)
            return res.status(500).json({ success: false, error })
        }
        
    },

    addUserToCourse :  async (req, res) => {
        try{
            await dbCourse.collection(req.body.coursename).insertOne({
                displayName: req.body.displayName,
                userID: req.body.userID,
              })
            res.status(200).send("User added successfully\n")
        }
        catch(err){
            console.log(err)
            res.send(400).send(err)
        }
    }, 

    addCourseToUser : async (req, res) => {
        userCollection.updateOne({"userID": req.body.userID}, {$push:{"courselist":req.body.coursename}},(err, result)=>{
            if (err) {
                console.error(err)
                res.status(500).json({ err: err })
                return
            }
            res.status(200).json({ ok: true })
        });
    },

    deleteUserFromCourse : async (req, res) => {
        try{
          var coursenamespace = req.params.coursename.substring(0,4)+ " "+req.params.coursename.substring(4,req.params.coursename.length)
            await 
            dbCourse.collection(coursenamespace).deleteOne({"userID": req.params.userID})
            res.status(200).send("User deleted successfully\n")
        }
        catch(err){
            console.log(err)
            res.status(400).send(err)
        }
    },

    deleteCourseFromUser : async (req, res) => {
        try{
            await 
            userCollection.updateMany({"userID": req.body.userID},{$pull: {"courselist": req.body.coursename}})
            res.status(200).send("Course deleted successfully\n")
        }
        catch(err){
            console.log(err)
            res.status(400).send(err)
        }
    }
}
