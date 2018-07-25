Ext.define('erp.view.oa.persontask.workDaily.Daily',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
	    	  region: 'south',         
	    	  xtype:'erpDatalistGridPanel', 
	    	  anchor: '100% 100%',
	    	  selModel: Ext.create('Ext.selection.CheckboxModel',{
	    	  }),
	    	  tbar:[ {
	    	    	iconCls: 'group-add',
	    	    	id: 'ok',
	    			text: '确&nbsp;定'
	    	  },{
	    	    	iconCls: 'x-button-icon-close',
	    	    	text: '关&nbsp;闭',
	    			id: 'close',
	    			handler: function(){
	    				var win = parent.Ext.ComponentQuery.query('window');
						if(win){
							Ext.each(win, function(){
								this.close();
							});
						} else {
							window.close();
						}
	    	    	}
	    	  }]
	    	  
	    }]
		});
		me.callParent(arguments); 
	}
});