import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {

    //TODO: implement proper command line interface
    //TODO: improve scale

    /**
     * Software for creating a Heatmap from rtl_power data
     */

    private static boolean DEBUGGING_MODE = false;

    private static String getLinesAndColums(String path) throws FileNotFoundException{
        int lines = 0;
        int colums = 0;

        Scanner lineScanner = new Scanner(new File(path));
        Scanner columScanner = null;

        while (lineScanner.hasNextLine()){
            columScanner = new Scanner(lineScanner.nextLine());
            columScanner.useDelimiter(",");
            colums = 0;

            while (columScanner.hasNext()){
                columScanner.next();
                colums++;
            }
            lines++;
        }

        columScanner.close();
        lineScanner.close();
        if (DEBUGGING_MODE){
            System.out.println("Lines: " + lines);
            System.out.println("Columns: " + colums);
        }
        return colums + "-" + lines;
    }

    private static void readFile(String path, String[][] dataArray) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        Scanner dataScanner = null;

        String data = "";

        int x = 0;
        while (scanner.hasNextLine()){
            dataScanner = new Scanner(scanner.nextLine());
            dataScanner.useDelimiter(",");
            int y = 0;
            while (dataScanner.hasNext()){
                data = dataScanner.next();
                dataArray[x][y] = data;
                y++;
            }
            x++;
        }
        scanner.close();
    }

    private static int getFreqStep(String[][] dataArray){
        int rec = 1;
        for (int y = 0; y < dataArray.length - 1; y++){
            if (dataArray[y][1].equals(dataArray[y+1][1])){
                rec++;
            } else {
                break;
            }
        }
        if (DEBUGGING_MODE){
            System.out.println("Frequency Step: " + rec);
        }
        return rec;
    }

    private static void printArray(String[][] array){
        for (int i = 0; i < array.length; i++){
            for (int j = 0; j < array[0].length; j++){
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void printArray(double[][] array){
        for (int i = 0; i < array.length; i++){
            for (int j = 0; j < array[0].length; j++){
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static double getMax(double[][] array){
        double max = Double.MIN_VALUE;
        for (int i = 0; i < array.length; i++){
            for (int j = 0; j < array[0].length; j++){
                if (array[i][j] > max){
                    max = array[i][j];
                }
            }
        }
        return max;
    }

    private static double getMin(double[][] array){
        double min = Double.MAX_VALUE;
        for (int i = 0; i < array.length; i++){
            for (int j = 0; j < array[0].length; j++){
                if (array[i][j] < min){
                    min = array[i][j];
                }
            }
        }
        return min;
    }

    private static void copyArray(String[][] raw, double[][] helper) {
        for (int i = 0; i < raw.length; i++){
            for (int j = 6; j < raw[0].length; j++){
                if (raw[i][j] == null){
                    raw[i][j] = "0.0";
                }
                helper[i][j-6] = Double.parseDouble(raw[i][j]);
            }
        }
    }

    private static void parseArray(String[][] raw, double[][] data, boolean print){
        int freqStep = getFreqStep(raw);

        double[][] helper = new double[raw.length][raw[0].length - 6];

        copyArray(raw, helper);

        int index = 0;
        for (int y = 0; y < helper.length; y++){

            for (int x  = 0; x < helper[0].length; x++){
                if (index == data[0].length)
                   index = 0;
                data[y / freqStep][index] = helper[y][x];
                index++;
            }
        }

        if (print || DEBUGGING_MODE)
            printArray(data);

        normalizeValues(getMax(data), getMin(data), data);
    }

    private static void normalizeValues(double max, double min, double[][] data){
        for (int y = 0; y < data.length; y++){
            for (int x = 0; x < data[0].length; x++){
                data[y][x] = (data[y][x] - min) / (max - min);
            }
        }
    }

    private static void drawScale(String[][] raw, double[][] data, int width, int height) {
        //TODO: improve
        int step = getFreqStep(raw);

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.002);

        StdDraw.setFont(new Font("Arial", Font.BOLD, 7));

        for (int i = 0; i < step; i++){
            double hertz = Double.parseDouble(raw[i][2]);
            hertz /= 1e6;
            StdDraw.line(i * (raw[0].length - 6) + (raw[0].length - 6) / 2, height - 5, i * (raw[0].length - 6) + (raw[0].length - 6) / 2, height - 10);
            StdDraw.line(i * (raw[0].length - 6), height - 3, i * (raw[0].length - 6), height - 10);
            StdDraw.textLeft(i * (raw[0].length - 6) + 4, height - 5, hertz+" MHz");
        }
    }

    private static void drawHeatmap(double[][] data, String[][] raw, boolean scale, boolean label){
        int height = data.length;
        int width = data[0].length;

        height += 10;
        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.enableDoubleBuffering();

        long startTime;
        long endTime;

        for (int y = 0; y < data.length; y++){
            startTime = System.nanoTime();
            for (int x = 0; x < data[0].length; x++){
                float hue = (float) data[y][x];
                StdDraw.setPenColor(Color.getHSBColor(hue, 1.0f, 0.8f));
                StdDraw.point(x, y);
            }
            endTime = System.nanoTime();
            if (y == 0){
                double duration = (endTime - startTime) * data.length / 1e9;
                System.out.println("Estimated time to completion: " + (int) duration + " seconds.");
            }
        }

        if (label){
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 10));
            StdDraw.textLeft(10, 40, " "+raw[0][0]);
            StdDraw.textLeft(10, 25,  raw[0][1] + " to" + raw[raw.length - 1][1]);
            StdDraw.textLeft(10, 10, raw[0][2] + " -" + raw[raw.length - 1][3] + " Hz");

        }

        if (scale)
            drawScale(raw, data, width, height);

        StdDraw.show();
    }

    private static void printInfo(){
        System.out.println("Usage: java -jar RadioHeatmap.jar -f file -i image -t filetype [OPTIONS]");
        System.out.println();
        System.out.println("    -f, --file       path to csv source file [-f example.csv]");
        System.out.println("    -i, --image      name of target image file [-i example]");
        System.out.println("    -t, --type       image file type [-t png/jpeg]");
        System.out.println("    -p, --print      Print raw data");
        System.out.println("    -h, --help       help");
        System.out.println("    -s, --scale      draw scale on heatmap");
        System.out.println("    -l, --label      draw label on heatmap");
        System.out.println("    -deb             debugging mode");
        System.out.println();
        System.out.println("Example: java -jar RadioHeatmap.jar -f survey.csv -i survey -t png");
    }

    private static String[] cli(String[] args){
        String[] arguments = new String[6];

        if (args.length == 0){
            System.out.println("Not enough arguments");
            printInfo();
            System.exit(1);
        }
        for (int i = 0; i < args.length; i++){
            switch (args[i]){
                case  "-h":
                case "--help":
                    printInfo();
                    System.exit(1);
                    break;
                case "-v":
                case "--version":
                    System.out.println("Version 1.0 (14.01.2019)");
                    System.exit(1);
                    break;
                case "-f":
                    arguments[0] = args[i+1];
                    break;
                case "-i":
                    if (args[i].contains(".")){
                        arguments[1] = args[i].split(".")[0];
                    }
                    arguments[1] = args[i+1];
                    break;
                case "-t":
                    if (!(args[i].equals("png") || !(args[i].equals("jpeg"))))
                        arguments[2] = "png";
                    arguments[2] = args[i+1];
                    break;
                case "-s":
                    arguments[3] = "true";
                    break;
                case "-l":
                    arguments[4] = "true";
                    break;
                case "-p":
                    arguments[5] = "true";
                    break;
                case "-deb":
                    DEBUGGING_MODE = true;
                    break;
                default:
                    System.out.println();
            }

        }

        return arguments;

    }

    public static void main(String[] args) throws FileNotFoundException {
        String[] arguments = cli(args);

        String PATH = arguments[0];
        String filename = arguments[1];
        String fileExtension = "."+arguments[2];
        boolean scale, label, print;
        scale = Boolean.parseBoolean(arguments[3]);
        label = Boolean.parseBoolean(arguments[4]);
        print = Boolean.parseBoolean(arguments[5]);

        System.out.println("Free Software by Jakob Maier (2019)");
        System.out.println("https://github.com/gue-ni/heatmap.git");
        System.out.println();


        String[] linesAndColumns = getLinesAndColums(PATH).split("-");
        int lines = Integer.parseInt(linesAndColumns[1]);
        int columns = Integer.parseInt(linesAndColumns[0]);

        String[][] raw = new String[lines][columns];

        System.out.println("Reading file... (" + lines + " Lines)");
        readFile(PATH, raw);

        int freqStep = getFreqStep(raw);

        double[][] data = new double[lines / freqStep][(columns - 6) * freqStep];

        System.out.println(raw[0][0] + " from" + raw[0][1] + " to" + raw[raw.length - 1][1]);
        System.out.println("Frequency Range: " + raw[0][2] + " -" + raw[raw.length - 1][3] + " Hz");
        System.out.println("Parsing Data...");
        parseArray(raw, data, print);
        System.out.println("Data dimensions: " + data[0].length + " x " + data.length);

        System.out.println("Drawing Heatmap...");
        drawHeatmap(data, raw, scale, label);

        if (print)
            printArray(raw);

        System.out.println("Finished.");
        StdDraw.save(filename+fileExtension);
        System.out.println(filename+fileExtension + " saved sucessfully");
    }
}
