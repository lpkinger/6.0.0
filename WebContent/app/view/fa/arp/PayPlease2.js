Ext.define('erp.view.fa.arp.PayPlease2',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true,
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'fa/PayPleaseController/savePayPlease.action',
					deleteUrl: 'fa/PayPleaseController/deletePayPlease.action',
					updateUrl: 'fa/PayPleaseController/updatePayPlease.action',
					auditUrl: 'fa/PayPleaseController/auditPayPlease.action',
					printUrl: 'fa/PayPleaseController/printPayPlease.action',
					resAuditUrl: 'fa/PayPleaseController/resAuditPayPlease.action',
					submitUrl: 'fa/PayPleaseController/submitPayPlease.action',
					resSubmitUrl: 'fa/PayPleaseController/resSubmitPayPlease.action',
					getIdUrl: 'common/getId.action?seq=PayPlease_SEQ',
					keyField: 'pp_id',
					codeField: 'pp_code'
				},{
					xtype: 'paypleasedetailGrid',
					anchor: '100% 18%',
					keyField: 'ppd_id',
					mainField: 'ppd_ppid',
					id: 'grid',
					caller: 'PayPlease',
					title:'供应商详情'
				},{
					xtype:'erpGridPanel5',
					anchor:'100% 42%',
					bbar: new erp.view.core.toolbar.Toolbar,
					relative: true,
					keyField:'ppdd_id',
					mainField:'ppdd_ppid',
					caller: 'PayPleaseDet!YF',
					id: 'paypleasedetaildetGrid',
					_noc: 1,
					getCondition: function() {
						// gridCondition=ppd_ppidIS14790
						var cond = getUrlParam('gridCondition'), reg = /ppd_ppid(IS|=)(\d+)/;
						if(reg.test(cond)) {
							return 'ppdd_ppid=' + cond.replace(reg, '$2');
						}
						return null;
					},
					tbar:[{
						xtype:'tbtext',
						text:'采购单详情',
						cls: 'x-toolbar-title'
					}]
				}] 
		}); 
		me.callParent(arguments); 
	} 
});