# Radio Frequency Heatmap

![Example Waterfall](examples/survey_1.png)
Software for creating a heatmap from [`rtl_power`](https://github.com/keenerd/rtl-sdr) csv data collected using RTL-SDR software defined radio. Usefull for finding active frequencies in the rf spectrum. All decibel values are normalized and then drawn using Princeton's [ 'StdDraw' ](https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html) library. Blue represents the highest and red the lowest value. At the moment very large files cannot yet be processed. 


## Usage:
To see some examples, view [examples/survey_1.png](examples/survey_1.png) and [examples/survey_2.png](examples/survey_2.png). <br>

1. Download repository<br>
git clone https://github.com/gue-ni/heatmap.git <br>
cd build <br>

2. Collect signal data: <br>
  24h survey of the entire spectrum covered by the RTL-SDR:
  `rtl_power -f 24M:1700M:1M -i 100 -g 50 -e 24h data.csv` <br>
   Survey of the upper 19 MHz Airband for one hour. This can be used to find active air traffic control channels. <br>
  `rtl_power -f 118M:140M:8k -i 10 -g 50 1h airband.csv` <br>
  
3. Process data and create heatmap:<br>
  `java -jar RadioHeatmap.jar -f file.csv -i filename -t [png/jpeg] [OPTIONS]` <br> 
  Use this command to label the heatmap. <br>
  `java -jar RadioHeatmap.jar -f data.csv -i image -t png -l` <br>
  <br>
  Use this command to get help. <br>
  `java -jar RadioHeatmap.jar --help` <br>
  
 
  
## TODO:
Improve labeling. <br>
Enable processing of very large files. <br>



