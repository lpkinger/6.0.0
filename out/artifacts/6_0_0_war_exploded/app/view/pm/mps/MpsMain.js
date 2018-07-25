Ext.define('erp.view.pm.mps.MpsMain',{ 
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
					anchor: '100% 30%',
					saveUrl: 'pm/mps/saveMPS.action',
					deleteUrl: 'pm/mps/deleteMPS.action',
					updateUrl: 'pm/mps/updateMPS.action',
					submitUrl:'pm/mps/submitMPS.action',
					resSubmitUrl:'pm/mps/resSubmitMPS.action',
					auditUrl:'pm/mps/aduitMPS.action',
					resAuditUrl:'pm/mps/resAuditMPS.action',
					deleteAllDetailsUrl:'pm/mps/deleteAllDetails.action',
					getIdUrl: 'common/getId.action?seq=MPSMAIN_SEQ',
					keyField: 'mm_id',
					codeField:'mm_code'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%',
					detno: 'md_detno',
					keyField: 'md_id',
					mainField: 'md_mainid',
					headerCt: Ext.create("Ext.grid.header.Container",{
						forceFit: false,
						sortable: true,
						enableColumnMove:true,
						enableColumnResize:true,
						enableColumnHide: true
					}),
					invalidateScrollerOnRefresh: false,
					viewConfig: {
						trackOver: false
					},
					buffered: true,
					sync: true
				}] 
			}]
		}); 
		me.callParent(arguments); 
	} 
});