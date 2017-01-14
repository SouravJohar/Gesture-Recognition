import numpy as np
import cv2

cap = cv2.VideoCapture(0)
while True:
    #horizontal flip the image for better user experience
    frame, imgh = cap.read()
    img = imgh.copy()
    img = cv2.flip(imgh, 1)
#    print img.shape
    
    key = cv2.waitKey(10)
    #white overlay
    #cv2.rectangle(img,(0,0),(640,480),(255,255,255),-15)
    #drawing the required recatngles at the required places
    #rectangle size = 50x50
    cv2.rectangle(img,(320,240),(320,240),(0,0,0),3)
    cv2.rectangle(img,(310-50,230-50),(310,230),(0,255,0),3)
    cv2.rectangle(img,(330,230-50),(330+50,230),(0,255,100),3)
    cv2.rectangle(img,(310 -50,250),(310,250 + 50),(0,255,100),3)
    cv2.rectangle(img,(330,250),(330 +50,250 + 50),(0,255,100),3)
    
    cv2.imshow("Tester", img)

    #not important
    if key == 27:
        cv2.destroyAllWindows()
        break
cap.release()
    
