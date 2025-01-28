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
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;

/**
 * Controller class for handling the Report Frame UI.
 * This class is responsible for generating and displaying reports, including bar and pie charts.
 * It also provides functionality for exporting charts as image files.
 */
public class ReportFrameController implements IController {

    /**
     * Button for exporting the bar chart as an image.
     */
    @FXML
    private Button btnExportBarChart;

    /**
     * Button for exporting the pie chart as an image.
     */
    @FXML
    private Button btnExportPieChart;

    /**
     * Bar chart for displaying loan times by genre.
     */
    @FXML
    private BarChart<String, Number> barChartGenres;

    /**
     * Pie chart for displaying subscriber statuses.
     */
    @FXML
    private PieChart pieChartSubscribers;

    /**
     * Button for generating reports.
     */
    @FXML
    private Button btnGenerate = null;

    /**
     * ObservableList to hold data for the pie chart.
     */
    ObservableList<PieChart.Data> pieChartData;

    /**
     * Initializes the report frame by generating initial charts.
     */
    @FXML
    private void initialize() {
        generate();
    }

    /**
     * Handles the action event triggered by the "Generate" button.
     * Generates the reports and updates the charts.
     * 
     * @param event The action event triggered by the button.
     */
    @SuppressWarnings("unchecked")
    @FXML
    public void handleGenerateBtn(ActionEvent event) {
        generate();
    }

    /**
     * Generates the bar chart and pie chart using data retrieved from the server.
     */
    private void generate() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();

        Object[] statusReport = ClientUI.chat.requestServerToGenerateStatusReport(year, month);
        Object[] loanReport = ClientUI.chat.requestServerToGenerateLoanReport(year, month);

        if (loanReport != null) {
            // Load bar chart data
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Loan Times by Genre");
            List<String> genreList = (List<String>) loanReport[0];
            List<Double> avgLoanList = (List<Double>) loanReport[1];
            for (int i = 0; i < genreList.size(); i++) {
                String genre = genreList.get(i);
                Double avgLoan = avgLoanList.get(i);
                series.getData().add(new XYChart.Data<>(genre, avgLoan));
            }
            barChartGenres.getData().clear();
            barChartGenres.getData().add(series);
        } else {
            showAlert("Error", "Could not generate loan reports, not enough data");
        }

        if (statusReport != null) {
            int activeCount = (int) statusReport[0];
            int frozenCount = (int) statusReport[1];
            // Load pie chart data
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Active" + "(" + activeCount + ")", activeCount),
                new PieChart.Data("Frozen" + "(" + frozenCount + ")", frozenCount)
            );
            pieChartSubscribers.setData(pieChartData);
            pieChartSubscribers.setTitle("Subscriber Status");
        } else {
            showAlert("Error", "Could not generate subscriber status reports, not enough data");
        }
    }

    /**
     * Displays an alert dialog with the given title and message.
     * 
     * @param title The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Initializes the frame with a given object.
     * 
     * @param object The object used to initialize the frame.
     */
    @Override
    public void initializeFrame(Object object) {
        // TODO Auto-generated method stub
    }

    /**
     * Initializes the frame and generates initial data.
     */
    @Override
    public void initializeFrame() {
        initialize();
    }

    /**
     * Sets the permission level for the current user.
     * 
     * @param type The user type that defines permissions.
     */
    @Override
    public void setPermission(UserType type) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets an object in the controller.
     * 
     * @param object The object to set in the controller.
     */
    @Override
    public void setObject(Object object) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets the main controller for the UI.
     * 
     * @param controller The main menu controller.
     */
    @Override
    public void setMainController(MenuUIController controller) {
        // TODO Auto-generated method stub
    }

    /**
     * Exports a given chart as an image file.
     * 
     * @param chart The chart to export.
     * @param fileName The name of the file to save the chart as.
     */
    private void exportChartAsImage(Node chart, String fileName) {
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

    /**
     * Handles the action event triggered by the "Export Bar Chart" button.
     * Exports the bar chart as an image file.
     * 
     * @param event The action event triggered by the button.
     */
    @FXML
    private void handleExportBarChart(ActionEvent event) {
        exportChartAsImage(barChartGenres, "BarChartReport");
    }

    /**
     * Handles the action event triggered by the "Export Pie Chart" button.
     * Exports the pie chart as an image file.
     * 
     * @param event The action event triggered by the button.
     */
    @FXML
    private void handleExportPieChart(ActionEvent event) {
        exportChartAsImage(pieChartSubscribers, "PieChartReport");
    }
}