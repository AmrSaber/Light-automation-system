#Android Part

###In this part we used the bluetooth communication to connect the mobile with the arduino (using HC-05 bluetooth module).
The whole idea was realy to send a number (character actually) to the arduino and tell it what to do on recieving every number.  

In the recieving part (getting the temperature or the number of people in the room) we sent a number from the mobile to the ardiuno telling it "Ok, send me this data and that data" and then (directly) open a new thead to listen for the arduino's responce.  

And that's really the whole idea :D
