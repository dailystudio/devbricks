count=3
sleeptime=1

total=`expr $count \* $sleeptime`

echo "Welcome to use DailyStudio product."
echo "This script will perform a $total second(s) sleep separately in $count loop(s)"
echo 

i=1  
while [ $i -le $count ]  
do  
    echo "sleep: $i times with $sleeptime second(s)"  
    i=`expr $i + 1`
    sleep $sleeptime
done  
