import java.io.*;
// test file for csv
public class reader {

    public static void main(String[] args) {

        String file ="C:\\Users\\sarah\\OneDrive\\Desktop\\cs4\\monsters.csv";
        BufferedReader reader = null;
        String line ="";

        try {
            reader = new BufferedReader(new FileReader(file));

            while((line = reader.readLine()) != null){
                String [] row = line.split(",");

                for(String index : row){
                    System.out.printf(index+"    ");
                }
                System.out.println();
            }

        } catch (Exception e){
            e.printStackTrace();

        } finally {
            try{
                if(reader != null)
                    reader.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}