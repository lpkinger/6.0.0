Ext.define('erp.view.fa.ars.RecBalanceTK',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 50%',
				saveUrl: 'fa/ars/saveRecBalance.action',
				deleteUrl: 'fa/ars/deleteRecBalance.action',
				updateUrl: 'fa/ars/updateRecBalance.action',
				auditUrl: 'fa/ars/auditRecBalance.action',
				postUrl: 'fa/ars/postRecBalance.action',
				resPostUrl: 'fa/ars/resPostRecBalance.action',
				resAuditUrl: 'fa/ars/resAuditRecBalance.action',
				submitUrl: 'fa/ars/submitRecBalance.action',
				resSubmitUrl: 'fa/ars/resSubmitRecBalance.action',
				getIdUrl: 'common/getId.action?seq=RecBalance_SEQ',
				catchABUrl:'fa/RecBalanceController/catchAB.action',
				cleanABUrl:'fa/RecBalanceController/cleanAB.action',
				keyField: 'rb_id',
				codeField: 'rb_code',
				auditStatusCode:'rb_auditstatuscode',
				statusCode:'rb_statuscode',
				printStatusCode:'rb_printstatuscode',
				mStatusCode:'rb_strikestatuscode',
				assCaller:'RecBalanceAss',
				voucherConfig: {
					voucherField: 'rb_vouchercode',
					vs_code: 'RecBalance',
					yearmonth: 'rb_date',
					datas: 'rb_code',
					mode: 'single',
					status: 'rb_statuscode',
					kind: function(form){
						var f = form.down('#rb_kind');
						return f ? f.getValue() : null;
					},
					vomode: 'AR'
				}
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 50%',
				keyField: 'rbd_id',
				mainField: 'rbd_rbid',
				detailAssCaller:'RecBalanceDetailAss',
				tbar:['<span  class="x-panel-header-text-default-framed-new">发票详情</span>','->',{
					xtype:'datefield',
					fieldLabel:'日期区间',
					labelAlign:'right'
				},{
					xtype:'displayfield',
					value:'~'
				},{
					xtype:'datefield'
				},{
					text:'获取发票',
					iconCls: 'x-button-icon-check',
					cls: 'x-btn-gray',
					name:'catchab'
				},'-',{
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