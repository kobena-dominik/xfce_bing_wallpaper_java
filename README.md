# xfce_with_bing_wallpaper
Update the xfce background with the bing wallpaper of the day

This java program lets you update the bing wallpaper of the day for Xfce4 environment desktop.

This program check first wether or not you already have the bing wallpaper of the day before downloading it. So it uses no bandwidth unnecessarily.
This program checks wether or not the bing wallpaper of the day is already set before setting it.
This program will wait for an internet connection if the download fail / 

So what are you waiting ???? Enjoy the bing wallpaper of the Day.

INSTALLATION

    First check that you have java installed on your system with : java --version
    If not please do install java first
    Download the source in your home directory or wherever you like : ~/xfce_bing_wallpaper/Xfce.java
    run these commands : cd ~/xfce_bing_wallpaper/ && javac Xfce.java && jar -cfe Xfce.jar Xfce Xfce.class

Now setup xfce

    Go in session & startup 
    Add this command in startup script 
    java -jar ~/xfce_bing_wallpaper/Xfce.jar
    
Bravo it's OK your background will be updated everyday with bing daily wallpaper
