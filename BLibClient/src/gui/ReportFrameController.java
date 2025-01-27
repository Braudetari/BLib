package gui;

import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ReportFrameController {

	@FXML
	private BarChart<String, Number> barChartGenres;
	@FXML
	private PieChart pieChartSubscribers;
	@FXML
	private TextField monthField=null;
	@FXML
	private TextField yearField=null;
	@FXML
	private Button btnSearch=null;
	
	@FXML
	private void initialize() {
	    // Create a series for the bar chart
	    XYChart.Series<String, Number> series = new XYChart.Series<>();
	    series.setName("Loan Times by Genre");

	    // Add data to the series (replace these values with actual database queries)
	    series.getData().add(new XYChart.Data<>("Fiction", 15));    // Total days for Fiction
	    series.getData().add(new XYChart.Data<>("Non-Fiction", 20)); // Total days for Non-Fiction
	    series.getData().add(new XYChart.Data<>("Science", 10));    // Total days for Science
	    series.getData().add(new XYChart.Data<>("History", 25));    // Total days for History
	    series.getData().add(new XYChart.Data<>("Fantasy", 30));    // Total days for Fantasy
	    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
	            new PieChart.Data("Active", 120),
	            new PieChart.Data("Frozen", 30),
	            new PieChart.Data("Suspended", 10)
	        );

	        pieChartSubscribers.setData(pieChartData);
	        pieChartSubscribers.setTitle("Subscriber Status");

	    // Add the series to the bar chart
	    barChartGenres.getData().add(series);
	}
}
