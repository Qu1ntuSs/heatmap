# Heatmap 

![Example Waterfall](examples/survey_1.png)
Software for creating a heatmap from [`rtl_power`](https://github.com/keenerd/rtl-sdr) csv data collected using RTL-SDR software defined radio. Usefull for finding active frequencies.


## Usage:

1. Download repository<br>
git clone https://github.com/gue-ni/heatmap.git <br>
cd build <br>
2. Collect data:
  `rtl_power -f 24M:1700M:1M -i 100 -g 50 -e 24h data.csv`
3. Process data<br>
  `java -jar RadioHeatmap.jar -f file -i image -t filetype`
  `java -jar RadioHeatmap.jar --help` 
  
## TODO:
Improve labeling. <br>
Enable processing of very large files. <br>



