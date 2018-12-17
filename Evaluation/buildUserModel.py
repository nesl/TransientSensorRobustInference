import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from scipy import stats
import tensorflow as tf

#%matplotlib inline
plt.style.use('ggplot')


#This dataset comes from the Heterogeneity Activity Recognition Data Set
#

samplingFrequency = 5 #For 5 hertz
segmentLength = 4 #We want each segment to be 4 seconds long
#windowSize = samplingFrequency * segmentLength #The window size has this many values
windowSize = 90

#index,arrival_time,creation_time,x,y,z,user,model,device,gt

def read_data(file_path):
	column_names = ['timestamp', 'phone_mean_x', 'phone_mean_y', 'phone_mean_z', 'phone_std_x', 'phone_std_y', 'phone_std_z', \
		'watch_mean_x', 'watch_mean_y', 'watch_mean_z', 'watch_std_x', 'watch_std_y', 'watch_std_z', 'gt']
	#column_names = ['Index','Arrival_Time','Creation_Time','x','y','z','User','Model','Device','gt']
	data = pd.read_csv(file_path,header = None, names = column_names, dtype = \
		{"timestamp":int, "phone_mean_x":float, "phone_mean_y":float, "phone_mean_z":float, \
		"phone_std_x":float, "phone_std_y":float, "phone_std_z":float, "watch_mean_x":float, "watch_mean_y":float, "watch_mean_z":float, \
		"watch_std_x":float, "watch_std_y":float, "watch_std_z":float, "gt":object})
	#dtype={"Index":int, "Arrival_Time": int, "Creation_Time": int, "x":float,"y":float,"z":float, "User":object, "Model":object,"Device":object, "gt":object})
	return data

def feature_normalize(dataset):
	mu = np.mean(dataset,axis = 0)
	sigma = np.std(dataset,axis = 0)
	return (dataset - mu)/sigma
	#return 2*(dataset - np.min(dataset))/np.ptp(dataset)-1
	#return dataset
	
def windows(data, size):
	start = 0
	while start < data.count():
		yield int(start), int(start + size)
		start += (size / 2)
        
def segment_signal(data,window_size, channelSize):
	segments = np.empty((0,window_size, channelSize))
	labels = np.empty((0))
	for (start, end) in windows(data["timestamp"], window_size):
		#print("Segmenting...")
		p_x1 = data["phone_mean_x"][start:end]
		p_y1 = data["phone_mean_y"][start:end]
		p_z1 = data["phone_mean_z"][start:end]
		p_x2 = data["phone_std_x"][start:end]
		p_y2 = data["phone_std_y"][start:end]
		p_z2 = data["phone_std_z"][start:end]
		w_x1 = data["watch_mean_x"][start:end]
		w_y1 = data["watch_mean_y"][start:end]
		w_z1 = data["watch_mean_z"][start:end]
		w_x2 = data["watch_std_x"][start:end]
		w_y2 = data["watch_std_y"][start:end]
		w_z2 = data["watch_std_z"][start:end]
		if(len(data["timestamp"][start:end]) == window_size):
			segments = np.vstack([segments,np.dstack([p_x1,p_y1,p_z1, p_x2,p_y2, p_z2, w_x1, w_y1, w_z1, w_x2, w_y2, w_z2])])
			labels = np.append(labels,stats.mode(data["gt"][start:end])[0][0])
	print("Segments size: " + str(segments.shape))
	print("Labels size: " + str(labels.shape))
	return segments, labels
	
dataset_training = read_data('training.csv')
dataset_testing = read_data('testing.csv')
#filename = 'sensortag1sensortag2motionsense'
#dataset = read_data(filename + '.csv')
dataset_training.dropna(axis=0, how='any', inplace= True)
dataset_testing.dropna(axis=0, how='any', inplace= True)

# #Get all the indexes of zeroed out vals
# total_vals = len(dataset['phone_mean_x'])
# #print(total_vals)
# train_test_indexes = np.where(dataset['phone_mean_x'] == 0.0)[0]
# #print(train_test_indexes[0:10])

# #Create an array of true false where true indicates that this value was zereoed out
# train_test_split2 = []
# for x in range(0, total_vals+1):
	# if x not in train_test_indexes:
		# train_test_split2.append(True)
	# else:
		# train_test_split2.append(False)
		
# print(len(train_test_split2))

dataset_training['phone_mean_x'] = feature_normalize(dataset_training['phone_mean_x'])
dataset_training['phone_mean_y'] = feature_normalize(dataset_training['phone_mean_y'])
dataset_training['phone_mean_z'] = feature_normalize(dataset_training['phone_mean_z'])
dataset_training['phone_std_x'] = feature_normalize(dataset_training['phone_std_x'])
dataset_training['phone_std_y'] = feature_normalize(dataset_training['phone_std_y'])
dataset_training['phone_std_z'] = feature_normalize(dataset_training['phone_std_z'])
dataset_training['watch_mean_x'] = feature_normalize(dataset_training['watch_mean_x'])
dataset_training['watch_mean_y'] = feature_normalize(dataset_training['watch_mean_y'])
dataset_training['watch_mean_z'] = feature_normalize(dataset_training['watch_mean_z'])
dataset_training['watch_std_x'] = feature_normalize(dataset_training['watch_std_x'])
dataset_training['watch_std_y'] = feature_normalize(dataset_training['watch_std_y'])
dataset_training['watch_std_z'] = feature_normalize(dataset_training['watch_std_z'])

dataset_testing['phone_mean_x'] = feature_normalize(dataset_testing['phone_mean_x'])
dataset_testing['phone_mean_y'] = feature_normalize(dataset_testing['phone_mean_y'])
dataset_testing['phone_mean_z'] = feature_normalize(dataset_testing['phone_mean_z'])
dataset_testing['phone_std_x'] = feature_normalize(dataset_testing['phone_std_x'])
dataset_testing['phone_std_y'] = feature_normalize(dataset_testing['phone_std_y'])
dataset_testing['phone_std_z'] = feature_normalize(dataset_testing['phone_std_z'])
dataset_testing['watch_mean_x'] = feature_normalize(dataset_testing['watch_mean_x'])
dataset_testing['watch_mean_y'] = feature_normalize(dataset_testing['watch_mean_y'])
dataset_testing['watch_mean_z'] = feature_normalize(dataset_testing['watch_mean_z'])
dataset_testing['watch_std_x'] = feature_normalize(dataset_testing['watch_std_x'])
dataset_testing['watch_std_y'] = feature_normalize(dataset_testing['watch_std_y'])
dataset_testing['watch_std_z'] = feature_normalize(dataset_testing['watch_std_z'])



# for activity in np.unique(dataset["gt"]):
	# if(activity == "stand" or activity == "walk" or activity == "sit"):
		# subset = dataset[dataset["gt"] == activity][:180]
		# plot_activity(activity,subset)
		
		
input_height = 1
input_width = windowSize
num_labels = 4
num_channels = 12


segment_training, label_training = segment_signal(dataset_training, windowSize, num_channels)
#print("LABELS: " + str(labels))
label_training = np.asarray(pd.get_dummies(label_training), dtype = np.int8)
#print("LABELS: " + str(labels))
reshaped_segments_training = segment_training.reshape(len(segment_training), 1,windowSize, num_channels)

segment_testing, label_testing = segment_signal(dataset_testing, windowSize, num_channels)
#print("LABELS: " + str(labels))
label_testing = np.asarray(pd.get_dummies(label_testing), dtype = np.int8)
#print("LABELS: " + str(labels))
reshaped_segments_testing = segment_testing.reshape(len(segment_testing), 1,windowSize, num_channels)

#train_test_split = np.random.rand(len(reshaped_segments_training)) < 0.70
#print(len(train_test_split))

# train_x = reshaped_segments[train_test_split]
# train_y = labels[train_test_split]
# test_x = reshaped_segments[~train_test_split]
# test_y = labels[~train_test_split]

train_x = reshaped_segments_training
train_y = label_training
test_x = reshaped_segments_testing
test_y = label_testing

#Train on all values of dataset that were not zereod out
# train_x = reshaped_segments[train_test_split2]
# train_y = labels[train_test_split2]
# test_x = reshaped_segments[~train_test_split2]
# test_y = labels[~train_test_split2]

#print("Labels: " + str(test_y))

#input width should match the windowSize




batch_size = 10
kernel_size = 60  #60
#kernel_size = 8  
depth = 60 #60
#depth = 15
num_hidden = 1000

learning_rate = 0.0001
training_epochs = 5

total_batchs = train_x.shape[0] // batch_size

def weight_variable(shape):
    initial = tf.truncated_normal(shape, stddev = 0.1)
    return tf.Variable(initial)

def bias_variable(shape):
    initial = tf.constant(0.0, shape = shape)
    return tf.Variable(initial)
	
def depthwise_conv2d(x, W):
    return tf.nn.depthwise_conv2d(x,W, [1, 1, 1, 1], padding='VALID')
	
def apply_depthwise_conv(x,kernel_size,num_channels,depth):
    weights = weight_variable([1, kernel_size, num_channels, depth])
    biases = bias_variable([depth * num_channels])
    return tf.nn.relu(tf.add(depthwise_conv2d(x, weights),biases))
    
def apply_max_pool(x,kernel_size,stride_size):
    return tf.nn.max_pool(x, ksize=[1, 1, kernel_size, 1], 
                          strides=[1, 1, stride_size, 1], padding='VALID')

X = tf.placeholder(tf.float32, shape=[None,input_height,input_width,num_channels], name="input")
Y = tf.placeholder(tf.float32, shape=[None,num_labels])


c = apply_depthwise_conv(X,kernel_size,num_channels,depth)
p = apply_max_pool(c,10,2)
c = apply_depthwise_conv(p,6,depth*num_channels,depth//10)

shape = c.get_shape().as_list()
c_flat = tf.reshape(c, [-1, shape[1] * shape[2] * shape[3]])

f_weights_l1 = weight_variable([shape[1] * shape[2] * depth * num_channels * (depth//10), num_hidden])
f_biases_l1 = bias_variable([num_hidden])
f = tf.nn.tanh(tf.add(tf.matmul(c_flat, f_weights_l1),f_biases_l1))

out_weights = weight_variable([num_hidden, num_labels])
out_biases = bias_variable([num_labels])
y_ = tf.nn.softmax(tf.matmul(f, out_weights) + out_biases, name="y_")

loss = -tf.reduce_sum(Y * tf.log(y_))
optimizer = tf.train.GradientDescentOptimizer(learning_rate = learning_rate).minimize(loss)

correct_prediction = tf.equal(tf.argmax(y_,1), tf.argmax(Y,1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

saver = tf.train.Saver()

with tf.Session() as session:
	tf.global_variables_initializer().run()
	for epoch in range(training_epochs):
		cost_history = np.empty(shape=[1],dtype=float)
		for b in range(total_batchs):    
			offset = (b * batch_size) % (train_y.shape[0] - batch_size)
			batch_x = train_x[offset:(offset + batch_size), :, :, :]
			batch_y = train_y[offset:(offset + batch_size), :]
			_, c = session.run([optimizer, loss],feed_dict={X: batch_x, Y : batch_y})
			#print("Session: " + str(c))
			cost_history = np.append(cost_history,c)
		print("Epoch: " + str(epoch) + " Training Loss: " + str(np.mean(cost_history)) + " Training Accuracy: " + str(session.run(accuracy, feed_dict={X: train_x, Y: train_y})))

	#print("Testing Accuracy:" + str(session.run(accuracy, feed_dict={X: test_x, Y: test_y})))
	acc_final = session.run(accuracy, feed_dict={X: test_x, Y: test_y})
	print("Testing Accuracy: " + str(acc_final))
	#tf.train.write_graph(session.graph_def, 'models', './' + filename + '.pbtxt')  
	#saver.save(session, save_path = './models/' + filename + '.ckpt')
	session.close()
	
#print(tf.print(Y, [Y], message="This is Y: "))
	
# from tensorflow.python.tools import freeze_graph

# MODEL_NAME = filename

# input_graph_path = './models/' +  MODEL_NAME +'.pbtxt'
# checkpoint_path = './models/' + MODEL_NAME +'.ckpt'
# restore_op_name = "save/restore_all"
# filename_tensor_name = "save/Const:0"
# output_frozen_graph_name = './frozen_models/frozen_'+MODEL_NAME+'.pb'

# freeze_graph.freeze_graph(input_graph_path, input_saver="",
                          # input_binary=False, input_checkpoint=checkpoint_path, 
                          # output_node_names="y_", restore_op_name="save/restore_all",
                          # filename_tensor_name="save/Const:0", 
                          # output_graph=output_frozen_graph_name, clear_devices=True, initializer_nodes="")
						  
						  
