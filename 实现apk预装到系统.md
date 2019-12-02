##实现apk预装到系统

```
adb push /../demo.apk /sdcard/demo.apk

adb shell su

#解决在使用chmod 命令时可能出现  Unable to chmod autoconnwifi-debug.apk: Read-only file system  错误
mount -o rw,remount -t yaffs2 /dev/block/mtdblock3 /system

chmod 777 system

chmod 777 /system/app

chmod 777 /system/app/demo

cp /sdcard/demo.apk /system/app/demo/demo.apk

chmod 755 system

chmod 755 /system/app

mkdir /system/app/demo

chmod 755 /system/app/demo

chmod 666 /system/app/demo/demo.apk

reboot
```