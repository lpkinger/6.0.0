Ext.define('erp.view.hr.emplmana.Phyexam',{ 
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
					saveUrl: 'hr/emplmana/savePhyexam.action',
					deleteUrl: 'hr/emplmana/deletePhyexam.action',
					updateUrl: 'hr/emplmana/updatePhyexam.action',		
					getIdUrl: 'common/getId.action?seq=Phyexam_SEQ',
					keyField: 'ph_id'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					necessaryField: 'pd_name',
					keyField: 'pd_id',
					detno: 'pd_detno',
					mainField: 'pd_phid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});