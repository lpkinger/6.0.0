Ext.define('erp.view.fa.ars.RecBalancePRDetail',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 44%',
				saveUrl: 'fa/RecBalanceController/saveRecBalancePRDetail.action',
				deleteUrl: 'fa/ars/deleteRecBalance.action',
				updateUrl: 'fa/RecBalanceController/updateRecBalancePRDetailById.action',
				auditUrl: 'fa/ars/auditRecBalance.action',
				postUrl: 'fa/ars/postRecBalance.action',
				resPostUrl: 'fa/ars/resPostRecBalance.action',
				resAuditUrl: 'fa/ars/resAuditRecBalance.action',
				submitUrl: 'fa/ars/submitRecBalance.action',
				resSubmitUrl: 'fa/ars/resSubmitRecBalance.action',
				catchPRUrl:'fa/RecBalanceController/catchPR.action',
				cleanPRUrl:'fa/RecBalanceController/cleanPR.action',
				catchABUrl:'fa/RecBalanceController/catchAB.action',
				cleanABUrl:'fa/RecBalanceController/cleanAB.action',
				getIdUrl: 'common/getId.action?seq=RecBalance_SEQ',
				keyField: 'rb_id',
				codeField: 'rb_code',
				auditStatusCode:'rb_auditstatuscode',
				statusCode:'rb_statuscode',
				printStatusCode:'rb_printstatuscode',
				mStatusCode:'rb_strikestatuscode',
				voucherConfig: {
					voucherField: 'rb_vouchercode',
					vs_code: 'RecBalance',
					yearmonth: 'rb_date',
					datas: 'rb_code',
					status: 'rb_statuscode',
					mode: 'single',
					kind: function(form){
						var f = form.down('#rb_kind');
						return f ? f.getValue() : null;
					},
					vomode: 'AR'
				}
			},{
				xtype: 'recbalanceprdetail',
				anchor: '100% 28%',
				caller:'RBPDGird',
				keyField: 'rbpd_id',
				mainField: 'rbpd_rbid',
				tbar:['<span  class="x-panel-header-text-default-framed">预收账款详情</span>','->',{
					xtype:'datefield',
					fieldLabel:'日期区间',
					labelAlign:'right'
				},{
					xtype:'displayfield',
					value:'~'
				},{
					xtype:'datefield'
				},{
					xtype:'button',
					text:'获取预收账款',
					name:'catchpr',
					iconCls: 'x-button-icon-check',
					cls: 'x-btn-gray'
				},'-',{
					xtype:'button',
					text:'清除预收账款',
					iconCls: 'x-button-icon-delete',
					cls: 'x-btn-gray',
					name:'cleanpr'
				}]
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 28%',
				keyField: 'rbd_id',
				mainField: 'rbd_rbid',
				detailAssCaller:'RecBalanceDetailAss',
				tbar:['<span  class="x-panel-header-text-default-framed">应收账款详情</span>','->',{
					xtype:'multidbfindtrigger',
					//xtype:'dbfindtrigger',
					name:'bi_code',
					id:'bi_code',
					fieldLabel:'开票记录号',
					hidden: true
				},{
					xtype:'multidbfindtrigger',
					name:'ac_code',
					id:'ac_code',
					fieldLabel:'对账单号',
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
				},{
					xtype:'button',
					text:'获取发票',
					iconCls: 'x-button-icon-check',
					cls: 'x-btn-gray',
					name:'catchab'
				},'-',{
					xtype:'button',
					text:'清除发票',
					iconCls: 'x-button-icon-delete',
					cls: 'x-btn-gray',
					name:'cleanab'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});