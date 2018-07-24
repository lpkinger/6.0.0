Ext.define('erp.view.fa.ars.RecBalanceNotice',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'RecBalanceNoticeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'fa/ars/saveRecBalanceNotice.action',
					deleteUrl: 'fa/ars/deleteRecBalanceNotice.action',
					updateUrl: 'fa/ars/updateRecBalanceNotice.action',
					auditUrl: 'fa/ars/auditRecBalanceNotice.action',
					resAuditUrl: 'fa/ars/resAuditRecBalanceNotice.action',
					printUrl: 'common/printCommon.action',
					submitUrl: 'fa/ars/submitRecBalanceNotice.action',
					resSubmitUrl: 'fa/ars/resSubmitRecBalanceNotice.action',
					catchABUrl:'fa/ars/catchRecBalanceNoticeAB.action',
					cleanABUrl:'fa/ars/cleanRecBalanceNoticeAB.action',
					getIdUrl: 'common/getId.action?seq=RECBALANCENOTICE_SEQ',
					codeField: 'rb_code',
					keyField: 'rb_id',
					statusField: 'rb_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'rbd_detno',
					keyField: 'rbd_id',
					mainField: 'rbd_rbid',
					allowExtraButtons: true
				}]
			}] 
		}); 
		me.callParent(arguments); 
		if(caller == 'RecBalanceNotice!YS') {
			me.down('grid').addDocked({
				xtype: 'toolbar',
				dock: 'top',
				items: [{
						xtype:'tbtext',
						text:'发票详情',
						cls: 'x-toolbar-title'
					},'->',{
						xtype:'multidbfindtrigger',
						//xtype:'dbfindtrigger',
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
			});
		}
	} 
});