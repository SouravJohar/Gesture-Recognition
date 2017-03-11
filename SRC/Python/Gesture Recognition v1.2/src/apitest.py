import cv2 
import numpy as np
import api

hist = api.captureHistogram()
cap = cv2.VideoCapture(0)
while True:
    _, frame = cap.read()
    ret, frame, contours  = api.detectHand(frame, hist, sketchContours = True)
    #print ret
    if ret:
        frame, refined = api.getFingertips(contours, True, frame)

    cv2.imshow("frame", frame)
    k = cv2.waitKey(10)
    if k == 27:
        cv2.destroyAllWindows()
        cap.release
        break
