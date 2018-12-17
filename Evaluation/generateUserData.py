from random import random
import os

sit_count = 0
walk_count = 0
run_count = 0
bike_count = 0


def getLabel(subListOfLabels):
	return_val = -1
	return_str = ""
	
	global sit_count, walk_count, run_count, bike_count
	
	for i in range(0, len(subListOfLabels)):
		if subListOfLabels[i] == "1":
			return_val = i
	
	if return_val == 0:
		return_str = "SITTING"
		#sit_count += 1
	elif return_val == 1:
		return_str = "WALKING"
		#walk_count += 1
	elif return_val == 2:
		return_str = "RUNNING"
		#run_count += 1
	elif return_val == 3:
		return_str = "BICYCLING"
		#bike_count += 1
	return return_str

def addLabelCount(label):
	global sit_count, walk_count, run_count, bike_count
	
	if label == "SITTING":
		sit_count += 1
	elif label == "WALKING":
		walk_count += 1
	elif label == "RUNNING":
		run_count += 1
	elif label == "BICYCLING":
		bike_count += 1


def createNewDataset(filename, outputFile, generateLostData = True):
	# Open the file with read only permit
	f = open(filename, "r")

	# use readlines to read all lines in the file
	# The variable "lines" is a list containing all lines in the file
	line = f.readline()

	values = line.split(",")

	#Phone columns: raw_acc:3d:mean_x,raw_acc:3d:mean_y,raw_acc:3d:mean_z,raw_acc:3d:std_x,raw_acc:3d:std_y,raw_acc:3d:std_z
	#    18, 19, 20, 21, 22, 23
	#Watch column names: watch_acceleration:3d:mean_x,watch_acceleration:3d:mean_y,watch_acceleration:3d:mean_z,watch_acceleration:3d:std_x,watch_acceleration:3d:std_y,watch_acceleration:3d:std_z
	#  101, 102, 103, 104, 105, 106



	#label:SITTING,label:FIX_walking,label:FIX_running,label:BICYCLING
	#  227, 228, 229, 230

	#phone_accel_value_start = values.index("label:FIX_walking")
	#print(values[phone_accel_value_start])

	line = f.readline()

	while line:

		values = line.split(",")
		
		timestamp = values[0]
		
		label = getLabel(values[227:231])
		
		#Generate a random val
		random_val = random()
		
		#Use if < 'percentage' to determine how much of the data to zero out
		
		
		#We actually have a label
		if(len(label) > 0):
		#Format of the output is like this:  timestamp, phone_acc mean x,y,z, phone_acc std_dev x,y,z,
		#  watch_acc mean x,y,z, watch_acc std_dev x,y,z,  label
		
			if generateLostData:
			
				#This is if we want all data to show up in the data.csv
				toOutput = timestamp + "," + values[18] + "," + values[19] + "," + values[20] + "," + values[21] + "," + values[22] + "," + values[23] + "," \
					+ values[101] + "," + values[102] + "," + values[103] + "," + values[104] + "," + values[105] + "," + values[106] + "," + label
				
				#This is if we want only the phone accel data to show up, and watch data all zeroed out
				#toOutput = timestamp + "," + values[18] + "," + values[19] + "," + values[20] + "," + values[21] + "," + values[22] + "," + values[23] + "," \
				#	+ "0.0, 0.0, 0.0, 0.0, 0.0, 0.0," + label
				
				#This is if we want only the watch accel data to show up, and phone data all zeroed out
				#toOutput = timestamp + "," + "0.0, 0.0, 0.0, 0.0, 0.0, 0.0," \
				#	+ values[101] + "," + values[102] + "," + values[103] + "," + values[104] + "," + values[105] + "," + values[106] + "," + label
				
				#This is if we don't want any watch data at all - run this with buildSingleModel.py
				#toOutput = timestamp + "," + values[18] + "," + values[19] + "," + values[20] + "," + values[21] + "," + values[22] + "," + values[23] + "," + label
				
				#This is if we don't want any phone data at all - run this with buildSingleModel.py
				#toOutput = timestamp + "," + values[101] + "," + values[102] + "," + values[103] + "," + values[104] + "," + values[105] + "," + values[106] + "," + label
				
				#This is if we only want the watch data zeroed out 75% of the time
				# if random_val < 0.25:
					# toOutput = timestamp + "," + values[18] + "," + values[19] + "," + values[20] + "," + values[21] + "," + values[22] + "," + values[23] + "," \
					# + values[101] + "," + values[102] + "," + values[103] + "," + values[104] + "," + values[105] + "," + values[106] + "," + label
				# else:
					# toOutput = timestamp + "," + "0.0, 0.0, 0.0, 0.0, 0.0, 0.0," \
					# + values[101] + "," + values[102] + "," + values[103] + "," + values[104] + "," + values[105] + "," + values[106] + "," + label
			else:
			
				if random_val < 0.50:
					 toOutput = timestamp + "," + values[18] + "," + values[19] + "," + values[20] + "," + values[21] + "," + values[22] + "," + values[23] + "," \
					 + values[101] + "," + values[102] + "," + values[103] + "," + values[104] + "," + values[105] + "," + values[106] + "," + label
				else:
					 toOutput = timestamp + "," + values[18] + "," + values[19] + "," + values[20] + "," + values[21] + "," + values[22] + "," + values[23] + "," \
					 + "0.0, 0.0, 0.0, 0.0, 0.0, 0.0," + label
			
			#Make sure that the output does not contain 'nan'
			if not "nan" in toOutput:
				addLabelCount(label)
				outputFile.write(toOutput + "\n")
		
		line = f.readline()
		#break

		
	print("Sit Count: " + str(sit_count))
	print("Walk Count: " + str(walk_count))
	print("Run Count: " + str(run_count))
	print("Bike Count: " + str(bike_count))

	# close the file after reading the lines.
	f.close()
	
	
#filenames = ['78A91A4E-4A51-4065-BDA7-94755F0BB3BB.features_labels.csv', '9DC38D04-E82E-4F29-AB52-B476535226F2.features_labels.csv', '806289BC-AD52-4CC1-806C-0CDB14D65EB6.features_labels.csv']

#We only want the user data files - ignore everything else
notNeededFiles = ["data.csv", "training.csv", "testing.csv"]
#Get all the necessary CSV files
training_set = [(f + "/" + f) for f in os.listdir('.') if "csv" in f and f not in notNeededFiles]
cutoff = len(training_set)//2
#Remove the last 7 files
testing_set = training_set[:cutoff]
training_set = training_set[cutoff:]

#print(filenames)

writeFile = open('training.csv', "w")

for filename in training_set:
	createNewDataset(filename, writeFile, True)
	print("----- training file ------")
	# sit_count = 0
	# walk_count = 0
	# run_count = 0
	# bike_count = 0
	# createNewDataset(filename, writeFile, False)
	# print("----- training file ------")
	# sit_count = 0
	# walk_count = 0
	# run_count = 0
	# bike_count = 0

writeFile.close()

writeFile = open('testing.csv', "w")

sit_count = 0
walk_count = 0
run_count = 0
bike_count = 0

for filename in testing_set:
	# createNewDataset(filename, writeFile, True)
	# print("----- testing file ------")
	# sit_count = 0
	# walk_count = 0
	# run_count = 0
	# bike_count = 0
	createNewDataset(filename, writeFile, False)
	print("----- testing file ------")
	# sit_count = 0
	# walk_count = 0
	# run_count = 0
	# bike_count = 0

writeFile.close()
