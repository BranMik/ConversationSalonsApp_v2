const functions = require("firebase-functions");

const admin = require("firebase-admin");
admin.initializeApp();

exports.notificationNewConversationTopic = functions.firestore
    .document("/conversations/{documentId}")
    .onCreate((snap, context) => {
      const cTitle = snap.data().title;
      const cAuthor = snap.data().authorUID;
      functions.logger.log("Notif try", context.params.documentId, cTitle);
      functions.logger.log("Notif try", context.params.documentId, cAuthor);
      const payload = {
        notification: {
          title: "Conversation Salons",
          body: "There is a new Conversation posted : " + cTitle,
          sound: "default",
        },
        data: {
          createdBy: cAuthor,
          conversationTitle: cTitle,
          fragmentToOpen: "Conversations",
        },
      };
      const options = {
        priority: "high",
        timeToLive: 60*60*2,
      };
      return admin.messaging()
          .sendToTopic("NEW_CONVERSATION_TOPIC", payload, options)
          .then(function(response) {
            console.log("Successfully sent message: ", response);
            console.log(response.results[0].error);
            functions.logger.log("Notif try", "SUCCESS");
          })
          .catch(function(error) {
            console.log("Error sending message: ", error);
            functions.logger.log("Notif try", "ERROR");
          });
    });
exports.notificationNewAnnouncement = functions.firestore
    .document("/announcements/{documentId}")
    .onCreate((snap, context) => {
      const aTitle = snap.data().title;
      const aAuthor = snap.data().authorUID;
      functions.logger.log("Notif try", context.params.documentId, aTitle);
      functions.logger.log("Notif try", context.params.documentId, aAuthor);
      const payload = {
        notification: {
          title: "Conversation Salons",
          body: "There is a new announcement posted : " + aTitle,
          sound: "default",
        },
        data: {
          createdBy: aAuthor,
          announcementTitle: aTitle,
          fragmentToOpen: "Announcements",
        },
      };
      const options = {
        priority: "high",
        timeToLive: 60*60*2,
      };
      return admin.messaging().sendToTopic("NEW_ANNOUNCEMENT", payload, options)
          .then(function(response) {
            console.log("Successfully sent message: ", response);
            console.log(response.results[0].error);
            functions.logger.log("Notif try", "SUCCESS");
          })
          .catch(function(error) {
            console.log("Error sending message: ", error);
            functions.logger.log("Notif try", "ERROR");
          });
    });
exports.notificationNewInvitation = functions.firestore
    .document("/invitations/{documentId}")
    .onCreate((snap, context) => {
      const iTitle = snap.data().title;
      const iAuthor = snap.data().authorUID;
      functions.logger.log("Notif try", context.params.documentId, iTitle);
      functions.logger.log("Notif try", context.params.documentId, iAuthor);
      const payload = {
        notification: {
          title: "Conversation Salons",
          body: "There is a new invitation posted : " + iTitle,
          sound: "default",
        },
        data: {
          createdBy: iAuthor,
          invitationTitle: iTitle,
          fragmentToOpen: "Invitations",
        },
      };
      const options = {
        priority: "high",
        timeToLive: 60*60*2,
      };
      return admin.messaging().sendToTopic("NEW_INVITATION", payload, options)
          .then(function(response) {
            console.log("Successfully sent message: ", response);
            console.log(response.results[0].error);
            functions.logger.log("Notif try", "SUCCESS");
          })
          .catch(function(error) {
            console.log("Error sending message: ", error);
            functions.logger.log("Notif try", "ERROR");
          });
    });
exports.notificationNewRecommendation = functions.firestore
    .document("/recommendations/{documentId}")
    .onCreate((snap, context) => {
      const rTitle = snap.data().title;
      const rAuthor = snap.data().authorUID;
      functions.logger.log("Notif try", context.params.documentId, rTitle);
      functions.logger.log("Notif try", context.params.documentId, rAuthor);
      const payload = {
        notification: {
          title: "Conversation Salons",
          body: "There is a new recommendation posted : " + rTitle,
          sound: "default",
        },
        data: {
          createdBy: rAuthor,
          announcementTitle: rTitle,
          fragmentToOpen: "Announcements",
        },
      };
      const options = {
        priority: "high",
        timeToLive: 60*60*2,
      };
      return admin.messaging()
          .sendToTopic("NEW_RECOMMENDATION", payload, options)
          .then(function(response) {
            console.log("Successfully sent message: ", response);
            console.log(response.results[0].error);
            functions.logger.log("Notif try", "SUCCESS");
          })
          .catch(function(error) {
            console.log("Error sending message: ", error);
            functions.logger.log("Notif try", "ERROR");
          });
    });
exports.notificationNewArticle = functions.firestore
    .document("/articles/{documentId}")
    .onCreate((snap, context) => {
      const arTitle = snap.id;
      functions.logger.log("Notif try", context.params.documentId, arTitle);
      const payload = {
        notification: {
          title: "Conversation Salons",
          body: "There is a new article posted : " + arTitle,
          sound: "default",
        },
        data: {
          articleTitle: arTitle,
          fragmentToOpen: "Articles",
        },
      };
      const options = {
        priority: "high",
        timeToLive: 60*60*2,
      };
      return admin.messaging().sendToTopic("NEW_ARTICLE", payload, options)
          .then(function(response) {
            console.log("Successfully sent message: ", response);
            console.log(response.results[0].error);
            functions.logger.log("Notif try", "SUCCESS");
          })
          .catch(function(error) {
            console.log("Error sending message: ", error);
            functions.logger.log("Notif try", "ERROR");
          });
    });
