Ext.define('erp.view.fa.arp.PayBalancePRDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 44%',
				saveUrl: 'fa/PayBalanceController/savePayBalancePRDetail.action',
				deleteUrl: 'fa/PayBalanceController/deletePayBalance.action',
				updateUrl: 'fa/PayBalanceController/updatePayBalancePRDetailById.action',
				auditUrl: 'fa/PayBalanceController/auditPayBalance.action',
				postUrl: 'fa/PayBalanceController/postPayBalance.action',
				resPostUrl: 'fa/PayBalanceController/resPostPayBalance.action',
				resAuditUrl: 'fa/PayBalanceController/resAuditPayBalance.action',
				submitUrl: 'fa/PayBalanceController/submitPayBalance.action',
				resSubmitUrl: 'fa/PayBalanceController/resSubmitPayBalance.action',
				catchPRUrl:'fa/PayBalanceController/catchPP.action',
				cleanPRUrl:'fa/PayBalanceController/cleanPP.action',
				catchABUrl:'/fa/PayBalanceController/catchAB.action',
				cleanABUrl:'/fa/PayBalanceController/cleanAB.action',
				getIdUrl: 'common/getId.action?seq=PayBalance_SEQ',
				keyField: 'pb_id',
				codeField: 'pb_code',
				auditStatusCode:'pb_auditstatuscode',
				statusCode:'pb_statuscode',
				printStatusCode:'pb_printstatuscode',
				mStatusCode:'pb_strikestatuscode',
				voucherConfig: {
					voucherField: 'pb_vouchcode',
					vs_code: 'PayBalance',
					yearmonth: 'pb_date',
					datas: 'pb_code',
					status: 'pb_statuscode',
					mode: 'single',
					kind: function(form){
						var f = form.down('#pb_kind');
						return f ? f.getValue() : null;
					},
					vomode: 'AP'
				}
			},{
				xtype: 'paybalanceprdetail',
				anchor: '100% 28%',
				caller:'PBPDGird',
				keyField: 'pbpd_id',
				mainField: 'pbpd_pbid',
				_noc:1,
				tbar:['<span  class="x-panel-header-text-default-framed">预付账款详情</span>','->',{
					xtype:'datefield',
					fieldLabel:'日期区间',
					labelAlign:'right'
				},{
					xtype:'displayfield',
					value:'~'
				},{
					xtype:'datefield'
				},'-',{
					text:'获取预付账款',
					name:'catchpr',
					iconCls: 'x-button-icon-check',
					cls: 'x-btn-gray'
				},'-',{
					text:'清除预付账款',
					name:'cleanpr',
					iconCls: 'x-button-icon-delete',
					cls: 'x-btn-gray'
				}]
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 28%',
				keyField: 'pbd_id',
				mainField: 'pbd_pbid',
				detailAssCaller:'PayBalanceDetailAss',
				_noc:1,
				/*bbar:{
					xtype: 'erpToolbar',
					//enableExport: false,
					enableDelete: false
				},*/
				tbar:['<span  class="x-panel-header-text-default-framed">应付账款详情</span>','->',{
					xtype:'dbfindtrigger',
					name:'bi_code',
					id:'bi_code',
					fieldLabel:'开票记录号',
					hidden: true
				},{
					xtype:'datefield',
					fieldLabel:'日期区间',
					labelAlign:'right'
				},{
					xtype:'displayfield',
					value:'~'
				},{
					xtype:'datefield'
				},'-',{
					text:'获取发票',
					name:'catchab',
					iconCls: 'x-button-icon-check',
					cls: 'x-btn-gray'
				},'-',{
					text:'清除发票',
					name:'cleanab',
					iconCls: 'x-button-icon-delete',
					cls: 'x-btn-gray'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});