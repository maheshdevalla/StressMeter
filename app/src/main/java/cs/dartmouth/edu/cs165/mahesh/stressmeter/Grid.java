package cs.dartmouth.edu.cs165.mahesh.stressmeter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class Grid extends Fragment {


    private GridView gridview;  //Define the grid view as the container of the image
    @SuppressWarnings("FieldCanBeLocal")
    private Button button;
    private List<Integer> images;   //list of all images
    private static final String uri = "image_uri";
    private static final String image_location = "position";
    private int images_stress_number = 0;
    public int getImages_stress_number() {
        return images_stress_number;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.gridview, container, false);

        gridview = (GridView) view.findViewById(R.id.gridview);
        images = getImages(getImages_stress_number());
        images_stress_number++;
        gridview.setAdapter(new ImageAdapter(getActivity()));
        /*
         *  Changing the set of images from
         *  one grid to other grid
         *  after selecting more_images button
         *
         */
        button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).endMediaPlayer();
                images = getImages(getImages_stress_number());
                images_stress_number++;
                gridview.setAdapter(new ImageAdapter(getActivity()));
            }
        });
        /*
            *  Moving the images
            *  one grid to other grid
            *
            *
        */
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).endMediaPlayer();
                Intent intent = new Intent(getActivity(), SaveImage.class);
                Bundle bundle = new Bundle();
                bundle.putInt(uri, images.get(position));
                bundle.putInt(image_location, position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    /*
     * Method to return the stress levels accoriding to the images.
     */
    public List<Integer> getImages(int index) throws NullPointerException{
        List<Integer> images_list = new ArrayList<Integer>();
        int id = index % 3 + 1;

        //calling the getGridById to get the result
        int []res = PSM.getGridById(id);
        if(res!=null) {
            for (int i = 0; i < res.length; i++) {
                images_list.add(res[i]);
            }
        }
        return images_list;
    }

    /*
     * Adapter for the grid view
     */
    class ImageAdapter extends BaseAdapter {
        Context context;
        public ImageAdapter(Context context) {
            this.context = context;
        }


      //return the view to the
       @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageview;
            if (null == convertView) {
                imageview = new ImageView(context);
                imageview.setLayoutParams(new GridView.LayoutParams(400, 400));
                imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageview.setPadding(0, 0, 0, 0);
            }
            else {
                imageview = (ImageView) convertView;
            }
            imageview.setImageResource(images.get(position));
            return imageview;
        }
        // Helper method to return the number of images
        @Override
        public int getCount() {
            return images.size();
        }

        // Helper method to return the position of image
        @Override
        public Object getItem(int position) {
            return position;
        }

        /*
         * helper method to return the stress level of the image.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
