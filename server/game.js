var gameInfo = {};
gameInfo.gameName = "Pong";
gameInfo.dexFileName = "Pong.dex";
gameInfo.className = "pong.Pong";
gameInfo.downloadPath = "https://ancient-gorge-73625.herokuapp.com/downloads/23af5bf8b9e3037a7675cd3c2a2e6761f5ad0a1f70dc12a66070eda2ef060b71";
gameInfo.maxGamePlayer = 2;
gameInfo.minGamePlayer = 2;
var game = {};
var newGame = function(roomId,player1Id,player2Id){
    game[roomId] = {};
    game[roomId].player1 = {id:player1Id,isReady:false,pos:{x:20,y:480/2}};
    game[roomId].player2 = {id:player2Id,isReady:false,pos:{x:770,y:480/2}};
    game[roomId].ballPos = {x:400,y:240};
    game[roomId].ballSpeed = 10;
    game[roomId].transferredData = {};
    game[roomId].ballDir = {x:0,y:0};
    game[roomId].ballDir.x = randomIntFromInterval(-10,10);
    game[roomId].ballDir.y = game[roomId].ballDir.x/randomIntFromInterval(-5,5);
    game[roomId].ballDir = normalize(game[roomId].ballDir);
    game[roomId].ballDir.x*=game[roomId].ballSpeed;
    game[roomId].ballDir.y*=game[roomId].ballSpeed;
    game[roomId].winner = 0;
}
var resetGame = function(roomId){
    game[roomId] = {};
    game[roomId].player1.pos = {x:20,y:480/2};
    game[roomId].player2.pos = {x:770,y:480/2};
    game[roomId].ballPos = {x:400,y:240};
    game[roomId].transferredData = {};
    game[roomId].ballDir.x = randomIntFromInterval(-10,10);
    game[roomId].ballDir.y = game[roomId].ballDir.x/randomIntFromInterval(-5,5);
    game[roomId].ballDir = normalize(game[roomId].ballDir);
    game[roomId].ballDir.x*=game[roomId].ballSpeed;
    game[roomId].ballDir.y*=game[roomId].ballSpeed;
    game[roomId].winner = 0
}
var players = {};
//playerHeight
var racketHeight = 100;
var randomIntFromInterval = function(min,max)
{
    var num = Math.floor(Math.random() * (max - min + 1)) + min;
    return (num === 0) ? randomIntFromInterval(min, max) : num;
}
var hitFactor = function(ballPos,racketPos,racketHeight) {
    // ascii art:
    // ||  1 <- at the top of the racket
    // ||
    // ||  0 <- at the middle of the racket
    // ||
    // || -1 <- at the bottom of the racket
    return (ballPos.y - racketPos.y) / racketHeight;
}
var normalize = function(vector) {
    var norm = Math.sqrt(vector.x * vector.x + vector.y * vector.y);
    vector.x = vector.x / norm;
    vector.y = vector.y / norm;

    return {x:vector.x,y:vector.y};
}
var calcBallPos = function(roomId){
    if(game[roomId].ballPos.y <= 10 || game[roomId].ballPos.y >= 460){
        //hit top or bottom -> bounce off
        game[roomId].ballDir.y*=-1;
    }
    if(game[roomId].ballPos.x <= 0 || game[roomId].ballPos.x >= 800){
        game[roomId].ballDir.x*=-1;
    }
    game[roomId].ballPos.x += game[roomId].ballDir.x;
    game[roomId].ballPos.y += game[roomId].ballDir.y;
    if(game[roomId].ballPos.x <= 10){
        game[roomId].winner = 2;
    }
    if(game[roomId].ballPos.x >= 776){
        game[roomId].winner = 1;
    }
    game[roomId].transferredData = game[roomId].ballPos;
}
var calcBallDir = function(roomId){
    var y = 0;
    var dir = {};
    if(game[roomId].ballPos.x < 400){
        y = hitFactor(game[roomId].ballPos,game[roomId].player1.pos,racketHeight);
        dir = {x:1,y:y};
    }else{
        y = hitFactor(game[roomId].ballPos,game[roomId].player2.pos,racketHeight);
        dir = {x:-1,y:y};
    }

    // Calculate direction, make length=1 via .normalized
    //var dir = new Vector2(1, y).normalized;
    dir = normalize(dir);

    // Set Velocity with dir * speed
    game[roomId].ballDir.x = dir.x*game[roomId].ballSpeed;
    game[roomId].ballDir.y = dir.y*game[roomId].ballSpeed;
}

var updatePlayer = function(roomId,data){
    if(data.id == game[roomId].player1.id){
        game[roomId].player1.x = data.x;
        game[roomId].player1.y = data.y;
    }else{
        game[roomId].player2.x = data.x;
        game[roomId].player2.y = data.y;
    }
}

var addPlayer = function(playerId){
    players[playerId] = {};
    //ready in game, not in room
    players[playerId].isReady = false;
}
var playerReady = function(playerId){
    players[playerId].isReady = true;
}

module.exports.gameInfo = gameInfo;
module.exports.players = players;
module.exports.updatePlayer = updatePlayer;
module.exports.addPlayer = addPlayer;
module.exports.playerReady = playerReady;
module.exports.calculateBallPosition = calcBallPos;
module.exports.calculateBallDirection = calcBallDir;
module.exports.newGame = newGame;
module.exports.gameList = game;
module.exports.resetGame = resetGame;

