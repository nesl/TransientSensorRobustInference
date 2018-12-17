Project for Transient Sensor Robust Inference

mCerebrum-DemoApp contains the Android source code for interacting with multiple bluetooth devices

Evaluation contains python code pertaining to evaluating the accuracy of CNNs trained and tested on missing data cases.
Uses the ExtraSensory Dataset at http://extrasensory.ucsd.edu/.  Just download the files, unzip them, and move the resulting user folders
into the directory containing all the python files.

CNN Training consists of python code that I used to train the CNN classifier using data from the app that I built (mCerebrum-DemoApp)

# mCerebrum-MotionSense-Altered contains the Android source code for interacting with MotionSense HRV devices and sampling the phone's inertial sensors.
   If you go through the code, you'll find the following major differences from the original MD2K software:
## NEW ADDITIONS

 - Integrated inertial sensing from Phone (phone/Accelerometer.java and phone/Gyroscope.java)
 - Added direct data export to CSV for all data (exporter.java)
 - Added NTP time updating when exporting data (ntpUpdateThread.java)

