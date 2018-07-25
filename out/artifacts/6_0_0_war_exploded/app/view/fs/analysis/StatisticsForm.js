Ext.define('erp.view.fs.analysis.StatisticsForm',{ 
	extend: 'erp.view.core.form.Panel',
	alias: 'widget.erpStatisticsForm',
	id: 'statisticsform', 
    frame : true,
    enableTools: false,
    header: false,//不显示title
	layout : 'column',
	defaultType : 'textfield',
	autoScroll: true,
	labelSeparator : ':',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       labelWidth: 105,
	       blankText : $I18N.common.form.blankText
	},
	tbar: [{
		name: 'query',
		id: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray'
	}, '->',{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	style:'margin-left:5px;margin-right:10px;',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	getItemsAndButtons : function(){
		var items = [{
			xtype: 'textfield',
			fieldLabel: '客户编号',		
			columnWidth: 0.25,
			id: 'custcode',
			name: 'custcode',
			hidden: true
		},{
			xtype: 'dbfindtrigger',
			fieldLabel: '客户名称',		
			columnWidth: 0.25,
			dbCaller: 'CustomerInfor',
			id: 'custname',
			name: 'custname'
		},{
			xtype: 'condatefield',
			fieldLabel: '日期',		
			columnWidth: 0.5,
			value: 4,
			id: 'date',
			name: 'date'
		}];
		items = Ext.Array.merge(items,this.extraItems);
		Ext.apply(this,{
			items:items
		});
	}
});