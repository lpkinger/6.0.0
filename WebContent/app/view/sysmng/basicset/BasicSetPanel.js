/**
 * 后台设置面板
 */
Ext.define('erp.view.sysmng.basicset.BasicSetPanel', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.erpBasicSetPanel',
	id : 'basicsetpanel',
	border : false,
	/*
	 * layout: { type: 'hbox', pack: 'start', align: 'stretch' },
	 */
	layout : 'border',
	requires : [ 'erp.view.sysmng.basicset.fixed.FixedPanel',
			'erp.view.sysmng.basicset.dictionary.DictionaryPanel' ],
	closeAction : 'hide',
	items : [ {
		region : 'west',
		width : 150,
		//style : 'top:0px!important;margin-left:120px !important',
		xtype : 'basicsetbar'
	}, {
		region : 'center',
		layout : 'border',
		bodyBorder : false,
		items : [ {
			region : 'center',
			xtype : 'basicnavpanel'
		} ]
	} ],
	initComponent : function() {
		this.callParent(arguments);
	}

});