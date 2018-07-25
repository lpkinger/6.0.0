Ext.define('erp.view.common.statistics', {
	extend : 'Ext.Viewport',
	id : 'stsViewport',
	layout : 'anchor',
	hideBorders : true,
	initComponent : function() {
		var btn = new Ext.Button({
			xtype : 'erpQueryButton',
			text:'筛 选',
			renderTo : 'div1',
				id:'statistic'
		});
		btn.render();
		var me = this;
		me.callParent(arguments);
	}

});