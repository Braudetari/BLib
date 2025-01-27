package gui;

import common.*;
import javafx.stage.Stage;

public interface IController {
	public void initializeFrame(Object object);
	public void initializeFrame();
	public void setPermission(User.UserType type);
	public void setObject(Object object);
	public void setMainController(MenuUIController controller);
}
