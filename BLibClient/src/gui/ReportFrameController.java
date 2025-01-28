package gui;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.imageio.ImageIO;

import client.ClientUI;
import common.User.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;

public class ReportFrameController implements IController{
	@FXML
	private Button btnExportBarChart;
	@FXML
	private Button btnExportPieChart;
	@FXML
	private BarChart<String, Number> barChartGenres;
	@FXML
	private PieChart pieChartSubscribers;
	@FXML
	private Button btnGenerate=null;
	ObservableList<PieChart.Data> pieChartData;
	
	@FXML
	private void initialize() {
		generate();
	}

	@SuppressWarnings("unchecked")
	@FXML
	public void handleGenerateBtn(ActionEvent event) {
		generate();
	}
	
	private void generate() {
		LocalDate today = LocalDate.now();
		int month = today.getMonthValue();
		int year = today.getYear();
		Object[] statusReport = ClientUI.chat.requestServerToGenerateStatusReport(year, month);
		Object[] loanReport = ClientUI.chat.requestServerToGenerateLoanReport(year, month);
		if(loanReport != null) {
			//Load bar chart
		    XYChart.Series<String, Number> series = new XYChart.Series<>();
		    series.setName("Loan Times by Genre");
			List<String> genreList = (List<String>)loanReport[0];
		    List<Double> avgLoanList = (List<Double>)loanReport[1];
		    for(int i=0; i<genreList.size(); i++) {
		    	String genre = genreList.get(i);
		    	Double avgLoan = avgLoanList.get(i);
		    	series.getData().add(new XYChart.Data<>(genre, avgLoan));
		    }
		    barChartGenres.getData().clear();
		    barChartGenres.getData().add(series);
		}
		else {
			showAlert("error", "could not generate loan reports, not enough data");
		}
		if(statusReport != null) {
		    int activeCount = (int)statusReport[0];
		    int frozenCount = (int)statusReport[1];
		    //Load PieChart data
		    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
		            new PieChart.Data("Active" + "("+activeCount+")", activeCount),
		            new PieChart.Data("Frozen"+ "("+frozenCount+")", frozenCount)
		        );
	        pieChartSubscribers.setData(pieChartData);
	        pieChartSubscribers.setTitle("Subscriber Status");
		}
		else {
			showAlert("error", "could not generate subscriber status reports, not enough data");
		}
	}
	
	private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
	@Override
	public void initializeFrame(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeFrame() {
		initialize();
		
	}

	@Override
	public void setPermission(UserType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObject(Object object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMainController(MenuUIController controller) {
		// TODO Auto-generated method stub
		
	}
	
	private void exportChartAsImage(Node chart, String fileName) {
		//export chart as image in general 
		    WritableImage image = chart.snapshot(null, null);
		    
		    File file = new File(fileName + ".png");
		    try {
		        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		        System.out.println("Chart exported successfully to: " + file.getAbsolutePath());
		    } catch (IOException e) {
		        e.printStackTrace();
		        System.out.println("Failed to export chart.");
		    }
		}

		@FXML
		private void handleExportBarChart(ActionEvent event) {
		    exportChartAsImage(barChartGenres, "BarChartReport"); //specific barChart
		}

		@FXML
		private void handleExportPieChart(ActionEvent event) {
		    exportChartAsImage(pieChartSubscribers, "PieChartReport"); //specific pieChart 
		}
}
