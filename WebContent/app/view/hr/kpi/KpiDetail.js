Ext.define('erp.view.hr.kpi.KpiDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
					xtype: 'erpKpiPanel',
					anchor: '100% 50%',
					fixedlayout:true,
					saveUrl: 'hr/kpi/saveDetail.action?caller=' +caller,
					updateUrl:'hr/kpi/updateDetail.action?caller=' +caller,
					deleteUrl: 'hr/kpi/deleteDetail.action?caller=' +caller,
					getIdUrl: 'common/getCommonId.action?caller=' +caller,
					keyField: '',
					codeField: ''
			},
			{
				xtype: 'erpGridPanel2',
				selModel: {
				    injectCheckbox: 0,
				    mode: "MULTI",     //"SINGLE"/"SIMPLE"/"MULTI"
				    checkOnly: true     //只能通过checkbox选择
				},
				selType: "checkboxmodel",
				anchor: '100% 50%'
			}] 
		}); 
		me.callParent(arguments); 
	} 
});