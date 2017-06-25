import os, sys
import time

import tensorflow as tf

from apscheduler.schedulers.blocking import BlockingScheduler

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

class Model:
    def __init__(self):
        # Load the graph
        with tf.gfile.FastGFile("retrained_graph.pb", 'rb') as f:
            self.graph_def = tf.GraphDef()
            self.graph_def.ParseFromString(f.read())
            tf.import_graph_def(self.graph_def, name='')

        # Loads label file, strips off carriage return
        self.label_lines = [line.rstrip() for line
                           in tf.gfile.GFile("retrained_labels.txt")]

    def predict(self):
        with tf.Session() as sess:
            # Feed the image_data as input to the graph and get first prediction
            softmax_tensor = sess.graph.get_tensor_by_name('final_result:0')

            image_data = tf.gfile.FastGFile("test.jpg", 'rb').read()

            predictions = sess.run(softmax_tensor, \
                     {'DecodeJpeg/contents:0': image_data})

            # Sort to show labels of first prediction in order of confidence
            top_k = predictions[0].argsort()[-len(predictions[0]):][::-1]

            scores = ""
            final_prediction = self.label_lines[top_k[0]]

            for node_id in top_k:
                human_string = self.label_lines[node_id]
                score = predictions[0][node_id]

                scores = scores + '%s:%.5f\n' % (human_string, score)

            with open("prediction_details.txt", "w") as my_file:
                my_file.write(scores)
            with open("prediction.txt", "w") as my_file:
                my_file.write(final_prediction)

            print("Writing prediction")


def main():
    model = Model()
    while True:
        model.predict()
        print("Ran")
        time.sleep(1)




if __name__ == '__main__':
    main()
