Ext.define('erp.view.fa.ars.RecBalanceAP',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 44%',
				saveUrl: 'fa/RecBalanceController/saveRecBalanceAP.action',
				deleteUrl: 'fa/ars/deleteRecBalance.action',
				updateUrl: 'fa/RecBalanceController/updateRecBalanceAPById.action',
				auditUrl: 'fa/ars/auditRecBalance.action',
				postUrl: 'fa/ars/postRecBalance.action',
				resPostUrl: 'fa/ars/resPostRecBalance.action',
				resAuditUrl: 'fa/ars/resAuditRecBalance.action',
				submitUrl: 'fa/ars/submitRecBalance.action',
				resSubmitUrl: 'fa/ars/resSubmitRecBalance.action',
				catchAPUrl:'fa/RecBalanceController/catchAP.action',
				cleanAPUrl:'fa/RecBalanceController/cleanAP.action',
				catchARUrl:'fa/RecBalanceController/catchAR.action',
				cleanARUrl:'fa/RecBalanceController/cleanAR.action',
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
				xtype: 'recbalanceap',
				anchor: '100% 28%',
				caller:'RBAPGird',
				keyField: 'rbap_id',
				mainField: 'rbap_rbid',
				detailAssCaller: 'RecBalanceApAss',
				tbar:['<span  class="x-panel-header-text-default-framed">应付发票详情</span>','->',{
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
					name:'catchap',
					iconCls: 'x-button-icon-check',
					cls: 'x-btn-gray'
				},'-',{
					xtype:'button',
					text:'清除发票',
					name:'cleanap',
					iconCls: 'x-button-icon-delete',
					cls: 'x-btn-gray'
				}]
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 28%',
				keyField: 'rbd_id',
				mainField: 'rbd_rbid',
				detailAssCaller:'RecBalanceDetailAss',
				tbar:['<span  class="x-panel-header-text-default-framed">应收发票详情</span>','->',{
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
					name:'catchar',
					iconCls: 'x-button-icon-check',
					cls: 'x-btn-gray'					
				},'-',{
					xtype:'button',
					text:'清除发票',
					name:'cleanar',
					iconCls: 'x-button-icon-delete',
					cls: 'x-btn-gray'
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});