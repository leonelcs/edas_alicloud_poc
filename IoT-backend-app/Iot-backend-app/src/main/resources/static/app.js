var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/ec-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnect, onError);
    showGreeting("testing123");
}

function onConnect(frame) {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/functions/testresponse', function () {
    	console.log("got test response");
    });
    
    stompClient.subscribe('/functions/goodresponse', function () {
    	console.log("got good response");
    });
    
    ///functions/update/100/100/101/1002//mclimate/020000001019/3
    
    stompClient.subscribe('/functions/update/*/*/*/*//*/*/*', function (message) {
    	console.log("got function update");
    	console.log(message.headers.destination, message.body);
    	showGreeting(message.headers.destination, message.body);
    });
    
    //stompClient.send("/app/hello", {}, JSON.stringify({'name': "martin"}));
}

function onError(error) {
	console.log(error);
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    //stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
	
	/*console.log('Connecting to : ' + $("#name").val());
    stompClient.subscribe('/', function (greeting) {
        showGreeting(JSON.parse(greeting.body).content);
    });*/
    
}

function showGreeting(mapping, message) {
	console.log("got something!");
	console.log(message);
    $("#greetings").append("<tr><td>" + mapping + ": " + message + "</td></tr>");
}

function doGood() {
	console.log("Doing good, this should work");
	stompClient.send("/app/good", {}, JSON.stringify({'name': "martin"}));
}

function doTest() {
	console.log("Doing test, this should work, eventually...");
	stompClient.send("/app/test", {}, JSON.stringify({'name': "martin"}));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#goodbutton" ).click(function() { doGood(); });
    $( "#testbutton" ).click(function() { doTest(); });
});