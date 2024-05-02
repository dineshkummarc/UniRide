const express = require('express');
const helmet = require('helmet');
const jwt = require('jsonwebtoken');
const path = require('path');
const WebSocket = require('ws');
const fs = require('fs');

const app = express();
app.use(express.json());
app.use(helmet());

// Secret key for JWT
const secretKey = 'your_secret_key_here';

// Middleware to verify JWT token
const verifyToken = (req, res, next) => {
    const token = req.headers['authorization'];
    if (!token) {
        console.log("No token provided");
        return res.status(401).json({ error: 'Unauthorized' });
    }

    jwt.verify(token.split(' ')[1], secretKey, (err, decoded) => {
        if (err) {
            console.log("Invalid token:", err.message);
            return res.status(401).json({ error: 'Invalid token' });
        }
        req.user = decoded;
        console.log("Token verified successfully");
        next();
    });
};

// WebSocket server
const wsServer = new WebSocket.Server({ noServer: true });

wsServer.on('connection', (ws) => {
    ws.on('message', (message) => {
        // Broadcast message to all clients
        wsServer.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(message);
            }
        });
    });
});

// HTTP endpoint to receive location data
app.post('/location', verifyToken, (req, res) => {
    // Extract location data from request body
    const { latitude, longitude, rotation, uuid } = req.body;

    // Save location data to JSON file
    const filename = path.join(__dirname, 'locations', `location_${uuid}.json`);
    const data = { latitude, longitude, rotation, timestamp: new Date() };

    // Write location data to file (replacing existing file)
    fs.writeFile(filename, JSON.stringify(data) + '\n', (err) => {
        if (err) return res.status(500).json({ error: 'Failed to save location data' });
        res.status(200).json({ message: 'Location data received successfully' });

        // Broadcast location data to WebSocket clients
        const messageToSend = JSON.stringify(data);
        wsServer.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(messageToSend);
            }
        });
    });
});

// HTTP endpoint to generate JWT token
app.get('/generateToken', (req, res) => {
    const token = jwt.sign({}, secretKey, { expiresIn: '1h' });
    res.status(200).json({ token });
});

// Create HTTP server
const server = app.listen(8888, () => {
    console.log('Server is running on port 8888');
});

// Upgrade HTTP server to WebSocket server
server.on('upgrade', (request, socket, head) => {
    wsServer.handleUpgrade(request, socket, head, (socket) => {
        wsServer.emit('connection', socket, request);
    });
});
