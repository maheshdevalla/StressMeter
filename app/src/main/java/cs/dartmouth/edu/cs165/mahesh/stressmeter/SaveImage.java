package cs.dartmouth.edu.cs165.mahesh.stressmeter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class SaveImage extends AppCompatActivity {

    //Stress number to identify each image.
    private int[] stress_numbers = {6, 8, 14, 16, 5, 7, 13, 15, 2, 4, 10, 12, 1, 3, 9, 11};
    private int stress_number = 0;
    private static final String uri = "image_uri";
    private static final String image_location = "position";
    private static final int EXIT_APPLICATION = 0x0001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_image);
        Intent intent = getIntent();
        int image = intent.getIntExtra(uri, 0);
        stress_number = intent.getIntExtra(image_location, 0);
        ImageView imageView = (ImageView) findViewById(R.id.showimage);
        imageView.setImageResource(image);

    }

    /*
     * Called when the cancel button is clicked
     */

    public void onCancel(View view) {
        finish();
    }

    /*
     * Called when the submit button is clicked
     */
    public void onSubmit(View view) throws FileNotFoundException{
        int stress = stress_numbers[stress_number];
        createGraph("stress_timestamp.csv", System.currentTimeMillis(), stress);
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("flag", EXIT_APPLICATION);
        startActivity(intent);
    }

    /*
     * Write the time and the stress value to the csv file
     */
    public void createGraph(String filename, long time, int stress) throws FileNotFoundException{
        try {
            OutputStream outputstreamdata_reader = openFileOutput(filename, MODE_APPEND);
            OutputStreamWriter outputstreamdata_writer = new OutputStreamWriter(outputstreamdata_reader);
            outputstreamdata_writer.write(time + "," + stress + "\r\n");
            outputstreamdata_writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
