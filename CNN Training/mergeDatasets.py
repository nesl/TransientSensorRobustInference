
MAX_TIME_OFFSET = 200
currentTimeMilliseconds = 0

#filesToMerge = ["sensortag1", "sensortag2", "motionsense"]
filesToMerge = ["sensortag2", "sensortag1"]
mergingFilePointers = []
# Open the file with read only permit
mergedFileName = ""

for filename in filesToMerge:
	f = open(filename + ".csv", "r")
	mergingFilePointers.append(f)
	mergedFileName += filename

writeFile = open(mergedFileName + '.csv', "w")

# use readlines to read all lines in the file
#Now we iterate through the first file, and check the other files to see if their timestamps
# can be synced up (within 100ms)
currentLine = mergingFilePointers[0].readline()
newEntry = []
mergingFileEntries = [[] for x in filesToMerge]
currentGT = ""
currentIndex = 0
while currentLine:
	values = currentLine.split(",")
	main_timestamp = int(values[0])
	newEntry = values[:-1]
	currentGT = values[-1]
	
	#Check the timestamps of these others files
	for fpIndex in range(1,len(mergingFilePointers)):
		
		if len(mergingFileEntries[fpIndex]) == 0:
			line = mergingFilePointers[fpIndex].readline()
			current_values = line.split(",")
			mergingFileEntries[fpIndex] = current_values
		
		values = mergingFileEntries[fpIndex]
		other_timestamp = int(values[0])
		#print("\t Other: " + line)
		#These two values are within the maximium offset so we can sync them.
		if abs(main_timestamp - other_timestamp) < MAX_TIME_OFFSET:
			newEntry.extend(values[1:-1])  #Add the xyz values to the new entry
		elif main_timestamp > other_timestamp:  #This means that the other timestamp is slower, so we have to advance it.
			#print(str(main_timestamp) + " : " + str(other_timestamp))
			while True:  #Advance this file pointer until they sync up
				line = mergingFilePointers[fpIndex].readline()
				if not line:
					#print(mergingFileEntries[fpIndex])
					#print("Broke")
					break
				values = line.split(",")
				mergingFileEntries[fpIndex] = values
				other_timestamp = int(values[0])
				if main_timestamp - other_timestamp < MAX_TIME_OFFSET:
					#print("Broke 2" + str(mergingFileEntries[fpIndex]))
					newEntry.extend(values[1:-1])
					break
		#Otherwise, we just have to advance the currentLine
		else:
			continue
		
	newEntry.append(currentGT)
	
	#print(newEntry)
	if len(newEntry) > len(filesToMerge)*3:
	#if len(newEntry) > 5:
		currentIndex += 1
		toWrite = ""
		for item in newEntry:
			toWrite += item + ","
		toWrite = toWrite[:-1]
		#print(toWrite)
		writeFile.write(toWrite)
	currentLine = mergingFilePointers[0].readline()
	

print("Created " + str(currentIndex) + " entries")
# close the file after reading the lines.
for fp in mergingFilePointers:
	fp.close()
writeFile.close()