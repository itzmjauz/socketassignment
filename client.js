readl = require('readline')
net   = require('net')
HOST  = 'wolkje-69.cs.vu.nl'
PORT  = 5378

rl = readl.createInterface({
  input : process.stdin,
  output: process.stdout
})

function ask(){
  rl.question("Message: ", function(answer){
    rl.close()
    client.write(answer+'\n')
  })
}

client = new net.Socket()
client.connect(PORT, HOST, function() {
  process.stdout.write('CONNECTED TO: ' + HOST + ':' + PORT+'\n')
  ask()
})

client.on('data', function(data) {
  if(data=="BAD-RQST-HDR") {
    process.stdout.write("Bad Header")
    client.destroy()
  }
  if(data=="BAD-RQST-BODY"){
    process.stdout.write("Bad Body")
    client.destroy()
  }
  if(data=="IN-USE"){
    process.stdout.write("Name already in use, please retry")
    client.destroy()
  }
  else {
    process.stdout.write(''+data)
    ask()
  }
})


client.on('close', function() {
  console.log('Connection closed')
})
