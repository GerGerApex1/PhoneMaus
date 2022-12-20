const configFile = require('../../config.json')
const dotenv = require('dotenv')
function getConfig() {
    return configFile
}
function initEnv() {
    dotenv.config({ path: '../../.env'})
}
module.exports = {
    getConfig,
    initEnv
}