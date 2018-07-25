Ext.define('erp.view.hr.emplmana.StartExam',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		var w = Ext.isIE ? screen.width*0.6*0.45 : '45%',
				h = Ext.isIE ? screen.height*0.7*0.45 : '45%';
		Ext.apply(me, { 
			items: [{
				id:'StartExamView',
				xtype: "window",
				autoShow: true,
				closable: false,
				maximizable : true,
		    	width: w,
		    	height: h,
		    	layout: 'border',
		    	items: [{
		    		region: 'center',
		    		xtype: 'erpFormPanel',
		    		caller:'StartExam',
		    		_noc:1,
		    		enableTools:false
		    	}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});