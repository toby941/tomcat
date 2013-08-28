package com.bill99.limit.service.token;

import java.util.Calendar;
import java.util.Date;

/**
 * @author jun.bao
 * @since 2013年7月29日
 */
public class Token {

	private String name;
	private Integer priority;

	private Date bornTime;

	private Date lastAssessTime;

	public Token() {
		super();
	}

	public Token(String name, Integer priority) {
		super();
		this.name = name;
		this.priority = priority;
		Date nowTime = Calendar.getInstance().getTime();
		bornTime = nowTime;
		lastAssessTime = nowTime;
	}

	public void reset() {
		Date nowTime = Calendar.getInstance().getTime();
		lastAssessTime = nowTime;
	}

	public Date getBornTime() {
		return bornTime;
	}

	public Date getLastAssessTime() {
		return lastAssessTime;
	}

	public String getName() {
		return name;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setBornTime(Date bornTime) {
		this.bornTime = bornTime;
	}

	public void setLastAssessTime(Date lastAssessTime) {
		this.lastAssessTime = lastAssessTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

}
