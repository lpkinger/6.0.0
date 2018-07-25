Ext.define('erp.view.ma.DBfindSet',{ 
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
					anchor: '100% 50%',
					saveUrl: 'ma/saveDBfindSet.action',
					deleteUrl: 'ma/deleteDBfindSet.action',
					updateUrl: 'ma/updateDBfindSet.action',
					getIdUrl: 'common/getId.action?seq=DBFINDSET_SEQ',
					keyField: 'ds_id'
				},{
					xtype: 'erpGridPanel2', 
					anchor: '100% 50%', 
					detno: 'dd_ddno',
					necessaryField: 'dd_fieldname',
					keyField: 'dd_id',
					mainField: 'dd_dsid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});