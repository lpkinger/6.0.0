Ext.define('erp.view.scm.reserve.PackageRule',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'scm/reserve/saveRuleMaxNum.action?caller=' +caller,
				deleteUrl: 'scm/reserve/deleteRuleMaxNum.action?caller=' +caller,
				updateUrl: 'scm/reserve/updateRuleMaxNum.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=RuleMaxNum_SEQ',
				keyField: 'rmn_id',
				codeField: 'rmn_code',
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%',
				directImport:true//支持直接将Excel数据插入从表
			}]
		}); 
		me.callParent(arguments); 
	} 
});