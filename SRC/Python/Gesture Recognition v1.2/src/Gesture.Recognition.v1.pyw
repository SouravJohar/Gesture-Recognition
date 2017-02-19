import numpy as np
import cv2
import HistogramFunctionsAPI as hfa
import pyautogui as gui
import TextOnImage as toi
import math

def dist(a, b):
	return math.sqrt((a[0] - b[0])**2 + (b[1] - a[1])**2)

avgClick = False
clickStates = []
stateOne = False; stateTwo = False
defaultHist, palmHist = hfa.getNewObjectHist("palm")
adaptHist = False; textTimer = 1; frameCount = 0; mouseClicked = False; goodContour = True
screenWidth = gui.size()[0]; screenHeight = gui.size()[1]
cap = cv2.VideoCapture(0)
f = open(r"\\VBOXSVR\Code\HandRecData.txt", "w")
f.write("")

while True:
    frameCount += 1
    _, frame = cap.read()
    frame = cv2.flip(frame, 1)
    if defaultHist:
        textTimer, defaultHist = toi.displayText(frame, "Histogram recorded", textTimer, 30, defaultHist)
    if adaptHist:
        textTimer, adaptHist = toi.displayText(frame, "Histogram adapted", textTimer, 30, adaptHist)

    locatedPalm = hfa.locateObject(palmHist, frame)
    image, contours, _ = cv2.findContours(locatedPalm, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    palmArea = 15000; flag = "null"

    for (i, c) in enumerate(contours):
        area = cv2.contourArea(c)
        if area > palmArea:
            palmArea = area
            flag = i
    if flag != "null":
        cnt = contours[flag]
        hull = cv2.convexHull(cnt)
        if goodContour: #goodContour
                cv2.drawContours(frame, [cnt], 0, (0,255,0), 2)
        hull = cv2.convexHull(cnt, returnPoints = False)
        defects = cv2.convexityDefects(cnt, hull)
        fingerTips = range(defects.shape[0])
        contourMetric = len(fingerTips)
        for i in range(defects.shape[0]):
                s,e,f,d = defects[i,0]
                farPoint = tuple(cnt[s][0])
                fingerTips[i] = farPoint
        j = 0
        while j<len(fingerTips) -1:
                if dist(fingerTips[j], fingerTips[j+1]) < 10:
                        del fingerTips[j]
                j = j + 1
        yCoordinates = zip(*fingerTips)
        indexFinger = fingerTips[yCoordinates[1].index(min(list(yCoordinates[1])))]
        thumbFinger = fingerTips[fingerTips.index(min(fingerTips))]
        try:
                fingerDistance = int(dist(indexFinger, thumbFinger))
        except:
                fingerDistance = 0
        if fingerDistance < 10 or contourMetric > 21:
                goodContour = False
        else:
                if contourMetric < 20:
                        goodContour = True #add another criteria
        if goodContour: #goodContour
                cv2.circle(frame, thumbFinger, 5, (0,0,255), -1)
                cv2.circle(frame, indexFinger, 5, (0,0,255), -1)
                try:
                        clickMetric = (fingerDistance*100/100.0)/abs(thumbFinger[0] - indexFinger[0])
                except:
                        clickMetric = 0
                if clickMetric > 3 and  goodContour: #goodContour
                        mouseClicked = True
                        if len(clickStates) > 10:
                                del clickStates[0]
                                clickStates.append(mouseClicked)
                        else:
                                clickStates.append(mouseClicked)
                        if clickStates.count(True) >= 2:
                                avgClick = True
                        else:
                                avgClick = False

                else:
                        mouseClicked = False
                        if len(clickStates) > 10:
                                del clickStates[0]
                                clickStates.append(mouseClicked)
                        else:
                                clickStates.append(mouseClicked)
                        avgClick = [False, True][clickStates.count(True) >= 2]

                if stateTwo and avgClick:
                        cv2.circle(frame, (95, 117), 30, (0, 165, 255), -1)
                        cv2.rectangle(frame, (220, 100), (440, 135), (80, 127, 255), 2)
                        cv2.putText(frame, "Volume", (40, 175), cv2.FONT_HERSHEY_SIMPLEX, 1, (0,165,255), 1, cv2.LINE_AA)
                        f = open(r"\\VBOXSVR\Code\HandRecData.txt", "w")
                        if 90<indexFinger[1]<150:
                                cv2.circle(frame, (max(220, min(indexFinger[0], 440)),117) , 20, (71, 99, 255), -1)
                                f.write("v" + str(max(220, min(indexFinger[0], 440))))

                if stateOne and avgClick:
                        cv2.circle(frame, (95, 117), 30, (0, 165, 255), -1)
                        cv2.rectangle(frame, (220, 100), (440, 135), (80, 127, 255), 2)
                        cv2.putText(frame, "Brightness", (20, 175), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 165,255), 1, cv2.LINE_AA)
                        f = open(r"\\VBOXSVR\Code\HandRecData.txt", "w")
                        if 90<indexFinger[1]<150:
                                cv2.circle(frame, (max(220, min(indexFinger[0], 440)),117) , 20, (71, 99, 255), -1)
                                f.write("b" + str(max(220, min(indexFinger[0], 440))))

                if stateOne == False and stateTwo == False and 280<indexFinger[0]<360 and 30<indexFinger[1]<110 and avgClick:
                        stateOne = True

                if avgClick == False:
                        stateOne = False
                        stateTwo = False
                        f = open(r"\\VBOXSVR\Code\HandRecData.txt", "w")
                        f.write("")

                if stateTwo == False and stateOne ==False and 280<indexFinger[0]<360 and 130<indexFinger[1]<210 and avgClick:
                        stateTwo = True

    key = cv2.waitKey(10)
    if key == 108:
            adaptHist, palmHist = hfa.adaptObjectHistogram(frame)
    if key == 27:
            cv2.destroyAllWindows()
            cap.release()
            break
    if stateTwo == False and stateOne == False:
            cv2.circle(frame, (320, 170), 40, (0, 165, 255), 2)
            cv2.putText(frame, "V", (300, 190), cv2.FONT_HERSHEY_SIMPLEX, 2, (0,165,255), 2, cv2.LINE_AA)
    if stateOne == False and stateTwo == False:
            cv2.circle(frame, (320, 70), 40, (0, 165, 255), 2)
            cv2.putText(frame, "B", (300, 90), cv2.FONT_HERSHEY_SIMPLEX, 2, (0,165,255), 2, cv2.LINE_AA)
    cv2.imshow("VideoFeed", frame)
