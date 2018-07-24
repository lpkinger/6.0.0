Ext.define('erp.view.fa.arp.PayBalance',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: '/fa/PayBalanceController/savePayBalance.action',
				deleteUrl: '/fa/PayBalanceController/deletePayBalance.action',
				updateUrl: '/fa/PayBalanceController/updatePayBalance.action',
				auditUrl: '/fa/PayBalanceController/auditPayBalance.action',
				printUrl: '/fa/PayBalanceController/printPayBalance.action',
				postUrl: '/fa/PayBalanceController/postPayBalance.action',
				resPostUrl: '/fa/PayBalanceController/resPostPayBalance.action',
				resAuditUrl: '/fa/PayBalanceController/resAuditPayBalance.action',
				submitUrl: '/fa/PayBalanceController/submitPayBalance.action',
				resSubmitUrl: '/fa/PayBalanceController/resSubmitPayBalance.action',
				catchABUrl:'/fa/PayBalanceController/catchAB.action',
				cleanABUrl:'/fa/PayBalanceController/cleanAB.action',
				getIdUrl: 'common/getId.action?seq=PayBalance_SEQ',
				keyField: 'pb_id',
				codeField: 'pb_code',
				auditStatusCode:'pb_auditstatuscode',
				statusCode:'pb_statuscode',
				printStatusCode:'pb_printstatuscode',
				mStatusCode:'pb_vmstatuscode',
				assCaller:'PayBalanceAss',
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
				xtype: 'erpGridPanel2',
				anchor: '100% 50%',
				keyField: 'pbd_id',
				mainField: 'pbd_pbid',
				detailAssCaller:'PayBalanceDetailAss',
				/*_buttons: [{
					xtype: 'erpAssDetailButton',
					disabled: true
				}],*/
				tbar:[{
						xtype:'tbtext',
						text:'发票详情',
						cls: 'x-toolbar-title'
					},'->',{
						xtype:'multidbfindtrigger',
						name:'bi_code',
						id:'bi_code',
						fieldLabel:'开票记录号',
						hidden: true
					},'-',{
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