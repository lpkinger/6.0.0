package com.uas.pdaio.service;

public interface PdaioPowerDao{
	
	boolean  preSaveHandle(String caller);
	
	boolean  preDeleteHandle(String caller,Integer id);
	
	boolean  preChangeHandle(String caller,Integer id);
	
	boolean  preSeeAllHandle(String caller);
}
