
import time
import requests
import cv2
import operator
import numpy as np
#import matplotlib.pyplot as plt
from PIL import Image
from scipy.misc import toimage
from os.path import expanduser




def processRequest(json, data, headers, params):
    """
    Helper function to process the request to Project Oxford

    Parameters:
    json: Used when processing images from its URL. See API Documentation
    data: Used when processing image read from disk. See API Documentation
    headers: Used to pass the key information and the data type request
    """

    _url = 'https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize'
    _key = '9a2f9f6e4c694c72aa1bd6ea84ba5a87'
    _maxNumRetries = 10

    retries = 0
    result = None

    while True:

        response = requests.request('post', _url, json=json, data=data, headers=headers, params=params)

        if response.status_code == 429:

            print("Message: %s" % (response.json()['error']['message']))

            if retries <= _maxNumRetries:
                time.sleep(1)
                retries += 1
                continue
            else:
                print('Error: failed after retrying!')
                break

        elif response.status_code == 200 or response.status_code == 201:

            if 'content-length' in response.headers and int(response.headers['content-length']) == 0:
                result = None
            elif 'content-type' in response.headers and isinstance(response.headers['content-type'], str):
                if 'application/json' in response.headers['content-type'].lower():
                    result = response.json() if response.content else None
                elif 'image' in response.headers['content-type'].lower():
                    result = response.content
        else:
            print("Error code: %d" % (response.status_code))
            print("Message: %s" % (response.json()['error']['message']))

        break

    return result


def renderResultOnImage(result, img):
    """Display the obtained results onto the input image"""

    for currFace in result:
        faceRectangle = currFace['faceRectangle']
        cv2.rectangle(img, (faceRectangle['left'], faceRectangle['top']),
                      (faceRectangle['left'] + faceRectangle['width'], faceRectangle['top'] + faceRectangle['height']),
                      color=(255, 0, 0), thickness=5)

    for currFace in result:
        faceRectangle = currFace['faceRectangle']
        currEmotion = max(currFace['scores'].items(), key=operator.itemgetter(1))[0]

        textToWrite = "%s" % (currEmotion)
        cv2.putText(img, textToWrite, (faceRectangle['left'], faceRectangle['top'] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5,
                    (255, 0, 0), 1)


# Load raw image file into memory
def runthisfile():
    pathToFileInDisk = r'test.jpg'
    with open( pathToFileInDisk, 'rb' ) as f:
        data = f.read()

    _url = 'https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize'
    _key = '9a2f9f6e4c694c72aa1bd6ea84ba5a87'
    _maxNumRetries = 10
    headers = dict()
    headers['Ocp-Apim-Subscription-Key'] = _key
    headers['Content-Type'] = 'application/octet-stream'

    json = None
    params = None

    result = processRequest( json, data, headers, params )

    if result is not None:
        for currFace in result:
            currEmotion = max(currFace['scores'].items(), key=operator.itemgetter(1))[0]
        print(currEmotion)

def main():
    while True:
        runthisfile()
        time.sleep(5)




if __name__ == '__main__':
    main()
