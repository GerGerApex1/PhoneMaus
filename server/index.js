const configHandler = require('./src/config')
configHandler.initEnv()
const config = configHandler.getConfig()

const os = require('os');
const chalk = require('chalk');

const WS = require("./src/server");
const { info, debug, error} = require("./src/logger");
const moveMouse = require("./src/moveMouse");
const { ACCELEROMETER, GYROSCOPE, LINEAR_ACCELEROMETER, ROTATION_VECTOR } = require("./src/types");
const { getIP, seperateData, isJson } = require('./src/utils');

console.clear()

const server = new WS(config.serverConfig.port, getIP(config), config.serverConfig.pairCodeInterval)

let MOUSE_SENS = config.mouseConfig.sensitivity
let MOUSE_ADDITION = config.mouseConfig.addedSensitivity

server.onMessage(async function (data) {
    const string = seperateData(data.toString())
    switch (string[0]) {
        case ACCELEROMETER:
            const data_x = +string[0]
            const data_y = +string[1]
            const data_z = +string[2]
            const x = -data_y + MOUSE_ADDITION * MOUSE_SENS
            const y = -data_x+ MOUSE_ADDITION * MOUSE_SENS
            moveMouse(x, y)
            break;
        /*
        case GYROSCOPE:
            desktopApp.setValue(desktopApp.gyroscope, `${string[1]}, ${string[2]}, ${string[3]}`)
            break;
        case LINEAR_ACCELEROMETER:
            desktopApp.setValue(desktopApp.linear_acceleration, `${string[1]}, ${string[2]}, ${string[3]}`)
            break;
        case ROTATION_VECTOR:
            desktopApp.setValue(desktopApp.rotation_vector, `${string[1]}, ${string[2]}, ${string[3]}`)
            break;
        default:
            break;
        */
    }
    try {
        const seperate1 = " ".repeat(25 - string[1].length)
        const seperate2 = " ".repeat(20 - string[2].length)
        debug(`${chalk.redBright(string[1])}${seperate1}${chalk.greenBright(string[2])}${seperate2}${chalk.blueBright(string[3])}`)
    } catch (err) {
        error(err)
    }

})