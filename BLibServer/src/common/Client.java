package common;

public class Client {
    private int userId;          // Primary key
    private String name;
    private String lastName;
    private String membershipId; // Unique field
    private String userName;     // Unique field
    private String password;
    private String email;        // Unique field
    private String phoneNumber;
    private String userType;      // Foreign key, 1 = librarian, 2 = client

    // Constructor
    public Client(int userId, String name, String lastName, String membershipId, 
                  String userName, String password, String email, 
                  String phoneNumber, String userType) {
        this.userId = userId;
        this.name = name;
        this.lastName = lastName;
        this.membershipId = membershipId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserTypeId(String userTypeId) {
        this.userType = userType;
    }

    // Overriding toString() for easy printing
    @Override
    public String toString() {
        return userId + "," +
               name + "," +
               lastName + "," +
               membershipId + "," +
               userName + "," +
               password + "," +  // Added password here
               email + "," +
               phoneNumber + "," +
               userType;
    }


    // Static method to create a Client object from a string
    public static Client fromString(String clientString) {
        if (clientString == null || clientString.isEmpty()) {
            throw new IllegalArgumentException("Input string is null or empty.");
        }

        // Example input format: "userId,name,lastName,membershipId,userName,password,email,phoneNumber,userType"
        String[] parts = clientString.split(",");
        if (parts.length != 9) {
            throw new IllegalArgumentException("Input string format is invalid.");
        }

        try {
            int userId = Integer.parseInt(parts[0]);
            String name = parts[1];
            String lastName = parts[2];
            String membershipId = parts[3];
            String userName = parts[4];
            String password = parts[5];
            String email = parts[6];
            String phoneNumber = parts[7];
            String userType = parts[8];

            return new Client(userId, name, lastName, membershipId, userName, password, email, phoneNumber, userType);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Error parsing userId or other numeric fields.", ex);
        }
    }
    
        public static void main(String[] args) {
            // Test 1: Creating a Client object manually and testing toString()
            Client client1 = new Client(
                1, "John", "Doe", "M123", "johndoe", "password123",
                "johndoe@example.com", "1234567890", "client"
            );

            // Printing the Client object using toString()
            System.out.println("Testing toString():");
            System.out.println(client1);

            // Test 2: Creating a Client object from a string and testing fromString()
            String clientData = "2,Jane,Doe,M124,janedoe,password456,janedoe@example.com,0987654321,librarian";

            try {
                Client client2 = Client.fromString(clientData);

                System.out.println("\nTesting fromString():");
                System.out.println(client2);
            } catch (IllegalArgumentException ex) {
                System.out.println("Error creating Client from string: " + ex.getMessage());
            }

            // Test 3: Handling invalid input for fromString()
            String invalidClientData = "Invalid,String,Data";

            try {
                Client invalidClient = Client.fromString(invalidClientData);
                System.out.println("\nThis should not print: " + invalidClient);
            } catch (IllegalArgumentException ex) {
                System.out.println("\nTesting invalid input:");
                System.out.println("Caught exception: " + ex.getMessage());
            }
        }
    }



