package chapter6.beans;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

	private int id;
	private int userId;
	private String text;
	private Date createdDate;
	private Date updatedDate;

	// getter/setterは省略されているので、自分で記述しましょう。
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public boolean isEmpty() {
		return false;
	}

	public int getMessageId() {
		return 0;
	}

	public void setAccount(String string) {

	}

	public void setName(String string) {

	}

	public void add(String start) {

	}

}
