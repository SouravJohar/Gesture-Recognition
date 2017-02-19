import numpy as np
import os
f = open("HandRecData.txt", "w")
f.write("")
while True:
    f = open("HandRecData.txt", "r")
    data = f.read()
    if data != "":
        try:
            attribute, value = data[0], data[1:]
 
            if attribute == "b":
                value = np.interp(int(value), [220,440], [0, 1])
                #print value
                os.system("brightness " + str(value))
            if attribute == "v":
                value = np.interp(int(value), [220,440], [0, 10])
                os.system("osascript -e " + "\"" + "set Volume " + str(value) + "\"")
        except:
            pass
    f.close()
