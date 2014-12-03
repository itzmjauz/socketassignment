#!/usr/bin/python
import socket
import thread

#  var = raw_input("Please enter something: ")
#  print "you entered", var

HOST = 'wolkje-69.cs.vu.nl'
PORT = 5378

CLOSED = False
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST, PORT))


def processInput():
    print("available commands are: !who , @user, !exit")
    usrin = getInput("$: ")

    if usrin == '':
        print("Input field was empty, please retry")
        processInput()

    if usrin.startswith('!who'):
        s.send('WHO\n')
        processInput()

    if usrin.startswith('!exit'):
        print("Client terminating...")
        global CLOSED
        CLOSED = True

    if usrin.startswith('@'):
        user = usrin.split('@')[1].split(' ')[0]  # grab the wanted user.
        # grab the message using double splits and join.
        message = ' '.join(usrin.split('@')[1].split(' ')[1:])
        s.send('SEND ' + user + ' ' + message + '\n')   # send it.
        processInput()


def getInput(prompt):
    answer = raw_input(prompt)
    return answer


def listener():
    while not CLOSED:
        recv = s.recv(8192)
        if recv == "BAD-RQST-HDR":
            print("Bad header")
            break
        if recv == "BAD-RQST-BODY":
            print("Invalid body/name/message")
            break
        if recv == "IN-USE":
            print("Name in use")
            break
        if recv == "UNKNOWN":
            print("User unknown")
        else:
            print(recv)

s.send('HELLO-FROM ' + getInput('Enter name: ') + '\n')
thread.start_new_thread(listener())
processInput()
