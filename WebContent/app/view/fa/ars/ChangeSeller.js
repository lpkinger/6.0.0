Ext.define('erp.view.fa.ars.ChangeSeller',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		var w = Ext.isIE ? screen.width*0.6*0.45 : '45%',
				h = Ext.isIE ? screen.height*0.7*0.45 : '55%';
		Ext.apply(me, { 
			items: [{
				id:'ChangeSellerView',
				xtype: "window",
				autoShow: true,
				closable: false,
				maximizable : true,
		    	width: w,
		    	height: h,
		    	layout: 'border',
		    	items: [{
		    		region: 'center',
		    		xtype: 'ChangeSeller'
		    	}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});