Ext.define('erp.view.scm.sale.MachineNoScan',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		me.FormUtil = Ext.create('erp.util.FormUtil');
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 20%'
			},{
				xtype: 'grid',
				anchor: '100% 80%',
				columnLines : true,
				id : 'grid',
				columns : [ {
					text : '产品编号',
					cls : 'x-grid-header-1',
					dataIndex: 'PRCODE',
					flex: 1.5
				}, {
					text : '产品名称',
					cls : 'x-grid-header-1',
					dataIndex: 'PRNAME',
					flex: 1.2
				}, {
					text : '产品规格',
					cls : 'x-grid-header-1',
					dataIndex: 'PRSPEC',
					flex: 1
				}, {
					text : '数量',
					cls : 'x-grid-header-1',
					dataIndex: 'QTY',
					flex: 0.7
				}, {
					text : '已采集数',
					cls : 'x-grid-header-1',
					dataIndex: 'YQTY',
					flex: 1
				}, {
					text : '待采集数',
					cls : 'x-grid-header-1',
					dataIndex: 'DCJQTY',
					flex: 1
				}, {
					text : '操作',
					cls : 'x-grid-header-1',
					flex: 1,
					xtype: 'buttoncolumn',
					buttons: [{
						text: '采集',
						handler: function(view, cell, recordIndex, cellIndex, e) {
							var record = view.getStore().getAt(recordIndex);
							me.gather(record);
						}						
					}, {
						text: '清除采集结果',
						handler: function(view, cell, recordIndex, cellIndex, e) {
							var record = view.getStore().getAt(recordIndex);
							me.clear(record);
						}
					}]
				}],
				store : new Ext.data.Store({
					fields : [ 'PRCODE', 'PRNAME', 'PRSPEC',
							'QTY', 'YQTY', 'DCJQTY', 'PIID', 'INOUTNO'],
					proxy : {
						type : 'ajax',
						url : basePath + 'scm/sale/getProdioMachine.action',
						reader : {
							type : 'json',
							root : 'data'
						}
					}
				})
			}]
		}); 
		me.callParent(arguments); 
	},
	gather:function(record){
	   var me = this;
 	   var win=Ext.create('Ext.window.Window', {
 		   width: 400,
 		   height: 300,
 		   modal: true,
 		   closeAction: 'destroy',
 		   title: '<h1>机器号采集</h1>',
 		   layout: {
 			   type: 'vbox'
 		   },
 		   id: 'gather',
 		   items:[{
 			   margin: '5 0 0 5',
 			   xtype:'textfield',
 			   fieldLabel:'产品编号',
 			   value: record.data.PRCODE,
 			   id:'prcode',
 			   readOnly : true
 		   },{
 			   margin: '5 0 0 5',
 			   xtype:'textfield',
 			   fieldLabel:'数量',
 			   value: record.data.QTY,
 			   id:'qty',
 			   readOnly : true
 		   },{
 			   margin: '5 0 0 5',
 			   xtype:'textfield',
 			   fieldLabel:'已采集数',
 			   value: record.data.YQTY,
 			   id:'yqty',
 			   readOnly : true
 		   },{
 			  margin: '5 0 0 5',
			   xtype: 'textfield',
			   fieldLabel: '出入库ID',
			   value: record.data.PIID,
			   id:'piid',
			   readOnly: true,
			   hidden: true
 		   },{
 			  margin: '5 0 0 5',
			   xtype: 'textfield',
			   fieldLabel: '出入库单号',
			   value: record.data.INOUTNO,
			   id:'inoutno',
			   readOnly: true,
			   hidden: true
 		   },{
 			   margin: '5 0 0 5',
 			   xtype: 'textfield',
			   fieldLabel: '机器号',
			   readOnly: false,
			   emptyText: '请采集机器号',
			   value: record.data.PIM_MAC,
			   id:'machineno',
			   allowBlank: false,
			   plugins: [Ext.create("Ext.ux.form.field.ClearButton")]
 		   },{
 			   margin: '5 0 0 5',
 			   xtype: 'radiogroup',
 			   id: 'operator',
 			   width: 300,
 		       fieldLabel: '操作',
 		       columns: 2,
 		       vertical: false,
 		       items: [{ 
 		    	  boxLabel: '采集', name: 'operator', inputValue: 'get', checked: true 
 		       },{
 		    	  boxLabel: '取消', name: 'operator', inputValue: 'back'
 		       }]
			}],
 		   buttonAlign:'center',
 		   buttons:[{
				xtype: 'button',
				id : 'confirm',
				text: $I18N.common.button.erpConfirmButton,
				cls: 'x-btn-gray',
				style: {
		    		marginLeft: '10px'
		        },
				width: 60
			},{
				xtype: 'button',
				id : 'blankAll',
				text: $I18N.common.button.erpCloseButton,
				cls: 'x-btn-gray',
				width: 80,
				style: {
		    		marginLeft: '10px'
		        },
		        handler:function(btn){
	 				btn.up('window').close();
	 			}
			}]
 	   });
 	   win.show();
 	   win.down('#machineno').focus(false, 200);
    },
    clear:function(record){
    	var me = this;
    	var piid = Ext.getCmp('pi_id').value, iswcj = Ext.getCmp("iswcj").value;
		Ext.Ajax.request({
	   		url : basePath + 'scm/sale/clearProdioMac.action',
	   		params: {
	   			piid     : record.data.PIID,
	   			prcode	 : record.data.PRCODE
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			me.FormUtil.getActiveTab().setLoading(false);
	   			var r = new Ext.decode(response.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
    			if(r.success){
    				if(!Ext.isEmpty(piid)) {
    					Ext.getCmp('grid').getStore().load({
    						params: {
    							piid: piid,
    							iswcj:iswcj
    						}
    					});
    				}
    				showMessage('提示', '清除采集结果成功!', 1000);
	   			}
	   		}
		});  
    }
});