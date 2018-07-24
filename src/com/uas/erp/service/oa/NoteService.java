package com.uas.erp.service.oa;
import java.util.Map;
public interface NoteService {
	void saveNote(String formStore, String  caller);
	void updateNote(String formStore, String  caller);
	void deleteNote(int no_id, String  caller);
	Map<String, Object> getNote(int id, String  caller);
	String saveReadStatus();
}
