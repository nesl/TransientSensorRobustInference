# filenames to parse:
# Watch_accelerometer.csv, Phones_accelerometer.csv

#This program downsamples all the data into 5Hz
#This works by choosing the first value in each 200ms block

MAX_TIME_DELAY = 200
currentTimeMilliseconds = 0

#Example MotionSenseHRV: ACC: -0.5955810546875,-0.3194580078125,-0.7149658203125
#Example SensorTag: [-0.2626953125, 0.1474609375, -4.0810546875]
#Example Phone: 

# Open the file with read only permit
f = open('Watch_accelerometer.csv', "r")
#f  = open('Phones_accelerometer.csv', "r")
writeFile = open('watch_accelerometer_ds.csv', "a")
#writeFile = open('Watch_accelerometer_ds.csv', "a")
#writeFile.write("index,arrival_time,creation_time,x,y,z,user,model,device,gt")
lastUniqueID = ""
# use readlines to read all lines in the file
# The variable "lines" is a list containing all lines in the file
line = f.readline()
print(line)
line = f.readline()
while line:
	values = line.split(",")
	#index = values[0]
	arrivalTime = int(values[1])
	#creationTime = values[2]
	#xVal = values[3]
	#yVal = values[4]
	#zVal = values[5]
	user = values[6]
	model = values[7]
	device = values[8]
	currentUniqueID = user+model+device
	if(lastUniqueID == ""):
		lastUniqueID = currentUniqueID
	else:
		break
	#groundTruth = values[9]
	
	
	
	#Time since last recorded timestamp is greater than 200ms
	if arrivalTime > currentTimeMilliseconds + MAX_TIME_DELAY and currentUniqueID == lastUniqueID:
		currentTimeMilliseconds = arrivalTime
		lastUniqueID = currentUniqueID
		#print(line)
		writeFile.write(line)
	
	line = f.readline()
	#break

# close the file after reading the lines.
f.close()
writeFile.close()