import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class Main {

    //TODO: speed up the drawing process

    private static String getLinesAndColums(String path) throws FileNotFoundException{
        int lines = 0;
        int colums = 0;

        Scanner lineScanner = new Scanner(new File(path));
        Scanner columScanner = null;

        while (lineScanner.hasNextLine()){
            columScanner = new Scanner(lineScanner.nextLine());
            columScanner.useDelimiter(",");
            while (columScanner.hasNext()){
                columScanner.next();
                colums++;
            }
            lines++;
        }

        colums = colums / lines + 1;

        columScanner.close();
        lineScanner.close();
        return colums + "-" + lines;
    }

    private static int getFreqStep(String[][] dataArray){
        int rec = 1;
        for (int y = 0; y < dataArray.length - 1; y++){
            if (dataArray[y][1].equals(dataArray[y+1][1])){
                rec++;
                //System.out.println(dataArray[y][1]);
            } else {
                break;
            }
        }
        //System.out.println("Rec " + rec);
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

    private static void copyArray(String[][] raw, double[][] helper){
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

        if (print)
            printArray(data);

        normalizeValues(getMax(data), getMin(data), data);
    }

    private static void normalizeValues(double max, double min, double[][] dbArray){
        for (int y = 0; y < dbArray.length; y++){
            for (int x = 0; x < dbArray[0].length; x++){
                dbArray[y][x] = (dbArray[y][x] - min) / (max - min);
            }
        }
    }

    private static void drawScale(String[][] raw, double[][] data, int width, int height) {
        //TODO: implement this Method
        double range =  Double.parseDouble(raw[raw.length - 1][3]) - Double.parseDouble(raw[0][2]);

        int step = getFreqStep(raw);

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.002);

        StdDraw.setFont(new Font("Arial", Font.BOLD, 7));

        for (int i = 0; i < step; i++){
            double hertz = Double.parseDouble(raw[i][2]);
            hertz /= 1e6;
            StdDraw.line(i * (raw[0].length - 6) + ((raw[0].length - 6) / 2), height - 5, i * (raw[0].length - 6) + ((raw[0].length - 6) / 2), height - 10);
            StdDraw.line(i * (raw[0].length - 6), height, i * (raw[0].length - 6), height - 10);
            StdDraw.textLeft(i * (raw[0].length - 6) + 4, height - 5, hertz+" MHz");
        }
    }

    private static void drawHeatmap(double[][] data, String[][] raw){
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

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 10));
        StdDraw.textLeft(20, 40, " "+raw[0][0]);
        StdDraw.textLeft(20, 25,  raw[0][1] + " to" + raw[raw.length - 1][1]);
        StdDraw.textLeft(20, 10, raw[0][2] + " -" + raw[raw.length - 1][3] + " Hz");

        drawScale(raw, data, width, height);

        StdDraw.show();

    }

    public static void main(String[] args) throws FileNotFoundException {
        String PATH = "airband.csv";
        

        String filename = "survey";
        String fileExtension = ".png";

        int lines = Integer.parseInt(getLinesAndColums(PATH).split("-")[1]);
        int columns = Integer.parseInt(getLinesAndColums(PATH).split("-")[0]);

        String[][] raw = new String[lines][columns];

        readFile(PATH, raw);

        int freqStep = getFreqStep(raw);

        double[][] data = new double[lines / freqStep][(columns - 6) * freqStep];

        System.out.println(raw[0][0] + " from" + raw[0][1] + " to" + raw[raw.length - 1][1]);
        System.out.println("Frequency Range: " + raw[0][2] + " -" + raw[raw.length - 1][3] + " Hz");
        System.out.println("Parsing Data...");
        parseArray(raw, data, false);
        System.out.println("Data dimensions: X: " + data[0].length + " Y: " + data.length);

        System.out.println("Drawing Heatmap...");
        drawHeatmap(data, raw);

        //printArray(raw);
        
        System.out.println("Finished");
        StdDraw.save(filename+raw[0][1]+fileExtension);
        System.out.println(filename+raw[0][1]+fileExtension + " saved sucessfully");

    }
}
