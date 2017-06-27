from os.path import expanduser

import numpy as np

from sklearn.decomposition import PCA
from sklearn.ensemble import AdaBoostClassifier
from sklearn.metrics import accuracy_score, fbeta_score

import tensorflow as tf
import tensorflow.contrib.keras as k
from tensorflow.contrib.keras.python.keras.models import Sequential
from tensorflow.contrib.keras.python.keras.layers import Dense, Dropout, Flatten
from tensorflow.contrib.keras.python.keras.layers import Conv2D, MaxPooling2D

from import_data import load_data, process_output

def main():
    x_train, y_train, x_test, y_test = load_data()

    model = Sequential()

    model.add(Conv2D(32, kernel_size=(11, 11), strides=4, padding="same", activation='relu',
                     input_shape=(48, 48, 1)))
    model.add(MaxPooling2D(pool_size=(3, 3), strides=2, padding="valid"))
    model.add(Conv2D(32, kernel_size=(5, 5), strides=1, padding="same", activation='relu'))
    model.add(MaxPooling2D(pool_size=(3, 3), strides=2, padding="valid"))
    model.add(Conv2D(32, kernel_size=(3, 3), strides=1, padding="same", activation='relu'))
    model.add(Conv2D(32, kernel_size=(3, 3), strides=1, padding="same", activation='relu'))
    model.add(Conv2D(32, kernel_size=(3, 3), strides=1, padding="same", activation='relu'))
    model.add(Dense(1024, activation='relu'))
    model.add(Dense(512, activation='relu'))
    model.add(Dense(7, activation='softmax'))

    model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])

    model.fit(x_train, y_train,
          batch_size=128,
          epochs=5,
          verbose=1,
          validation_data=(x_test, y_test))

    model.save(expanduser("~/emotion/alex_net.h5"))

    accuracy, fbeta = test_model(model, x_test, y_test)
    print("Accuracy: %s" % accuracy)
    print("F-Beta: %s" % fbeta)


def test_model(model, x_test, y_test):
    predictions = model.predict(x_test, batch_size=128)
    index_predictions = []
    for row in predictions:
        max_index = 0
        max_value = row[0]
        for index, value in enumerate(row):
            if value > max_value:
                max_index = index
        index_predictions.append(max_index)
    final_predictions = process_output(index_predictions)
    accuracy = accuracy_score(y_test, predictions)
    fbeta = fbeta_score(y_test, np.array(predictions) > 0.2, beta=2, average='samples')
    return accuracy, fbeta


def load_and_test():
    x_train, y_train, x_test, y_test = load_data()
    model = load_data(expanduser("~/emotion/model.h5"))

    accuracy, fbeta = test_model(model, x_test)
    print("Accuracy: %s" % accuracy)
    print("F-Beta: %s" % fbeta)


if __name__ == '__main__':
    main()
    #,load_and_test()
