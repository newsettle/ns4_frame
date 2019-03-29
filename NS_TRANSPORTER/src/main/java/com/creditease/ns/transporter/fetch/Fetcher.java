package com.creditease.ns.transporter.fetch;

public interface Fetcher {
	public void fetch();
	public void stop() throws Exception; 
}
