package gui;

import common.*;
import javafx.stage.Stage;

/**
 * Interface for controllers in the GUI framework.
 * Provides a set of methods for initializing frames, setting permissions, and managing main controllers.
 */
public interface IController {

    /**
     * Initializes the frame with the specified object.
     * Typically used to pass data or state required for the frame's setup.
     *
     * @param object the object used to initialize the frame.
     */
    public void initializeFrame(Object object);

    /**
     * Initializes the frame without any specific object.
     * Typically used for default or general setup.
     */
    public void initializeFrame();

    /**
     * Sets the user type permission level for the controller.
     * Used to adjust functionality or access based on the user's permissions.
     *
     * @param type the user type permission level.
     */
    public void setPermission(User.UserType type);

    /**
     * Sets a specific object for the controller.
     * This can be used to pass data or state to the controller.
     *
     * @param object the object to be set.
     */
    public void setObject(Object object);

    /**
     * Sets the main controller managing the current view or frame.
     * Allows the controller to communicate or interact with the overarching UI structure.
     *
     * @param controller the main controller to be set.
     */
    public void setMainController(MenuUIController controller);
}
