const configHandler = require('./src/config')
configHandler.initEnv()
const config = configHandler.getConfig()

const os = require('os');
const chalk = require('chalk');

const WS = require("./src/server");
const { info, debug } = require("./src/logger");
const moveMouse = require("./src/moveMouse");
const { ACCELEROMETER, GYROSCOPE, LINEAR_ACCELEROMETER, ROTATION_VECTOR } = require("./src/types");

console.clear()

const interface = os.networkInterfaces()
const server = new WS(8080, interface["Wi-Fi"][0].address)
const seperateData = (message) => message.split(',')
let MOUSE_SENS = 0.5
let MOUSE_ADDITION = 0.650

server.onMessage(async function (data) {
    const string = seperateData(data.toString())
    switch (string[0]) {
        case ACCELEROMETER:
            const x = -+string[1] + MOUSE_ADDITION * MOUSE_SENS
            const y = -+string[2] + MOUSE_ADDITION * MOUSE_SENS
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
    const seperate1 = " ".repeat(15 - string[1].length)
    const seperate2 = " ".repeat(20 - string[2].length)
    debug(`${chalk.redBright(string[1])}${seperate1}${chalk.greenBright(string[2])}${seperate2}${chalk.blueBright(string[3])}`)
})