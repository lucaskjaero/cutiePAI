import numpy as np

from sklearn.decomposition import PCA
from sklearn.ensemble import AdaBoostClassifier
from sklearn.metrics import accuracy_score, fbeta_score

import tensorflow as tf
import tensorflow.contrib.keras as k
from tensorflow.contrib.keras.python.keras.models import Sequential
from tensorflow.contrib.keras.python.keras.layers import Dense, Dropout, Flatten
from tensorflow.contrib.keras.python.keras.layers import Conv1D, MaxPooling1D

from import_data import load_data

def main():
    x_train, y_train, x_test, y_test = load_data()
    """

    clf = AdaBoostClassifier()
    print("Training classifier")
    clf.fit(x_train, y_train)

    print("Making test predictions")

    predictions = clf.predict(x_test)

    print("Accuracy: %s" % accuracy_score(y_test, predictions))
    # Accuracy: 0.32711061577
    """
    #x_train = np.expand_dims(x_train, axis=2)
    model = Sequential()
    model.add(Conv2D(32, kernel_size=(3),
                 activation='relu',
                 input_shape=(48, 48, 1)))

    model.add(Conv2D(64, (3, 3), activation='relu'))
    model.add(MaxPooling1D(pool_size=(2, 2)))
    model.add(Dropout(0.25))
    model.add(Flatten())
    model.add(Dense(128, activation='relu'))
    model.add(Dropout(0.5))
    model.add(Dense(7, activation='sigmoid'))

    model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])

    model.fit(x_train, y_train,
          batch_size=128,
          epochs=4,
          verbose=1,
          validation_data=(x_test, y_test))

    predictions = model.predict(x_test, batch_size=128)
    print("Accuracy: %s" % accuracy_score(y_test, predictions))
    print(fbeta_score(y_test, np.array(predictions) > 0.2, beta=2, average='samples'))

if __name__ == '__main__':
    main()
