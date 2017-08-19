# SmartThermometer-arduino-AndroidApp
This was my final project at my University. This is an IoT that connects a mobile app to an arduino to retrieve information regarding the temperature of the room.  My Goal in this project was to prove that Bluetooth LE has vulnerabilities and to introduce a possible to solution to mitigate the problem without exhausting all the resources of the device.

The vulnerability I focused on is Man In The Middle Attack.

The app authenticates to the device thorugh password and then the information desired is then recieved to the app.


The security of of the connection is based in two paramenters: Encryption and Uniqness of the chipher text.
The encryption to use can be any of the Single Key Lightweight encryptions that are available, such as XTEA or SEA.
In this project I used AES for semplicity. 
Prior Authentication, an Index is being created which is going to add to the password through a concatenation of bytes, which is being made thorugh bitwise operations. Next is being encrypted and sent to the device.

This should prevent possibile Man in the Middle attacks, and Replay attacks. These operations are not to expensive in terms of CPU possibilities for Internet of Things devices, which are known to have CPU Limits.


The procedure of this technique can be found on the EjDigest Methods, encrypts and decrypts the unique messages.

