package com.lwx.core.utils.http;

import java.io.InputStream;

/**
 * @Author liuax01
 * @Date 2018/1/15 16:48
 */
public class CertConfig {

	private String password; //证书密码

	private InputStream certStream; //证书

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public InputStream getCertStream() {
		return certStream;
	}

	public void setCertStream(InputStream certStream) {
		this.certStream = certStream;
	}
}
