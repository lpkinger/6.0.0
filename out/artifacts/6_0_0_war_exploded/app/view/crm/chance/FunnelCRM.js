Ext.define('erp.view.crm.chance.FunnelCRM',{ 
	extend: 'Ext.Viewport', 
	id:'ccc',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
		items: [{
			id:'funnelwin',
			xtype: "window",
			autoShow: true,
			closable: true,
			maximizable : false,
			buttonAlign: 'center',
			buttons: [
			          { 
			        	  id:'closebtn',
			        	  text: '关   闭' 
			        		  }
			        ],
			//height:400,
		//	width:300,
	    	width: '70%',
	    	height: '85%',
	    	layout: 'fit',
	    	fieldStyle : "background:#FFFAFA;color:#515151;",
	    }]
		});
		me.callParent(arguments); 
	}
});