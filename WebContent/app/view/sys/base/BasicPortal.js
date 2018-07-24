Ext.define('erp.view.sys.base.BasicPortal',{
	extend: 'Ext.panel.Panel', 
	alias: 'widget.basicportal',
	id:'basicportal',
	flag:'',
	labelSeparator : ':',
	buttonAlign : 'center',
	bodyStyle : 'background:#f9f9f9;top:0px!important',
	fieldDefaults : {
		msgTarget: 'none',
		blankText : $I18N.common.form.blankText
	},
	requires:['erp.view.sys.hr.GroupTabPanel'],
	layout:'fit',
	items: [{
		region:'west',
		xtype:'productkindtree',
		title:'物料种类',
		width:400,
		minWidth:1300
	}],
	initComponent : function(){
		this.callParent(arguments);
	}
});