package com.bill99.limit.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 客户端context配置信汇总类
 * 
 * @author jun.bao
 * @since 2013年9月9日
 */
@XmlRootElement(name = "contextConfig")
public class ContextConfig {

	private String id;
	/**
	 * 应用机器节点数量
	 */
	private Integer nodeCount;
	/**
	 * 与客户端应用一致的context name,用于匹配对应
	 */
	private String contextName;

	/**
	 * 定义每次发送快照的时间间隔
	 */
	private Integer snapshotSendInterval;

	/**
	 * 定义各个token队列的详细配置参数
	 */
	private List<TokenPoolConfig> tokenPoolConfigs;

	@XmlElement
	public String getContextName() {
		return contextName;
	}

	@XmlElement
	public Integer getSnapshotSendInterval() {
		return snapshotSendInterval;
	}

	@XmlElements({ @XmlElement(name = "tokenPoolConfig", type = TokenPoolConfig.class) })
	public List<TokenPoolConfig> getTokenPoolConfigs() {
		return tokenPoolConfigs;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public void setSnapshotSendInterval(Integer snapshotSendInterval) {
		this.snapshotSendInterval = snapshotSendInterval;
	}

	public void setTokenPoolConfigs(List<TokenPoolConfig> tokenPoolConfigs) {
		this.tokenPoolConfigs = tokenPoolConfigs;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement
	public Integer getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(Integer nodeCount) {
		this.nodeCount = nodeCount;
	}

	@Override
	public String toString() {
		return "ContextConfig [id=" + id + ", nodeCount=" + nodeCount
				+ ", contextName=" + contextName + ", snapshotSendInterval="
				+ snapshotSendInterval + ", tokenPoolConfigs="
				+ tokenPoolConfigs + "]";
	}
}
