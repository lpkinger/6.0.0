Ext.define('erp.view.fa.arp.PayPlease',{ 
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
					catchAPUrl:'fa/PayPleaseController/catchAP.action',
					cleanAPUrl:'fa/PayPleaseController/cleanAP.action',
					keyField: 'pp_id',
					codeField: 'pp_code'
				},{
					xtype: 'paypleasedetailGrid',
					anchor: '100% 18%',
					keyField: 'ppd_id',
					mainField: 'ppd_ppid',
					caller: 'PayPlease',
					id: 'grid',
					title:'供应商详情'
				},{
					xtype:'erpGridPanel5',
					anchor:'100% 42%',
					bbar: new erp.view.core.toolbar.Toolbar,
					relative: true,
					keyField:'ppdd_id',
					mainField:'ppdd_ppid',
					caller: 'PayPleaseDet',
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
						text:'发票详情',
						cls: 'x-toolbar-title'
					},'->',{
						xtype:'dbfindtrigger',
						name:'bi_code',
						id:'bi_code',
						fieldLabel:'开票记录号',
						hidden: true
					},{
						xtype:'dbfindtrigger',
						name:'ac_code',
						id:'ac_code',
						fieldLabel:'应付对账单号',
						hidden: true
					},{
						xtype:'datefield',
						name:'startdate',
						id:'startdate',
						labelWidth:40,
						fieldLabel:'从'
					},'-',{
						xtype:'datefield',
						name:'enddate',
						id:'enddate',
						labelWidth:40,
						fieldLabel:'到'
					},'-',{
						text:'获取发票',
						name:'catchab'
					},'-',{
						text:'清除发票',
						name:'cleanab'
					},'-',{
						text:'发票明细',
						name:'detail'
					}]
				}] 
		}); 
		me.callParent(arguments); 
	} 
});