const { error } = require("./logger")
const os = require('os');
const { json } = require("express");
const ip4vRegex = /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$/
const jsonRegex = /^\s*(\{(?:"[^"]+"|'[^']+'|[^\{\}\[\]\\"]|\\["\\\/bfnrt]|\\u[0-9a-fA-F]{4})*\}|\[[^\[\]\\]*(?:\\[\s\S][^\[\]\\]*)*\])\s*$/;
const seperateData = (message) => message.split(',')

function getIP(config) {
    if(config.serverConfig.ip === "DEFAULT_IP") {
        const interface = os.networkInterfaces()
        let computer_address = interface["Wi-Fi"][0].address
        if(!ip4vRegex.test(computer_address)) {
            computer_address = interface["Wi-Fi"][1].address
        }
        return computer_address
    } else {  
        const validIp = ip4vRegex.test(config.serverConfig.ip)
        if(validIp === false) {
            error(`Config Error: ${config.serverConfig.ip} is not a valid IP. Change it on your config.json to make the server runnable.`)
            return process.exit(1)
        }
        return configFile.serverConfig.ip
    }
}
function isJson(text) {
    jsonRegex.test(text)
}
module.exports = {
    seperateData,
    getIP,
    isJson
}