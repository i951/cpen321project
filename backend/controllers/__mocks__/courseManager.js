// Create  a  mock  for  any  of  your  back-end  modules 
// (https://jestjs.io/docs/en/mockfunctions). That is, you 
// should create mocks for all interfaces exposed by this 
// module.  For  now,  each  mock  should  return  a 
// predefined  value of  the  right  return type,  as  
// expected from the corresponding interface.  
// There is no need to mock databases and APIs of 
// external components. Implement a test suite that 
// triggers each of the mocks.  

// Mocking Courses Module Interfaces (from courseManger.js):
// getStudentList
// addUserToCourse
// addCourseToUser
// deleteUserFromCourse
// deleteCourseFromUser

module.exports = {
    // getStudentList : async (req, res) => {
    //     try {
    //         var coursenamespace = req.params.coursename.substring(0,4)+ " "+req.params.coursename.substring(4,req.params.coursename.length)
    //         dbCourse.collection(coursenamespace).find({}).project({userID:1, displayName:1, _id:0}).toArray((err, resultstudent) => {
    //           if (err) {
    //             console.error(err)
    //             res.status(500).json({ err: err })
    //             return
    //           }
    //           res.status(200).json(resultstudent)
    //         })
    //     } catch (error) {
    //         console.log(error)
    //         return res.status(500).json({ success: false, error })
    //     }
    // },
    getStudentList: jest.fn( async (courseNameNoSpace)=> {
        let courseNameSpace = courseNameNoSpace.substring(0,4)+ " "+courseNameNoSpace.substring(4,courseNameNoSpace.length)
            if (courseNameSpace === null) {
                return({success: false})
            } else if (courseNameSpace === "") {
                return({err: "error"})
            } else if (courseNameSpace === "CPEN 321") {
                return ([
                    {
                        userID: "john",
                        displayName: "john",
                    },
                    {
                        userID: "mary",
                        displayName: "mary",
                    },
                ])
            }
      }),

    // addUserToCourse :  async (req, res) => {
    //     try{
    //         await dbCourse.collection(req.body.coursename).insertOne({
    //             displayName: req.body.displayName,
    //             userID: req.body.userID,
    //             })
    //         res.status(200).send("User added successfully\n")
    //         notificationManager.userAddedNotification(req.body.userID, req.body.coursename)
    //     }
    //     catch(err){
    //         console.log(err)
    //         res.status(400).send(err)
    //     }
    // }, 
    addUserToCourse:  async (req, res) => {
        let displayName = req.body.displayName
        let userID = req.body.userID

        if (displayName && userID) {
            res.status = 200
            res.json = "User added successfully\n"
        } else {
            res.status = 400
            res.json = "error"
        }
    }, 

    // addCourseToUser : async (req, res) => {
    //     userCollection.updateOne({"userID": req.body.userID}, {$push:{"courselist":req.body.coursename}},(err, result)=>{
    //         if (err) {
    //             console.error(err)
    //             res.status(500).json({ err: err })
    //             return
    //         }
    //         res.status(200).json({ ok: true })
    //     });
    // },
    addCourseToUser : async (req, res) => {
        let userID = req.body.userID
        let courselist = req.body.coursename

        if (userID && courselist) {
            res.status = 200
            res.json = { ok: true }
        } else {
            res.status = 500
            res.json = { err: "error" }
        }
    },

    // deleteUserFromCourse : async (req, res) => {
    //     try{
    //       var coursenamespace = req.params.coursename.substring(0,4)+ " "+req.params.coursename.substring(4,req.params.coursename.length)
    //         await 
    //         dbCourse.collection(coursenamespace).deleteOne({"userID": req.params.userID})
    //         res.status(200).send("User deleted successfully\n")
    //     }
    //     catch(err){
    //         console.log(err)
    //         res.status(400).send(err)
    //     }
    // },
    deleteUserFromCourse : async (req, res) => {
        let coursenamespace = req.params.coursename.substring(0,4)+ " "+req.params.coursename.substring(4,req.params.coursename.length)
        let userID = req.body.userID

        if (userID && coursenamespace) {
            res.status = 200
            res.json = "User deleted successfully\n"
        } else {
            res.status = 400
            res.json = "error"
        }
    },

    // deleteCourseFromUser : async (req, res) => {
    //     try{
    //         await 
    //         userCollection.updateMany({"userID": req.body.userID},{$pull: {"courselist": req.body.coursename}})
    //         res.status(200).send("Course deleted successfully\n")
    //     }
    //     catch(err){
    //         console.log(err)
    //         res.status(400).send(err)
    //     }
    // },
    deleteCourseFromUser : async (req, res) => {
        let userID = req.body.userID
        let courselist = req.body.coursename

        if (userID && courselist) {
            res.status = 200
        res.json = "Course deleted successfully\n"
        } else {
        res.status = 400
        res.json = "error"
        }
    }
}