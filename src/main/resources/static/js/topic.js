function loadTopic(topicId){
    fetch("/api/problems")
    .then(function(res){
        return res.json();
    })
    .then(function(data){
        var i=getRandomIndex(0,1);
        var userData = data[i];
        var myUserObject = {};
        myUserObject.name = userData.name;
        myUserObject.gender = userData.gender;
        myUserObject.image = userData.image;
        displayUser(myUserObject);
    })
    .catch(function(err){
        console.error("Error fetching random user:", err);
    });
}