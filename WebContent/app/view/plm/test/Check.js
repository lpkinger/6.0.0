Ext.define('erp.view.plm.test.Check',{ 
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
					anchor: '100% 68%',
					saveUrl: 'plm/check/saveCheck.action',
					deleteUrl: 'plm/check/deleteCheck.action',
					updateUrl: 'plm/check/updateCheck.action',
					auditUrl: 'plm/check/confirm.action',
					resAuditUrl: 'plm/check/resauditCheck.action',
					submitUrl: 'plm/check/submitCheck.action',
					resSubmitUrl: 'plm/check/ressubmitCheck.action',
					getIdUrl: 'common/getId.action?seq=CHECKTABLE_SEQ',
					keyField: 'ch_id',
					codeField:'ch_code'
				},
				{
					 xtype:'tabpanel',
					 anchor:'100% 32%',
					 layout:'fit',
					items:[{
						title:'沟通记录',
						xtype: 'erpGridPanel2',	
						id:'grid',
						caller:'Check'
					},{
						title:'变更明细',
						xtype: 'erpGridPanel5',
						id:'change',
						caller:'CheckChange',
						mainField:'cc_cldid',						 
					}]
				}
				
				]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});