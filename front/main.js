$(document).ready(function () {
    $("#list_message").height($(window).height() - 110);
    $(window).resize(function () {
        $("#list_message").height($(window).height() - 110);
        $("#list_message").scrollTop($("#list_message")[0].scrollHeight);
    });

});

$("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");

});


$('html').on('shown.bs.dropdown', function () {
    //if($("#pseudonyme").hasClass("active"))
    $("#pseudonyme").toggleClass("active");
});

$('html').on('hide.bs.dropdown', function () {
    if ($("#pseudonyme").hasClass("active"))
        $("#pseudonyme").toggleClass("active");

});


$("#zonemessage input").prop('disabled', true);


var app = angular.module('chat', []);


/* Connexion to the bridge*/

app.value('eventBus', new vertx.EventBus("https://localhost:4443/"));

app.controller('channelCtrl', ['eventBus', '$scope', '$http', '$timeout', function (eventBus, $scope, $http, $timeout) {

    $scope.userToken = "";

    $scope.showLogin = true;

    $scope.init = function () {

        $http.get("https://localhost:4443/channels").then(function (response) {
            $scope.channels = response.data;
            $scope.countChannels = Object.keys($scope.channels).length;
        });


        eventBus.onopen = function () {
            console.log("Event bus connected !");
            eventBus.registerHandler('channels', function (object) {

                var channel = ({
                    "id_channel": object.id_channel,
                    "name": object.name.toLowerCase(),
                    "ownerid": object.ownerid
                });

                $scope.channels[$scope.countChannels] = channel;
                $scope.countChannels += 1;
                $scope.$apply();
            });


        };
    };

    $scope.isActive = function (item) {
        return $scope.selected === item;
    };

    $scope.connection = function () {
        $http.post("https://localhost:4443/connect/" + $scope.name_login + "/" + $scope.password_login).then(function (response) {
            $scope.userToken = response.data.token;
            if ($scope.userToken != "null") {
                $scope.showLogin = false;
            } else {
                alert("Invalid Login or Password");
            }
        });
    };


    $scope.choiceChannel = function (channel) {
        $scope.title = channel.name;
        $scope.selected = channel;
        $("#zonemessage input").prop('disabled', false);

        $http.get("https://localhost:4443/message/" + channel.id_channel).then(function (response) {
            $http.get("https://localhost:4443/users").then(function (response2) {
                $scope.messages = response.data;
                $scope.users = (response2.data);

                $timeout(function () {
                    var scroller = document.getElementById("list_message");
                    scroller.scrollTop = scroller.scrollHeight;
                }, 0, false);

                var i = 0;
                for (; i < Object.keys($scope.messages).length; i++) {
                    $scope.messages[i].author = getName($scope.messages[i].author_id);
                }

            });

        });


        eventBus.registerHandler("channel" + $scope.selected.id_channel, function (object) {
            if (object.channelid == $scope.selected.id_channel) {
                var index = Object.keys($scope.messages).length;
                $scope.messages[index] = object;
                $scope.messages[index].author = getName(object.authorid);
                $scope.$apply();
                $("#list_message").scrollTop($("#list_message")[0].scrollHeight);
            }
        });


    }

    $scope.newMessage = function () {
        $http.put("https://localhost:4443/message/" + $scope.userToken + "/" + $scope.selected.id_channel + "/" + $scope.user_message);
        $scope.user_message = '';
    }

    function getName(id) {
        var i = 0;
        for (; i < Object.keys($scope.users).length; i++) {
            if ($scope.users[i].id == id)
                return $scope.users[i].name;
        }
        return "Undefine";
    }

    $scope.newChannel = function () {
        $http.put("https://localhost:4443/channel/" + $scope.userToken + "/" + $scope.name_channel.toLowerCase());
        $('#modalNewChannel').modal('hide');
        $scope.name_channel = '';
    }


}]);
