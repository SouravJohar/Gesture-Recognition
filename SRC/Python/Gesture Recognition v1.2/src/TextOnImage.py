import cv2
import numpy as np

def displayText(frame , text, textTimer, timerLimit, truthVal):
    if truthVal and textTimer <= timerLimit:
        cv2.putText(frame, text, (165,350), cv2.FONT_HERSHEY_SIMPLEX, 1, (255,255,255), 2, cv2.LINE_AA)
        textTimer += 1
    if textTimer >= timerLimit:
        truthVal = False
        textTimer = 0
    return truthVal, textTimer
