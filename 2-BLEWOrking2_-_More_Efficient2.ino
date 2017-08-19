#include <SoftwareSerial.h>
#include <AESLib.h>
#include <Base64.h>

//TMP36 Pin Variables
int sensorPin = 0; //the analog pin the TMP36's Vout (sense) pin is connected to
                        //the resolution is 10 mV / degree centigrade with a
                        //500 mV offset to allow for negative temperatures
 int keyN;
// int i; //Counter
// String password;
    
/*
 * setup() - this function runs once when you turn your Arduino on
 * We initialize the serial connection with the computer
 */


SoftwareSerial mySerial(8, 7); // RX, TX  
// Connect HM10      Arduino Uno
//     Pin 1/TXD          Pin 7
//     Pin 2/RXD          Pin 8

void setup() {  
  Serial.begin(9600);
   keyN = 3;
  // If the baudrate of the HM-10 module has been updated,
  // you may need to change 9600 by another value
  // Once you have found the correct baudrate,
  // you can update it using AT+BAUDx command 
  // e.g. AT+BAUD0 for 9600 bauds
  mySerial.begin(9600);
}

void loop() {  
 
  if (mySerial.available()) {

    //c = String | i = integer  | 
       
    int i;
    String c;
    c = mySerial.readString();//Read String recieved
    if (c == "nope"){
        Serial.println("Session Terminated.. reset"); 
        setup();
       }
       
           /* Serial.print("SData Recieved:  ");
            Serial.println(c); */
   else{

    /*== Recieving the character ==*/
                Serial.print("SData Recieved:  ");
                Serial.println(c); 

/* ==Ej Protection Mecchanisms == 
                
                      //==Decoding and Decrypting the Data==
                  
                                //==Deconding the String from the readable Base64==
                                  char decodedString[30]; // Variable that will contain the decrypted string
                                  strcpy(decodedString, c.c_str());  // Copy the string inside the array of characters 
                                  // Serial.println(encodedLen);
                                 // char decodedLen = ;
                                  char decoded[(char)(base64_dec_len(decodedString, sizeof(decodedString)))]; //Creating new array with new size that will contain the new size
                                  base64_decode(decoded, decodedString, 30); //Base64 decoding ("decoded container", "encodedString", "size of encoding string")
                                //  delete decodedString; // delete this array
                                  
                                   
                                   //==Padding the key that will decrypt the encryption== | Using c as  the key 
                                   c = "r!r"; // main key used in both program | maybe implement diffrent key
                                   Serial.print("Key Used on decryption: ");
                                   Serial.println(c);
                                   //char pad = 0x029;
                                    int missingLength;
                                   if( (missingLength = (c.length() % 16)) != 0)//Check if the variable is up to 16 bits
                                        {
                                           missingLength = 16 - (c.length() % 16 ); 
                                           for (i = 0; i< missingLength; i++) // Calculate the remaining bits
                                           c+= (char)0x29;//pad missing length with ")"
                                        }
                                   //delete &missingLength; // Delete this variable
                                   
                                 //==AES128 Decryption==
                                   uint8_t key_string_array[16]; // Variable that will contain the key | Setting Key  
                                   strcpy((char*)key_string_array, c.c_str());  // Copy the key into an array           
                                   aes128_dec_single(key_string_array, decoded);
                                   c ="";// Empty the variable c
                                   
                                   //==Normalising variable== | Using c to store the decoded message
                                   
                                  // String encodedMessage;
                                     for(i = 0; i<16; i++)
                                          {
                                                if(decoded[i] == 0x29)
                                                    decoded[i]=0;
                                                else c+=decoded[i];
                                           }
                               
                                   Serial.print("Decrypted in AES and decoded in base64:  ");
                                   Serial.println(c);
                                  
                                   
                                   // ==Calling Method EjDigest == | Using c to collect the value to send from the Digest function 
                                 c = ejKeyDigestOut(decoded); 
                                   // delay(3000);
                                   Serial.print("Data Normalized:  ");
                                   Serial.println(c); // show the value extracted

 /*End of Protection Mecchanisms*/

                        
      
                  //Checking if Password is correct
                  if(c == "1045"){
                   
                  Serial.print(i = readTemperature());  // if Password correct retrieve temperature Value...| using "i" to store the temeprature value
                  
                  Serial.println(" degrees C");
                  i=ejKeyDigestIn(i);
                  mySerial.print(i); // Change the charateristics of the GATT server notifying the android
             }
             else
                   Serial.print("WRONG PASSWORD");
       }
    }
    
 }

 String ejKeyDigestOut(char *data){
        keyN++; 
        int key;
        long int g=0;
        int b=0;

                  /* Creation of the Key */
                if (keyN>15)
                    key = 3;
          
                    Serial.print("Current data trustful key:  ");
                    Serial.println(key = keyN^55);
                    

                /*Converting array of char to Int*/
                  for(unsigned int i=0; i<strlen(data); i++)
                   {
                        g = g * 10 + ( data[i] - '0' );
                   }
                     Serial.print("Successful Converted Number to an Integer:  "); 
                     Serial.println(g);
                     Serial.print("Key Extracted:  "); 
                     Serial.println(b = g&63);//Extraction of the key
                     
                       //Checking if Key is correct
                  if(b != key)//If key is diffrent, data is being destroyed
                      g = 0; // data is being destroyed.
                else
                   { 
                     Serial.print("print of data normalized:  "); 
                     Serial.println(g=g>>7);//Data is being normalized to be recognized
                   }
        return (itoa(g, data, 10));
    }
    

   int ejKeyDigestIn(int temp ){
     char x[10];
        keyN++; //Creation of the key
        int key;

      if (keyN>20)
            key = 3;
     Serial.print("New Key:  "); Serial.println(key = keyN^55);
        temp = temp << 7;
      Serial.print("Data to be sent"); Serial.println(temp = temp|key);
        return temp;
    }

int readTemperature()
{
                         int reading = analogRead(sensorPin);  
                         // converting that reading to voltage, for 3.3v arduino use 3.3
                         float voltage = reading * 5.0;
                         voltage /= 1024.0; 
                          // now print out the temperature
                         int temperatureC = (voltage - 0.5) * 100 ;  //converting from 10 mv per degree wit 500 mV offset
                                                                       //to degrees ((voltage - 500mV) times 100)
                         return temperatureC;
  }



