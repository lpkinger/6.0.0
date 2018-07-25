Ext.define('erp.view.fs.credit.CustCreditTargets',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var tbar = new Array();
		if(type==null){
			tbar = ['->',{
				xtype:'button',
				id:'exportBtn',
				iconCls : 'x-button-icon-excel',
				cls : 'x-btn-gray',
				text : $I18N.common.button.erpExportButton,
				style : {
					marginLeft : '10px'
				},
				width : 60
			},{
				xtype:'erpPrintButton',
				style:'margin-left:5px;margin-right:10px;'
			}];
		}else{
			tbar = ['->',{
				xtype:'button',
				id:'saveBtn',
			    text : '保存',
			    iconCls : 'x-button-icon-save',
				cls : 'x-btn-gray',
				width : 60,
				style:'margin-left:5px;'
			},{
				xtype:'button',
				id:'measureBtn',
			    text : '测算',
			    iconCls : 'x-button-icon-change',
				cls : 'x-btn-gray',
				width : 60,
				style:'margin-left:5px;margin-right:10px;'
			}];
		}
		Ext.apply(this, { 
			items: [{
					xtype: 'erpCustCreditTargetsFormPanel',
					anchor: '100% 20%',
					tbar:tbar
				},{				
					xtype: 'erpGridPanel2',
					layout:'fit',
					anchor : '100% 80%',
					keyField: 'cct_id',
					condition:gridCondition,
					bbar : null				
				}]
		}); 
		
		this.callParent(arguments); 
	}
});