import numpy as np
import pandas as pd
from scipy.misc import toimage


def load_data():
    data = pd.read_csv("fer2013.csv")
    print("CSV loaded")

    train_set = data.loc[data['Usage'] == "Training"]
    test_set = data.loc[data['Usage'] == "PrivateTest"]

    x_train = process_images(train_set["pixels"])
    y_train = train_set["emotion"]

    x_test = process_images(test_set["pixels"])
    y_test = test_set["emotion"]

    print("Data loaded")

    return x_train, y_train, x_test, y_test


def process_images(images):
    """
    Turns the input strings of images into numpy arrays.
    Expects a pandas.Series of strings
    Outputs a list of np.Array objects
    """
    #assert type(images) == pandas.core.series.Series
    images_output = []
    for image_string in images.iteritems():
        # Get string data into numpy array
        image_array = np.asarray([int(pixel) for pixel in image_string[1].split()])

        # TODO It's one dimensional for some reason.
        # I took a guess as to how it's encoded. Someone else look at this.
        # image_array = image_array.reshape(48, 48)

        # Normalize between -1 and 1
        normalized_image_array = (2 * (image_array / 256)) - 1

        images_output.append(normalized_image_array)

    return images_output