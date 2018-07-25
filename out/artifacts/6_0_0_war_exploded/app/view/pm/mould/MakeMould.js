Ext.define('erp.view.pm.mould.MakeMould',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'MakeMouldViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 48%',
					saveUrl : 'pm/mould/saveMakeMould.action?caller=' + caller,
					deleteUrl : 'pm/mould/deleteMakeMould.action?caller=' + caller,
					updateUrl : 'pm/mould/updateMakeMould.action?caller=' + caller,
					submitUrl : 'pm/mould/submitMakeMould.action?caller=' + caller,
					auditUrl : 'pm/mould/auditMakeMould.action?caller=' + caller,
					resAuditUrl : 'pm/mould/resAuditMakeMould.action?caller=' + caller,
					resSubmitUrl : 'pm/mould/resSubmitMakeMould.action?caller=' + caller,
					checkUrl : 'pm/mould/checkMakeMould.action?caller=' + caller,
					printUrl : 'pm/mould/printMakeMould.action?caller=' + caller,
					resCheckUrl : 'pm/mould/resCheckMakeMould.action?caller=' + caller,
					endUrl : 'pm/mould/endMakeMould.action?caller=' + caller,
					resEndUrl : 'pm/mould/resEndMakeMould.action?caller=' + caller,
					getIdUrl: 'common/getId.action?seq=Make_SEQ',
					keyField: 'ma_id',
					statusField: 'ma_status',
					statuscodeField: 'ma_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 52%', 
					detno: 'mm_detno',
					keyField: 'mm_id',
					mainField: 'mm_maid',
					bbar: {xtype: 'erpToolbar',id:'toolbar',enableUp: false, enableDown: false},
					allowExtraButtons : true
				/*	viewConfig: {
						getRowClass: function(record) {
							if(record.data.bd_usestatus=="DISABLE"){ 
								return 'disable';
							}
						} 
					}*/
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});