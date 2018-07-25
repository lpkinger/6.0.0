Ext.define('erp.view.b2b.qc.MRBDOWN',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',				
				codeField: 'mr_code',
				keyField: 'mr_id',
				statusField: 'mr_status',
				statuscodeField: 'mr_statuscode'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 28%', 
				detno: 'md_detno',
				keyField: 'md_id',
				mainField: 'md_mrid',
				readOnly:true,
				bbar: {xtype: 'erpToolbar',id:'toolbar', enableAdd: false,enableDelete: false}
			},{
				xtype: 'mrbdowndetail',
				anchor: '100% 22%',
				caller:'MRBDOWNDetail',
				detno: 'mrd_detno',
				keyField: 'mrd_id',
				mainField: 'mrd_mrid',
				readOnly:true,
				bbar: {xtype: 'erpToolbar',id:'MRBDOWNDetailtool', enableAdd: false,enableDelete: false}
			}]
		}); 
		me.callParent(arguments); 
	} 
});