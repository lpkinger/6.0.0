Ext.define('erp.view.co.cost.MakeCostUnClose',{ 
	extend: 'Ext.Viewport',
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	}, 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				width: 450,
				height: 300,
	    		xtype: 'form',
	    		title: '主营成本结转凭证取消',
	    		cls: 'singleWindowForm',
				bodyCls: 'singleWindowForm',
	    		bodyStyle: 'background:#f1f1f1',
		    	buttonAlign: 'center',
		    	buttons: [{
		    		iconCls:'x-button-icon-check',
	    			cls: 'x-btn-gray',
	    			id: 'unclose',
	    			text: '取消凭证',
	    			width: 100
	    		},{
	    			xtype:'erpCloseButton'
	    		}]
			}] 
		}); 
		me.callParent(arguments); 
	}
});