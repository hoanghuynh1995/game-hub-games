var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

var game = require('./game.js');

server.listen(8080, function(){
    console.log("Server is now running...");
});


io.on('connection', function(socket){
    var playerId = socket.id;
    var roomId = '123';
    game.addPlayer(playerId);
    console.log("Player Connected!");
    socket.join(roomId);
    socket.emit('connected',{name:playerId,gameName:game.gameInfo.gameName,dexFileName:game.gameInfo.dexFileName,
        className:game.gameInfo.className,downloadPath:game.gameInfo.downloadPath,maxGamePlayer:game.gameInfo.maxGamePlayer});
    //socket.emit('downloadPath',game.gameInfo.downloadPath);
    socket.on('disconnect', function(){
        console.log("Player Disconnected");
        delete game.players[playerId];
        if(io.sockets.adapter.rooms[roomId] == null){
            delete game.gameList[roomId];
        }
    });

    socket.on('startGame',function(){
        //io.sockets.adapter.rooms[roomId].sockets;
    });
    socket.on('playerMoved',function(data){
        data.id = socket.id;
        game.updatePlayer(roomId,data);
        socket.broadcast.emit('playerMoved',data);
    });
    socket.on('ready', function(){
        game.playerReady(playerId);
        for(var i=0;i<Object.keys(io.sockets.adapter.rooms[roomId].sockets).length;i++){
            if(game.players[Object.keys(io.sockets.adapter.rooms[roomId].sockets)[i]].isReady == false){
                return;
            }
        }
        //new game
        game.newGame(roomId,Object.keys(io.sockets.adapter.rooms[roomId].sockets)[0],Object.keys(io.sockets.adapter.rooms[roomId].sockets)[1]);
        //emit game player order
        io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[0]).emit('startGame',{player:1});
        io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[1]).emit('startGame',{player:2});
        //send ball data interval
        var refreshId = setInterval(function() {
            if(game.gameList[roomId].winner != 0){
                clearInterval(refreshId);
                if(game.gameList[roomId].winner == 1){
                    io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[0]).emit('endGame',{win:true});
                    io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[1]).emit('endGame',{win:false});
                }else{
                    io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[0]).emit('endGame',{win:false});
                    io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[1]).emit('endGame',{win:true});
                }
            }
            if (io.sockets.adapter.rooms[roomId] == null) {
                clearInterval(refreshId);
            }else {
                game.calculateBallPosition(roomId);
                //socket.emit('ballInfo',game.gameList[roomId].transferredData);
                io.to(roomId).emit('ballInfo', game.gameList[roomId].transferredData);
            }
        }, 20);
    });
    socket.on('onCollision',function(){
        game.calculateBallDirection(roomId);
    });
});
