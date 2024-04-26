const WebSocket = require('ws');

const ws = new WebSocket('ws://localhost:8888');

ws.on('open', function open() {
    console.log('WebSocket connection opened');
});

ws.on('message', function incoming(data) {
    try {
        const locationData = JSON.parse(data);
        console.log('Received location data:', locationData);
    } catch (error) {
        console.error('Error parsing location data:', error);
    }
});

ws.on('close', function close() {
    console.log('WebSocket connection closed');
});

ws.on('error', function error(error) {
    console.error('WebSocket error:', error);
});
