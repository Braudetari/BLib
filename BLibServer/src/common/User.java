package common;

public class User {
	private String username = null;
	private String password = null;
	private UserType type = null;
	public static enum UserType {SUBSCRIBER(0), LIBRARIAN(1), GUEST(2);
		private final int value;
		UserType(int value){
			this.value = value;
		}
		public static UserType fromInt(int value) {
			for(UserType type : UserType.values()) {
				if(type.getValue() == value) {
					return type;
				}
			}
			return null;
		}
		public int getValue() {
			return value;
		}
	};
	
	
	////////	Constructors	////////
	public User() {
		username=null;
		password=null;
		type = UserType.GUEST;
	}
	public User(String username, String password, UserType type) {
		this.username = username;
		this.password = password;
		this.type = type;
	}
	
	////////	SETTER/GETTER	////////
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public UserType getType() {
		return this.type;
	}
	public void setType(UserType type) {
		this.type = type;
	}
	
}
