import cv2
import numpy as np
import math
#add support for multiple hands
#add support for multiple video sources
def dist(a, b):
	return math.sqrt((a[0] - b[0])**2 + (b[1] - a[1])**2)

def drawDefects(frame, defects, cnt, refineDefects = None, refineMetric = None):
    if refineMetric is None:
        refineMetric = 0
    if refineDefects is None:
        refineDefects = False
    #print "refine" + str(refineDefects)
    if refineDefects is None:
        refineDefects = False
    defectList = []
    for i in range(defects.shape[0]):
        s,e,f,d = defects[i,0]
        start = tuple(cnt[s][0])
        end = tuple(cnt[e][0])
        far = tuple(cnt[f][0])
        if refineDefects:
            defectList.append(far)
        else:
            #print "fucker"
            cv2.circle(frame,far,5,[0,255,255],-1)
    defectList.sort()
    if refineDefects:
        j = 0
        #print "old", len(defectList)
        #print defectList
        while j<len(defectList) -1:
            #print refineMetric
            #print dist(defectList[j], defectList[j+1])
            if dist(defectList[j], defectList[j+1]) < refineMetric:

                    del defectList[j]
            else:
                j += 1
        #print "new", len(defectList)
        for i in range(len(defectList)):
            #print "okau"
            cv2.circle(frame,defectList[i],5,[0,255,255],-1)
        return frame, defectList


    return frame, None



def captureHistogram():
    #add video source
    cap = cv2.VideoCapture(0)
    while True:

        _, frame = cap.read()
        frame = cv2.flip(frame, 1)
        cv2.putText(frame, "Place region of the hand inside the boxes and press `A`", (5,50), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255,255,255), 1, cv2.LINE_AA)
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
    return objectHist

def locateObject(frame, objectHist):
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

def detectHand(frame, hist, sketchContours, colour = None, thickness = None):
    if colour is None:
        colour  = (0,255,0)
    if thickness is None:
        thickness = 2
    ''' add security checks for frame, hist, sketchContours'''
    copyframe = frame
    detectedHand = locateObject(frame, hist)
    image, contours, _ = cv2.findContours(detectedHand, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    palmArea = 0; flag = None
    for (i, c) in enumerate(contours):
        area = cv2.contourArea(c)
        if area > palmArea:
            palmArea = area
            flag = i
    if flag is not None:
        cnt = contours[flag]
        if sketchContours:
            cv2.drawContours(frame, [cnt], 0, colour, thickness)
            return True, frame, cnt
        else:
            return True, frame, cnt
    else:
        return False, copyframe, []

def getDefects(cnt, sketchDefects, frame, refineDefects = None, refineMetric = None):
    copy = frame
    hull = cv2.convexHull(cnt, returnPoints = False)
    defects = cv2.convexityDefects(cnt, hull)
    if defects is None:
        return frame, None, None
    #print defects
    #defectPoints = range(defects.shape[0])
    if refineMetric is None:
        refineMetric = 0
    if refineDefects is None:
        refineDefects = False
    #print refineMetric
    if not refineDefects and sketchDefects:
        frame, defects = drawDefects(frame, defects, cnt, refineDefects, refineMetric)
        return frame, None, defects

    if not refineDefects and not sketchDefects:
        return frame, None, defects
    if refineDefects and sketchDefects:
        frame, refinedDefectList = drawDefects(frame, defects, cnt, refineDefects, refineMetric)
        return frame,refinedDefectList, defects
    if refineDefects and not sketchDefects:
        _, refinedDefectList = drawDefects(frame, defects, cnt, refineDefects, refineMetric)
        return frame, refinedDefectList, defects

def getFingertips(cnt, sketchFingertips, frame):
    frame, refined, defects = getDefects(cnt, False, frame, False, 0)
    print frame, refined, defects
    if refined is None and defects is None:
            return frame, None
    for i in range(len(defects)):
        refined[i] = refined[i][::-1]
    
    refined.sort()
    fingertips = refined[:5]
    print "\n\nrefined" , fingertips
    for i in range(len(fingertips)):
            fingertips[i] = fingertips[i][::-1]
    if sketchFingertips:
            for i in fingertips:
                    cv2.circle(frame,i,5,[0,0,255],-1)
            return frame, fingertips
    if not sketchFingertips:
            return frame, fingertips
            
    
