import cv2
import numpy as np

def adaptObjectHistogram(frame):
    boxOne = frame[105:125,505:525]
    boxTwo = frame[ 105:125,555:575]
    boxThree = frame[155:175, 505:525]
    boxFour = frame[155:175,555:575]
    adaptObjectColor = np.hstack((boxOne, boxTwo, boxThree, boxFour))
    hsvObjectColor = cv2.cvtColor(adaptObjectColor, cv2.COLOR_BGR2HSV)
    objectHist = cv2.calcHist([hsvObjectColor], [0,1], None, [12,15], [0,180,0,256])
    cv2.normalize(objectHist, objectHist, 0,255,cv2.NORM_MINMAX)
    #channel adapter
    return True, objectHist

def getNewObjectHist(objectName):
    cap = cv2.VideoCapture(0)
    while True:

        _, frame = cap.read()
        frame = cv2.flip(frame, 1)
        cv2.putText(frame, "Place region of the {} inside the boxes and press `A`".format(objectName), (5,50), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255,255,255), 1, cv2.LINE_AA)
        cv2.rectangle(frame, (500,100), (530,130), (105,105,105), 2)
        cv2.rectangle(frame, (550, 100), (580, 130), (105,105,105), 2)
        cv2.rectangle(frame, (500, 150), (530, 180), (105,105,105), 2)
        cv2.rectangle(frame, (550, 150), (580, 180), (105,105,105), 2)
        boxOne = frame[105:125,505:525]
        boxTwo = frame[ 105:125,555:575]
        boxThree = frame[155:175, 505:525]
        boxFour = frame[155:175,555:575]
        finalHistImage = np.hstack((boxOne, boxTwo, boxThree, boxFour))
        cv2.imshow("Video Feed", frame)
        key = cv2.waitKey(10)
        if key == 97:
            objectColor = finalHistImage
            cv2.destroyAllWindows()
            break
        if key == 27:
            cv2.destroyAllWindows()
            cap.release()
            exit()

    hsvObjectColor = cv2.cvtColor(objectColor, cv2.COLOR_BGR2HSV)
    objectHist = cv2.calcHist([hsvObjectColor], [0,1], None, [12,15], [0,180,0,256])
    cv2.normalize(objectHist, objectHist, 0,255,cv2.NORM_MINMAX)
    return True, objectHist

def locateObject(objectHist, frame):
    hsvFrame = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    objectSegment = cv2.calcBackProject([hsvFrame], [0,1], objectHist, [0,180,0,256], 1)
    disc = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (5,5))
    cv2.filter2D(objectSegment, -1, disc, objectSegment)
    _, threshObjectSegment = cv2.threshold(objectSegment,70,255,cv2.THRESH_BINARY)
    threshObjectSegment = cv2.merge((threshObjectSegment,threshObjectSegment,threshObjectSegment))
    locatedObject = cv2.bitwise_and(frame, threshObjectSegment)
    locatedObjectGray = cv2.cvtColor(locatedObject, cv2.COLOR_BGR2GRAY)
    _, locatedObjectThresh = cv2.threshold(locatedObjectGray, 70, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
    locatedObject = cv2.medianBlur(locatedObjectThresh, 5)
    return locatedObject
