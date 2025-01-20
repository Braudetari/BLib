package common;

public class User {
	private int id;
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
	public User(int id, String username, String password, UserType type) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.type = type;
	}
	public User(User user) {
		this.id = user.id;
		this.password = user.password;
		this.type = user.type;
		this.username = user.username;
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
	
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
