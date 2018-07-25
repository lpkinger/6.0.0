Ext.define('erp.view.common.batchUpdate.Viewport',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		var width = Ext.isIE ? screen.width*0.7*0.8 : '70%',
	   			height = Ext.isIE ? screen.height*0.6 : '65%';
		Ext.apply(me, { 
		items: [{
			xtype: "window",
			title:'批量更新',
			autoShow: true,
			closable: false,
			maximizable : true,
	    	width: width,
	    	height: height,
	    	layout: 'anchor',
	    	items: [{
	    		xtype: 'erpBatchUpdateFormPanel',
	    		anchor:'100% 100%'
	    	},{
	    		xtype:'erpGridPanel2',
	    		hidden:true
	    	}],
		    buttonAlign:'center',
		    buttons:[{
		    	text: $I18N.common.button.erpCloseButton,
				iconCls: 'x-button-icon-close',
		    	cls: 'x-btn-gray',
		    	width: 60,
		    	handler: function(btn){
					var main = parent.Ext.getCmp("content-panel"); 
					if(main){
						main.getActiveTab().close();
					} 
		    	}
		    }]
	    }]
		});
		me.callParent(arguments); 
	}
});