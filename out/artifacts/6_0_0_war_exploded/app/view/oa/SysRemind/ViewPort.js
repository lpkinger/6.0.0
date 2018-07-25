Ext.define('erp.view.oa.SysRemind.ViewPort',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{      
	    	  anchor: '100% 100%',
	    	  xtype: 'erpFormPanel',
	    	  saveUrl: 'oa/note/saveNote.action',
			  deleteUrl: 'oa/note/deleteNote.action?caller=' +caller,
			  updateUrl: 'oa/note/updateNote.action?caller=' +caller,
			  getIdUrl: 'common/getId.action?seq=NOTE_SEQ'
	    }]
		});
		me.callParent(arguments); 
	}
});