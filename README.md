# vhServer
A java server for virtualhost app

## What's vhServer
This program can run scripts, receive a file and save it, receive a ping and reply to it, and everything you can add...

It communicates with the client via TCP in JSON format :

### Server protocol
JSON format is used for sending commands to the server :

this message open test.txt with notepad.exe on the server
```json
{"action":"run","content":"c:\windows\system32\notepad.exe","params":["test.txt"]}
```
The server returns the JSON response :
```json
{"type":"info","content":"Le fichier c:\\windows\\system32\\notepad.exe existe\n"}|
{"type":"success","content":"Fichier executé avec succès :\nc:\\windows\\system32\\notepad.exe\n"}|
```

## Execution
In a console :
``` bash
java -jar vhServer.jar
```
This runs **vhServer** on  the 9001 TCP port

To change the port at startup
``` bash
java -jar vhServer.jar 9005
```
## php client
The **virtualhosts** application implements a client and shows an example of communication with vhServer :

see https://github.com/jcheron/virtualhosts

Go to http://127.0.0.1/virtualhosts/serverExchange
