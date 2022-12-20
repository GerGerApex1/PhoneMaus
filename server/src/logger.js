const chalk = require('chalk')
function debug(...text) {
    const arrayOfText = text.join().split('\n')
    arrayOfText.forEach((element) => {
        console.log(chalk.green("[DEBUG]: ") + element)
    })
}
function success(...text) {
    const arrayOfText = text.join().split('\n')
    arrayOfText.forEach((element) => {
        console.log(chalk.blue("[SUCCESS] ") + element)
    });
}
function error(...text) {
    const arrayOfText = text.join().split('\n')
    arrayOfText.forEach((element) => {
        console.log(chalk.red("[ERROR] ") + element)
    })
}
function info(...text) {
    const arrayOfText = text.join().split('\n')
    arrayOfText.forEach((element) => {
        console.log(chalk.blueBright("[INFO] ") + element)
    })
}
module.exports = {
    debug, success, error, info,
}