jest.mock("../controllers/courseManager")
const courseManager = require("../controllers/courseManager")

describe("courseManager tests", () => {

  it("triggers getStudentList", async () => {
    expect.assertions(1)
    let courseNameNoSpace = "CPEN321"
    const receivedResult = await courseManager.getStudentList(courseNameNoSpace)
    return expect(receivedResult).toEqual(
      [
        {
          userID: "john",
          displayName: "john",
        },
        {
          userID: "mary",
          displayName: "mary",
        },
      ]
    )
  })

  it("triggers addUserToCourse", async () => {
    expect.assertions(2)
    const req = {
      body: { displayName: "john", userID: "john"}
    }
    let res = {}
    await courseManager.addUserToCourse(req, res)
    expect(res.status).toEqual(200)
    expect(res.json).toEqual("User added successfully\n")
  })

  it("triggers addCourseToUser", async () => {
    expect.assertions(2)
    const req = {
      body: { userID: "john", coursename: "CPEN 321" }
    }
    let res = {}
    await courseManager.addCourseToUser(req, res)
    expect(res.status).toEqual(200)
    expect(res.json).toEqual({ ok: true })
  })

  it("triggers deleteUserFromCourse", async () => {
    expect.assertions(2)
    const req = {
      params: { coursename: "CPEN 321"},
      body: { userID: "john" }
    }
    let res = {}
    await courseManager.deleteUserFromCourse(req, res)
    expect(res.status).toEqual(200)
    expect(res.json).toEqual("User deleted successfully\n")
  })

  it("triggers deleteCourseFromUser", async () => {
    expect.assertions(2)
    const req = {
      body: { userID: "john", coursename: "CPEN 321" }
    }
    let res = {}
    await courseManager.deleteCourseFromUser(req, res)
    expect(res.status).toEqual(200)
    expect(res.json).toEqual("Course deleted successfully\n")
  })

})