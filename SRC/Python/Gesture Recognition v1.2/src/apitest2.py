import cv2
import numpy as np
import handRecognition as hr

hist = hr.captureHistogram(0)
cap = cv2.VideoCapture(0)
while True:
    _, frame = cap.read()
    ret, frame, contours, defects = hr.detectHand(frame, hist, sketchContours = True, computeDefects = True)
    fingertips = hr.extractFingertips(defects, contours, 50, right = True)
    hr.plot(frame, fingertips)
    cv2.imshow("frame", frame)
    k = cv2.waitKey(10)
    if k == 27:
        cv2.destroyAllWindows()
        cap.release()
        break
    if k == 97:
        hist = hr.adaptObjectHistogram(frame)
