arm-none-linux-gnueabi-gcc hello.c -o hellostatic -static
rem g:\tools\adb push hellostatic /data/ch13
rem g:\tools\adb shell "chmod 777 /data/ch13/hellostatic"


