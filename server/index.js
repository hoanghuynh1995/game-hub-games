var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

var game = require('./game.js');

var rooms = [];

server.listen(8080, function(){
    console.log("Server is now running...");
});

io.on('connection', function(socket){
    var playerId = socket.id;
    var roomId = '123';
    game.addPlayer(playerId);
    console.log("Player Connected!");
    socket.join(roomId);
    socket.emit('connected',{name:playerId});
    //socket.emit('downloadPath',game.gameInfo.downloadPath);
    socket.on('disconnect', function(){
        console.log("Player Disconnected");
        delete game.players[playerId];
        if(io.sockets.adapter.rooms[roomId] == null){
            delete game.gameList[roomId];
        }
        rooms.forEach(function(item,index){
            if(item.hostName == playerId){
                rooms.splice(index,1);
            }
        })
    });

    socket.on('getRooms', function () {
        socket.emit('getRooms', rooms);
    });
    socket.on('removeRoom', function () {
        rooms.forEach(function (item, index) {
            if (item.hostName == playerId) {
                io.emit('removeRoom', item);
                rooms.splice(index, 1);
            }
        });
    });

    socket.on('createRoom', function (roomName) {
        var room = {};
        room.id = new Date().getTime();
        room.roomName = roomName;
        room.hostName = playerId;
        room.roomState = "waiting";
        room.gameName = game.gameInfo.gameName;
        rooms.push(room);
        socket.broadcast.emit('newRoom', room);
        socket.join(room.id);
        roomId = room.id;
        var roomInfo = {};
        roomInfo.room = room;
        roomInfo.players = io.sockets.adapter.rooms[room.id].sockets;
        socket.emit('createRoom', roomInfo);
        // socket.on('startGame',function(){
        //     io.to(room.id).emit('startGame');
        // });
    });
    socket.on('joinRoom', function (roomid) {
        if (io.sockets.adapter.rooms[roomid] != null) {
            socket.join(roomid);
            roomId = roomid;
            for (var i = 0; i < rooms.length; i++) {
                if (roomid == rooms[i].id) {
                    var roomInfo = {};
                    var r = {};
                    r.id = roomid;
                    r.roomName = rooms[i].roomName;
                    r.hostName = rooms[i].hostName;
                    r.roomState = rooms[i].roomState;
                    r.gameName = rooms[i].gameName;
                    roomInfo.room = r;
                    roomInfo.players = io.sockets.adapter.rooms[roomid].sockets;
                    socket.emit('joinRoom', roomInfo);
                    break;
                }
            }
            socket.broadcast.to(roomId).emit('newPlayer', playerId);
        }
    });

    socket.on('chat',function(data){
        socket.broadcast.to(data.roomId).emit('chat', {playerName:playerId,message:data.message});
    });

    socket.on('leaveRoom', function (roomId) {
        io.sockets.in(roomId).emit('playerLeft', playerId);
        roomId = "";
    });

    socket.on('startGame',function(){
        if(Object.keys(io.sockets.adapter.rooms[roomId].sockets).length >= game.gameInfo.minGamePlayer &&
            Object.keys(io.sockets.adapter.rooms[roomId].sockets).length <= game.gameInfo.maxGamePlayer){
            game.newGame(roomId,Object.keys(io.sockets.adapter.rooms[roomId].sockets)[0],Object.keys(io.sockets.adapter.rooms[roomId].sockets)[1]);
            io.to(roomId).emit('openGame');
        }
    });
    socket.on('playerMoved',function(data){
        data.id = socket.id;
        game.updatePlayer(roomId,data);
        socket.broadcast.emit('playerMoved',data);
    });
    socket.on('ready', function(){
        if(game.gameList[roomId].player1.id == playerId){
            game.gameList[roomId].player1.isReady = true;
        }else{
            game.gameList[roomId].player2.isReady = true;
        }
        if(!game.gameList[roomId].player1.isReady || !game.gameList[roomId].player2.isReady){
            return;
        }

        //emit game player order
        io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[0]).emit('startGame',{player:1});
        io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[1]).emit('startGame',{player:2});
        //send ball data interval
        var refreshId = setInterval(function() {
            if (io.sockets.adapter.rooms[roomId] == null) {
                clearInterval(refreshId);
            }else {
                if(game.gameList[roomId].winner != 0){
                    clearInterval(refreshId);
                    if(game.gameList[roomId].winner == 1){
                        io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[0]).emit('endGame',{win:true});
                        io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[1]).emit('endGame',{win:false});
                    }else{
                        io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[0]).emit('endGame',{win:false});
                        io.to(Object.keys(io.sockets.adapter.rooms[roomId].sockets)[1]).emit('endGame',{win:true});
                    }
                }else {
                    game.calculateBallPosition(roomId);
                    //socket.emit('ballInfo',game.gameList[roomId].transferredData);
                    io.to(roomId).emit('ballInfo', game.gameList[roomId].transferredData);
                }
            }
        }, 20);
    });
    socket.on('onCollision',function(){
        game.calculateBallDirection(roomId);
    });
    socket.on('exit',function(){
        game.newGame(roomId,Object.keys(io.sockets.adapter.rooms[roomId].sockets)[0],Object.keys(io.sockets.adapter.rooms[roomId].sockets)[1]);
    });
});