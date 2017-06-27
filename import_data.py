from os.path import expanduser
from math import ceil, floor

import numpy as np
import pandas as pd
from scipy.misc import toimage
from PIL import Image


def load_data(do_reshape=True):
    data = pd.read_csv("fer2013.csv")
    print("CSV loaded")

    train_set = data.loc[data['Usage'] == "Training"]
    test_set = data.loc[data['Usage'] == "PrivateTest"]
    validation_set = data.loc[data['Usage'] == "PublicTest"]

    x_train = process_images(train_set["pixels"])
    y_train = process_output(train_set["emotion"])

    x_test = process_images(test_set["pixels"])
    y_test = process_output(test_set["emotion"])

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
    for index, image_string in enumerate(images.iteritems()):
        # Get string data into numpy array
        image_array = np.asarray([int(pixel) for pixel in image_string[1].split()])

        # TODO It's one dimensional for some reason.
        # I took a guess as to how it's encoded. Someone else look at this.
        image_array = image_array.reshape(48, 48, 1)

        # Normalize between -1 and 1
        normalized_image_array = image_array / 256

        images_output.append(normalized_image_array)

    return np.array(images_output)


def process_output(output):
    n_values = np.max(output) + 1
    return np.eye(n_values)[output]

def resize_and_letterbox(image):
    TARGET_WIDTH = 299
    TARGET_HEIGHT = 299

    # Default values
    width = image.width
    height = image.height

    x1 = 0
    y1 = 0
    x2 = width
    y2 = height

    # Calculate new dimensions and letterboxing positions
    if width == height:
        new_width, new_height = TARGET_WIDTH, TARGET_HEIGHT
    elif width > height:
        new_height = ceil(TARGET_WIDTH * (height / width))
        new_width = TARGET_WIDTH

        # prepare for letterboxing
        buffer = TARGET_HEIGHT - new_height
        # If there's an extra pixel, add it to the top
        y1 = ceil(buffer / 2)
        y2 = y1 + new_height
    elif width < height:
        new_width = ceil(TARGET_HEIGHT * (width / height))
        new_height = TARGET_HEIGHT

        # prepare for letterboxing
        buffer = TARGET_WIDTH - new_width
        # If there's an extra pixel, add it to the right side.
        x1 = floor(buffer / 2)
        x2 = x1 + new_width

    # Resize the image
    resized_image = image.resize((new_width, new_height))

    # Create a blank background image to paste into
    letterboxed_image = np.zeros(TARGET_SHAPE)
    letterboxed_image.fill(255)

    # Letterbox the image
    image_array = np.array(resized_image)
    letterboxed_image[y1:y2, x1:x2] = image_array

    return letterboxed_image


def main():
    data = pd.read_csv("fer2013.csv")
    print("CSV loaded")

    train_set = data.loc[data['Usage'] == "Training"]
    test_set = data.loc[data['Usage'] == "PrivateTest"]

    for row in train_set.itertuples():
        label = row.emotion

        image_string = row.pixels
        image_array = np.asarray([int(pixel) for pixel in image_string.split()])
        image_array = image_array.reshape(48, 48)
        image = toimage(image_array)

        index = row.index

        image.save(expanduser("~/emotion/%s-%s.jpg") % (label, index))


if __name__ == '__main__':
    main()
