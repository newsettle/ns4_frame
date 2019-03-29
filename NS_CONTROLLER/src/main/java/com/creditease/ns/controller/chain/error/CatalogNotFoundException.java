package com.creditease.ns.controller.chain.error;

import com.creditease.framework.exception.NSException;

public class CatalogNotFoundException extends NSException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CatalogNotFoundException() {
		super();
	}
	
	public CatalogNotFoundException(String desc)
	{
		super(desc);
	}
}
