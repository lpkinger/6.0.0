Ext.define('erp.view.fa.CheckAccount',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor',
	requires: ['erp.view.core.plugin.CopyPasteMenu'],
	hideBorders: true, 
	
	initComponent : function(){ 
		var me = this; 
		var tbar = [];
		if(module!='STF'){
			tbar = [{
				xtype: 'tbtext',
				text: '<b>当前期间：</b>',
				margin: '0'
			},{
				xtype: 'tbtext',
				id: 'yearmonth',
				text: '201305',
				margin: '0 0 0 2'
			},'->',{
				xtype: 'tbtext',
				id: 'checks',
				margin: '0 5 0 2',
				hidden:true
			},'-',{
				xtype: 'tbtext',
				id: 'oks',
				margin: '0 5 0 2',
				hidden:true
			},'-',{
				xtype: 'tbtext',
				id: 'errors',
				margin: '0 10 0 2',
				hidden:true
			}];
		}else{
			tbar = [{		
				xtype: 'monthdatefield',
		    	fieldLabel: '日期',
		    	allowBlank: false,
		    	labelWidth: 50,
		    	width: 150,
		    	id: 'yearmonth'
			},{
				xtype: 'tbtext',
				text:'当前冻结期间:',
				margin: '0 0 0 15'
			}, {
				xtype: 'displayfield',
				height: 23,
				id: 'date',
				name:'date',
				margin: '0 0 0 10'
			},'->',{
				xtype: 'tbtext',
				id: 'checks',
				margin: '0 5 0 2',
				hidden:true
			},'-',{
				xtype: 'tbtext',
				id: 'oks',
				margin: '0 5 0 2',
				hidden:true
			},'-',{
				xtype: 'tbtext',
				id: 'errors',
				margin: '0 10 0 2',
				hidden:true
			}];
		}
				
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
				id: 'account-check',
				anchor: '100% 100%',
				tbar: tbar,
				columns: [{
					text: '',
					dataIndex: 'check',
					flex: 1,
					renderer: function(val, meta, record) {
						meta.tdCls = val;
						return '';
					}
				},{
					text: '序号',
					dataIndex: 'detno_',
					flex: 0.7,
					hidden: true
				},{
					text: '检测项',
					dataIndex: 'title_',
					flex: 10,
					renderer: function(val, meta, record) {
						if(record.get('check') == 'error') {
							meta.style = 'color: gray';
						}
						return val;
					}
				},{
					text: '',
					dataIndex: 'link',
					flex: 1,
					renderer: function(val, meta, record) {
						if(record.get('check') == 'error') {
							meta.tdCls = 'detail';
							return '详细情况';
						}
						return '';
					}
				}],
				columnLines: true,
				store: Ext.create('Ext.data.Store',{
					fields: [{name: 'detno_', type : 'number'}, {name: 'code_', type: 'string'},{name: 'title_', type: 'string'}],
					data: []
				}),
				buttonAlign: 'center',
				dockedItems: [{
			        xtype: 'toolbar',
			        dock: 'top',
			        style:{background:'#fff'},
			        margin:'0 0 5 0',
			        items: [{
						cls: 'x-btn-gray',
						iconCls : 'x-button-icon-query',
						id: 'check',
						text: module!='STF'?'结账检查':'冻结检查',
						width: 90
					},{
						cls: 'x-btn-gray',
						iconCls : 'x-button-icon-check',
						id: 'accoutover',
						text: module!='STF'?'结  账':'冻结',
						width: 60,
						hidden: !isAccount,
						disabled : true,
						margin: '0 0 0 5'
					},{
						cls: 'x-btn-gray',
						iconCls : 'x-button-icon-recall',
						id: 'resaccoutover',
						text: module!='STF'?'反结账':'反冻结',
						width: 75,
						hidden: !isAccount,
						margin: '0 0 0 5'
					},{
						xtype: 'checkbox',
						boxLabel: module!='STF'?'知道错误了，我要继续结账':'知道错误了，我要继续冻结',
						id : 'allow',
						hidden : true,
						'float':true,
						margin: '0 5 0 5'
					},'->',{
						cls: 'x-btn-gray',
						iconCls : 'x-button-icon-close',
						id: 'close',
						text: $I18N.common.button.erpCloseButton,
						width: 60,
						margin: '0 0 0 5'
					}]
		 		}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});