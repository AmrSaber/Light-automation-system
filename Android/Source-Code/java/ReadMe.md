#Notes
All the bluetooth communications is managed through BluetoothManager class (this class is really dear to me), so all the connection/desconnection reading/writing is done through that class  

The "reading" is blocks the program until finished so all the reading is done in a different thread through AsyncTask

There are 2 untested methods write(string) and readString() I wrote those lately for future projects but didn't use them, if you use them please tell me wether they are working or not
