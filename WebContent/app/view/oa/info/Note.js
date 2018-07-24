Ext.define('erp.view.oa.info.Note',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 100%',
					saveUrl: 'oa/note/saveNote.action',
					deleteUrl: 'oa/note/deleteNote.action?caller=' +caller,
					updateUrl: 'oa/note/updateNote.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=NOTE_SEQ',
					keyField: 'no_id' 
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});