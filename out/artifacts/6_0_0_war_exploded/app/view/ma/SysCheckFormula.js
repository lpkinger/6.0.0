Ext.define('erp.view.ma.SysCheckFormula',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel', 
					anchor: '100% 100%',
					saveUrl: 'ma/saveSysCheckFormula.action',
					deleteUrl: 'ma/deleteSysCheckFormula.action',
					updateUrl: 'ma/updateSysCheckFormula.action',
					getIdUrl: 'common/getId.action?seq=SYSCHECKFORMULA_SEQ',
					keyField: 'sf_id'
				}]
			}] 
		}); 
		me.callParent(arguments);
	} 
});