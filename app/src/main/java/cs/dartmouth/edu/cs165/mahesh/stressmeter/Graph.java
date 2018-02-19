package cs.dartmouth.edu.cs165.mahesh.stressmeter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static android.support.v4.content.ContextCompat.getColor;
import static cs.dartmouth.edu.cs165.mahesh.stressmeter.R.id.chart;

public class Graph extends Fragment {

    private View view;
    @Nullable
    @Override
    public View getView() {
        return view;
    }

    //creating a csv file to later plot the graph
    private String csv_file = "stress_timestamp.csv";
    public String getCsv_file() {
        return csv_file;
    }


    private int stress_number = 0;      //Initial stress value
    private int stress_data_points = 0; //Stress points to be collected later
    List<PointValue> table_data_points = new ArrayList<>();  //table points to for saving in graph
    private List<String[]> table_data;  //table data to display

    @Override
    /*
     *  When fragment is instantiated inflating the view and reading data for graph
     *
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.graph, container, false);
        table_data = new ArrayList<>();
        // Reading the data similar to input stream readers files in java
        try {
            InputStream inputstream_reader = getActivity().openFileInput(getCsv_file());
            BufferedReader buffered_reader = new BufferedReader(new InputStreamReader(inputstream_reader));
            String readData;
            while ((readData = buffered_reader.readLine()) != null) {
                String[] readData_rows = readData.split(",");
                table_data.add(readData_rows);
                int value = Integer.parseInt(readData_rows[1]);
                stress_number = value > stress_number ? value : stress_number;
                table_data_points.add(new PointValue(stress_data_points++, value));
            }
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), "Issue occured with table file.", Toast.LENGTH_LONG).show();
        }
        makeGraph();
        makeTable();
        return view;
    }

    /*
       * adding data to the table
       * and making the table with stress levels
       *
    */
    @SuppressWarnings("unchecked")
    private void makeTable() throws ClassCastException,NullPointerException{
        TableView<String[]> tableView=null;
        try {
            tableView = (TableView<String[]>) getView().findViewById(R.id.stressTable);
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getActivity(), "Issue with table", Toast.LENGTH_LONG).show();
        }
        SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(getActivity(), "Time", "Stress");
        SimpleTableDataAdapter simpleTableDataAdapter = new SimpleTableDataAdapter(getActivity(), table_data);
        // setting the size and text for the data.
        if(tableView!=null) {
            tableView.setHeaderAdapter(simpleTableHeaderAdapter);
            simpleTableDataAdapter.setTextSize(10);
            simpleTableHeaderAdapter.setTextSize(15);
            tableView.setDataAdapter(simpleTableDataAdapter);
        }
        // helper methods to set the colors of rows in the table.

        int color1 = getColor(getContext(),android.R.color.holo_blue_light);
        int color2 = getColor(getContext(),android.R.color.holo_orange_dark);

        if(tableView!=null)
            tableView.setDataRowColoriser(TableDataRowColorizers.alternatingRows(color1, color2));
        android.widget.TableLayout.LayoutParams widget = new  android.widget.TableLayout.LayoutParams();
        widget.height = 150+92*stress_data_points;
        if(tableView!=null)
            tableView.setLayoutParams(widget);
    }
    /*
     * Making the graph by plotting the points
     *
     * Adding axis and making a margin
     */
    private void makeGraph() throws NullPointerException {
        Line graph_line = new Line(table_data_points).setColor(Color.BLACK).setCubic(true);
        graph_line.setFilled(true);
        List<Line> graph_line_data = new ArrayList<>();
        graph_line_data.add(graph_line);
                LineChartData graph_line_data_points = new LineChartData();
        Axis xaxis = new Axis().setHasLines(true);
        Axis yaxis = new Axis().setHasLines(true);
        xaxis.setName("Instance");
        yaxis.setName("StressLevel");
        graph_line_data_points.setAxisXBottom(xaxis);
        graph_line_data_points.setAxisYLeft(yaxis);
        graph_line_data_points.setLines(graph_line_data);
        try {
            LineChartView plot_graph = (LineChartView) getView().findViewById(chart);
            plot_graph.setLineChartData(graph_line_data_points);
            final Viewport view_graph_data = new Viewport(plot_graph.getMaximumViewport());
            view_graph_data.bottom = 0;
            view_graph_data.top = stress_number++;
            view_graph_data.left = 0;
            view_graph_data.right = stress_data_points == 0 ? 0 : stress_data_points - 1;
            plot_graph.setMaximumViewport(view_graph_data);
            plot_graph.setCurrentViewport(view_graph_data);
        } catch (NullPointerException e)
        {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Issue occurred while reading the csv file.", Toast.LENGTH_LONG).show();
        }
    }

}

