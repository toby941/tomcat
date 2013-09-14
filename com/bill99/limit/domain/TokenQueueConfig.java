package com.bill99.limit.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 按具体优先级维护token池中的各个队列参数信息
 * 
 * @author jun.bao
 * @since 2013年8月28日
 */
@XmlRootElement(name = "tokenQueueConfig")
public class TokenQueueConfig {

	private String id;
	private String tokenPoolId;
	private String value;
	private Date addTime;
	private Date updateTime;
	/**
	 * 持有token超时时间
	 */
	private Integer holdTimeout;

	/**
	 * 队列对应优先级
	 */
	private Integer priority;

	/**
	 * 请求token超时时间
	 */
	private Integer requestTimeout;

	/**
	 * 队列持有token数量
	 */
	private Integer tokenCount;

	@XmlElement
	public Integer getHoldTimeout() {
		return holdTimeout;
	}

	@XmlElement
	public Integer getPriority() {
		return priority;
	}

	@XmlElement
	public Integer getRequestTimeout() {
		return requestTimeout;
	}

	@XmlElement
	public Integer getTokenCount() {
		return tokenCount;
	}

	public void setHoldTimeout(Integer holdTimeout) {
		this.holdTimeout = holdTimeout;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public void setRequestTimeout(Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public void setTokenCount(Integer tokenCount) {
		this.tokenCount = tokenCount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTokenPoolId() {
		return tokenPoolId;
	}

	public void setTokenPoolId(String tokenPoolId) {
		this.tokenPoolId = tokenPoolId;
	}

	@XmlElement
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	@XmlElement
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "TokenQueueConfig [id=" + id + ", tokenPoolId=" + tokenPoolId
				+ ", value=" + value + ", addTime=" + addTime + ", updateTime="
				+ updateTime + ", holdTimeout=" + holdTimeout + ", priority="
				+ priority + ", requestTimeout=" + requestTimeout
				+ ", tokenCount=" + tokenCount + "]";
	}
}
