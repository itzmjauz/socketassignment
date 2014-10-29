prompt = require('prompt')
net   = require('net')
HOST  = 'wolkje-69.cs.vu.nl'
PORT  = 5378

client = new net.Socket()
client.connect(PORT, HOST, function() {
  connected = true

  console.log('CONNECTED TO: ' + HOST + ':' + PORT)
  prompt.start()
  promptOn()
})

client.on('data', function(data) {
  if(data=="BAD-RQST-HDR") {
    console.log("Bad Header")
    client.destroy()
  }
  if(data=="BAD-RQST-BODY"){
    console.log("Bad Body")
    client.destroy()
  }
  if(data=="IN-USE"){
    console.log("Name already in use, please retry")
    client.destroy()
  }
  else {
    console.log(''+data)
    promptOn()
  }
})

function promptOn(){
  if(connected==false) client.destroy()
  else {
    prompt.get(['msg'], function(err,result){
      client.write(result.msg+'\n')
      promptOn()
    })
  }
}

client.on('close', function() {
  connected=false
  console.log('Connection closed')
})
