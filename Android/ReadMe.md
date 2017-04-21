# Android Part

### In this part we used the bluetooth communication to connect the mobile with the arduino (using HC-05 bluetooth module).
The whole idea was really to send a number (a character actually) to the arduino, have it always listening then tell it what to do on recieving that number.  

In the recieving part (getting the temperature or the number of people in the room) we sent a number from the mobile to the ardiuno telling it "Ok, send me this data and that data" and then open a new thread to listen for the arduino's responce (new thread is necessary because the listening process blocks everything else).  

And that's really the whole idea :D
