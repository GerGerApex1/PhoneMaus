const mouse_event = require('../index')
let i = 1;
while (i < 20) {
    console.log(i)
    i++
    const idk = i * 0.3
    e(idk, idk)
}
function e(y, x) {
    mouse_event(y, x)
    console.log('Should moved your mouse in y = 20, x = 20')
}