package common;

public class loginInfo {
	 private String name;
	    private String password;

	    // Constructor
	    public loginInfo(String name, String password) {
	        this.name = name;
	        this.password = password;
	    }
	    public String getName() {
	        return name;
	    }
	    // Getter for password
	    public String getPassword() {
	        return password;
	    }
	    @Override
	    public String toString() {
	        return name + "," + password;
	    }

	    // Static method to construct a loginInfo object from a string
	    public static loginInfo fromString(String data) {
	        if (data == null || data.isEmpty()) {
	            throw new IllegalArgumentException("Input string cannot be null or empty");
	        }

	        String[] parts = data.split(",", 2); // Split the string into name and password
	        System.out.println(parts[0]+"   "+parts[1]);
	        if (parts.length < 2) {
	            throw new IllegalArgumentException("Input string must be in the format 'name,password'");
	        }

	        String name = parts[0].trim();
	        String password = parts[1].trim();

	        return new loginInfo(name, password);
	    }
}
