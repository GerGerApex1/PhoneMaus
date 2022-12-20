const ws = require('ws')
const { error, info, debug } = require('./logger')
const express = require('express')
const crypto = require('crypto');
const uuid = require('uuid')
const deviceTokens = new Map()
class Server {
    constructor(port, ip, codeInterval) {
        this.expressServer = this.initiateServer(port, ip)
        this.websocket = this.createWebsocketServer();
        this.code = 100000;
        setInterval(() => {
            this.generatePairCode()
        }, codeInterval);
    }
    initiateServer(port, ip) {
        if (!port) return error('No port provided')
        if (!ip) return error('No IP provided')
        const app = express()
        const server = app.listen(port)
        app.use(express.json())
        info(`Server Created. Port: ${port}, Host: ${ip}`)
        server.on('upgrade', function (request, socket, head) {
			debug(`Incoming Websocket Stream: ${request.url} | ${request.headers.authorization} | ${deviceTokens.get(request.headers.authorization)}`)
            if (request.url === "/connectWebSocket/") {
                if (deviceTokens.get(request.headers.authorization)) {
                    info('Phone Connected. More info:')
                    info('getDeviceInfo()')
                    this.websocket.handleUpgrade(request, socket, head, function (ws) {
                        this.websocket.emit('connection', ws, socket)
                    }.bind(this))
                }
            }
        }.bind(this))
        /**
         * {
         *   code: this.code
         *   deviceInfo: 
         *   class FirstSendInfo {
                 private var appVersion: Int = BuildConfig.VERSION_CODE
                 private var appBuild: String = BuildConfig.BUILD_TYPE
                 private var deviceModel: String = Build.MODEL
                 private var deviceManufacture: String = Build.MANUFACTURER
                 private var deviceBrand: String = Build.BRAND
/            }
         * }
         */
        const pairClient = function (req, res) {
			info(`${JSON.stringify(req.body)}`)
            if (req.body.code === this.code.toString()) {
                const specialToken = this.generateUniqueCode()
                deviceTokens.set(specialToken, req.body.deviceInfo)
				console.log(deviceTokens)
                res.json({
                    status: 'OK',
                    websocketToken: specialToken
                })
            } else {
                res.json({
                    status: 'ERR'
                })
            }
        }.bind(this)
        app.post('/pairClient', pairClient) 
        server.on('connection', function (ws) {
            info('Phone Connected!')
        })
        server.on('close', function (ws) {
            info('Phone Disconnected!')
        })
        return server
    }
    createWebsocketServer() {
        const wsServer = new ws.WebSocketServer({ noServer: true })
        return wsServer
    }
    onMessage(func) {
        this.websocket.on('connection', function (ws) {
            ws.on('message', func)
        })
    }
    generatePairCode() {
        crypto.randomInt(100000, 999999, (err, n) => {
            if (err) throw err;
			this.code = n
            info(`${this.#msToSeconds(this.code)} seconds are up, New code: ${this.code}`)

        });

    }
    #msToSeconds(milliseconds) {
        return milliseconds / 1000;
      }
      
    generateUniqueCode() {
        const uniqueUuid = uuid.v4()
        return uniqueUuid
    }
    isInitialized() {
        info(this.websocket)
        return this.expressServer && this.websocket != null
    }
}
module.exports = Server