# Light automation system (arduino + android)

###The main idea is to control room's light(s) automatically + remotely by using arduino board (and 2 ultra sonic sensors) and android application

####This was a small project (and we won the firs place :D) for 2 engineering students in thier first year of electerical engineering in SFE (Shoubra faculty of engineering, Egypt) and it consists mainly of 2 parts ...

###Arduino Part
The system would turn the light(s) on when some one enters the room and it would count the people in the room and the light(s) would go off when there is no one in the room,
this is done by putting 2 ultrasonic sensors cosecutevly on the interance's door the order od which the human will interact with the 2 sensors will define if he is entering or leaving

###Android part
The Android part is that you have full control on every thing from the phone,
you can directly control the light (force on/off) and you can turn the system on/off
You can also manage the system's data, i.e. you can get/set the number of people in the room easily and all of this is done by bluetooth communication (and bluetooth module HC-05 on the arduino board) 

###An Extra feature
There is a temperature sensor in the system which you can get its value from the phone, because why not :D
