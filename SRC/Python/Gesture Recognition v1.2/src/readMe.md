Designed specifically for a Mac running Windows (with OpenCV) installed on a VirtualMachine, but the majority of the code is OS independent.

Two APIs, `HistogramFunctions` and `TextOnImage` have been written to provide universal support.
`HistogramFunctions` defines functions to store the histogram for a particular object, locate an object in a image using a histogram, among others.
`TextOnImage` is a simple API for displaying text on a Video Feed for specified number of frames.

Directions to use:
1. Record your hand histogram.
2. `Click` on a button with your hand, and drag it to adjust its value.
3. Press `esc` to exit.

`Click` is defined in a gif file, in this folder.

Dependecies:
1.`OpenCV`, obviously.
2.`pyautogui`, for mouse control using your hand. (Under development)
3.`brightness` or `nircmd` (command line tools) for MacOS and Windows, respectively, depending on the platfrom you use.

For best results, try to keep your face as much as possible out of the videofeed. Use application with a clear background, different from skin color.

Note:
`virtualLinker.py` is a file specific to my needs, as I was using OpenCV on Windows, on a VirtualMachine on MacOS.
This .py file has to be running on MacOS while using the Gesture Recognition application.
Since a virtual OS has no access to the hardware attributes (brightness and volume) of the host, I had to
write to a file in a `shared folder` from my Virtual Windoews, and `virtualLinker.py` reads this file, and applies the changes on MacOS. Hard work, I tell you.

However, if you are using Windows normally, `nircmd` will be perfecr for you, no complications.

