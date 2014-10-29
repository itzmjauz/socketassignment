readl = require('readline')
net   = require('net')
HOST  = 'wolkje-69.cs.vu.nl'
PORT  = 5378

function ask(){
  rl = readl.createInterface({
    input : process.stdin,
    output: process.stdout
  })

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
  if(data=="BAD-RQST-HDR\n") {
    process.stdout.write("Bad Header\n")
    client.destroy()
  }
  else if(data=="BAD-RQST-BODY\n"){
    process.stdout.write("Bad Body\n")
    client.destroy()
  }
  else if(data=="IN-USE\n"){
    process.stdout.write("Name already in use, please retry\n")
    ask()
  }
  else {
    process.stdout.write(''+data)
    if(data!="SEND-OK\n") ask()
  }
})

client.on('close', function() {
  console.log('Connection closed')

})
