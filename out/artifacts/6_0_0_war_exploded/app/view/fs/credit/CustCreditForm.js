Ext.define('erp.view.fs.credit.CustCreditForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpCustCreditFormPanel',
	id: 'custCreditForm', 
    region: 'north',
    frame : true,
    header: false,//不显示title
	layout : 'column',
	autoScroll : true,
	//BaseUtil : Ext.create('erp.util.BaseUtil'),
	defaultType : 'textfield',
	labelSeparator : ':',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	tbar: [{
		name: 'query',
		id: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray'
	}, '->',{
		id: 'export',
    	name: 'export',
		text: $I18N.common.button.erpExportButton,
		iconCls: 'x-button-icon-excel',
    	cls: 'x-btn-gray'
    },{
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	style:'margin-left:5px;margin-right:10px;',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	initComponent : function(){
		Ext.apply(this,{
			items:[{
				xtype:'dbfindtrigger',
				fieldLabel:'客户名称',		
				columnWidth:0.5,
				id:'cra_cuvename',
				name:'cra_cuvename'
			}]
		});
		this.callParent(arguments);
	}
});