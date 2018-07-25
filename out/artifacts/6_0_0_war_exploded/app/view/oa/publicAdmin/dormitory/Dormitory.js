Ext.define('erp.view.oa.publicAdmin.dormitory.Dormitory',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'DormitoryViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 65%',
					saveUrl: 'common/saveCommon.action?caller=' +caller,
					deleteUrl: 'common/deleteCommon.action?caller=' +caller,
					updateUrl: 'oa/publicAdmin/dormitory/Dormitory/updateDormitory.action',
					//updateUrl: 'common/updateCommon.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=DORMITORY_SEQ',
					keyField: 'do_id' 
/*					saveUrl: 'oa/publicAdmin/dormitory/Dormitory/saveDormitory.action',
					updateUrl: 'oa/publicAdmin/dormitory/Dormitory/updateDormitory.action',
					deleteUrl: 'oa/publicAdmin/dormitory/Dormitory/deleteDormitory.action',
					getIdUrl: 'common/getId.action?seq=DORMITORY_SEQ',
					keyField: 'do_id',*/
					//codeField: 'do_code',
					//statusField: 'do_statuscode'
				},
				{
					xtype: 'erpGridPanel2',
					anchor: '100% 35%', 
					detno: 'dd_detno',
					keyField: 'dd_id',
					mainField: 'dd_doid'
				}
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});