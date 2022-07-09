const AWS = require('aws-sdk')
var express = require("express")
const AmazonCognitoIdentity = require('amazon-cognito-identity-js');
const AWS_ACCESS_KEY_ID = "AKIATNLYKFHXVBMV5DOO"
const AWS_SECRET_ACCESS_KEY = "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"
const USERPOOLID = "us-west-2_LJltK6RSZ"
const CLIENTID = "7docu6553pf9vi5ipv1ig1huns"
const userPool = new AmazonCognitoIdentity.CognitoUserPool({
    UserPoolId: USERPOOLID,
    ClientId: CLIENTID
});

//Update AWS configuration with the correct credentials and region
AWS.config.update({
    credentials: {
        accessKeyId: AWS_ACCESS_KEY_ID,
        secretAccessKey: AWS_SECRET_ACCESS_KEY
    },
    region: "us-west-2"
})
const cognito = new AWS.CognitoIdentityServiceProvider({
    apiVersion: "2016-04-18",
})


exports.signUserUp = (email, password, username) => {
    // Signs the user up and sends a confirmation code to the provided email
    return new Promise ((resolve, reject) => {
        const cognitoParams = {
            "ClientId": CLIENTID,
            "Password": password,
            "UserAttributes": [
                {
                    "Name": "email",
                    "Value": email
                }
            ],
            "Username": username,
        }
        cognito.signUp(cognitoParams, function (err, res) {
            if (err)
                reject(err)    // Example error codes: InvalidPasswordException, InvalidParameterException (invalid email address)
            resolve(res)
        })
    })
}

exports.confrimSignUP = (username, confirmationCode) => {
    // Changes the status of the user to CONFIRMED in AWS cognito
    return new Promise ((resolve, reject) => {
        const cognitoParams = {
            "ClientId": CLIENTID,
            "ConfirmationCode": confirmationCode,
            "Username": username
        }
        cognito.confirmSignUp(cognitoParams, (err, res) => {
            if (err) {
                reject(err) // Example error codes: ExpiredCodeException (expired or incorrect or invalid username), InvalidParameterType (invalid email)
            }
            resolve(res)
        })
    })

}


exports.login = (username, password) => {
    return new Promise((resolve, reject) => {
        var userData = {
            Username: username,
            Pool: userPool,
        }
        var authenticationDetails = new AmazonCognitoIdentity.AuthenticationDetails({
            Username: username,
            Password: password
        });
        var cognitoUser = new AmazonCognitoIdentity.CognitoUser(userData)
        cognitoUser.authenticateUser(authenticationDetails, {
            onSuccess: function (result) {
                var accessToken = result.getAccessToken().getJwtToken();
                resolve(accessToken)
            },
            onFailure: (function (err) {
                reject(err)
            })
        })
    })

}

exports.resendConfrimationCode = async (username) => {
    // Resened confirmation code in case the previous confirmation code was expired
    cognitoParams = {
        "ClientId": CLIENTID,
        "Username": username
    }
    cognito.resendConfirmationCode(cognitoParams, (err, res) => {
        if (err) {
            throw (err)
        }
    })
} 

exports.confirmAccessToken = (accessToken) => {
    return new Promise((resolve, reject) => {

        cognito.getUser({ "AccessToken": accessToken}, (err, res) => {
            if (err){
                console.log(err)
                reject(err) //Example err code: NotAuthorizedException
            }
            console.log(res)
            resolve(res) 
        })
    })
}



