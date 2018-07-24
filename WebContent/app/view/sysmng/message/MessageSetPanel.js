/**
 * 功能升级面板
 */
Ext.define('erp.view.sysmng.message.MessageSetPanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.erpMessageSetPanel',
	id: 'messagepanel', 
	border: false,
	requires : [ 'erp.view.sysmng.message.MessageSetBar',
				 'erp.view.sysmng.message.MessagenavPanel'
				 
	 			 ],
	layout : 'border',
	items: [ {
		region : 'west',
		width : 150,
		xtype : 'messagesetbar'
	}, {
		region : 'center',
		layout : 'border',
		bodyBorder : false,
		items : [ {
			region : 'center',
			xtype : 'messagenavpanel'
		} ]
	}
	
	],
	initComponent : function(){ 
		this.callParent(arguments);		
	}

});