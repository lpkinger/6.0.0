Ext.define('erp.view.scm.sale.BarcodeScan',{ 
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
					flex: 0.5
				}, {
					text : '已采集数',
					cls : 'x-grid-header-1',
					dataIndex: 'YQTY',
					flex: 0.5
				}, {
					text : '待采集数',
					cls : 'x-grid-header-1',
					dataIndex: 'DCJQTY',
					flex: 0.5
				}, {
					text : '操作',
					cls : 'x-grid-header-1',
					flex: 1.7,
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
					}, {
						text: '料盘标签打印',
						name:'detailprint',
						handler: function(view, cell, recordIndex, cellIndex, e) {console.log(1);
							var record = view.getStore().getAt(recordIndex);
							var id=record.data.PIID;
							var prodcode=record.data.PRCODE;
							var reportName="TrayLabel";
    						var condition = "{prodinout.pi_id}=" + id+ " AND PD_PRODCODE='"+prodcode+"'";
					    	var form=Ext.getCmp('form');
					    	form.setLoading(true);//loading...
					    	Ext.Ajax.request({
								url : basePath + 'scm/sale/printBarcode.action',
								params: {
									id: id,
									reportName:reportName
								},
								method : 'post',
								timeout: 360000,
								callback : function(options,success,response){
									var res = new Ext.decode(response.responseText);
									if(res.exceptionInfo) {
										form.setLoading(false);
										showError(res.exceptionInfo);
										return;
									}
									if(res.info.printtype=='pdf'){
										window.location.href=res.info.printUrl+'/print?reportname='+res.info.reportname+'&condition='+condition+'&whichsystem='+res.info.whichsystem+"&"+'defaultCondition=select * from prodinout where pi_id='+id;
									}else{
										var url = res.info.printUrl + '?reportfile=' + res.info.reportname + '&&rcondition='+condition+'&&company=&&sysdate=373FAE331D06E956870163DCB2A96EC7&&key=3D7595A98BFF809D5EEEA9668B47F4A5&&whichsystem='+res.info.whichsystem+'';		
										window.open(url,'_blank');
									}
								}
							});
						}
					}]
				}],
				store : new Ext.data.Store({
					fields : [ 'PRCODE', 'PRNAME', 'PRSPEC',
							'QTY', 'YQTY', 'DCJQTY', 'PIID', 'INOUTNO'],
					proxy : {
						type : 'ajax',
						url : basePath + 'scm/sale/getProdioBarcode.action',
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
 		   title: '<h1>条码采集</h1>',
 		   layout: {
 			   type: 'vbox'
 		   },
 		   id: 'gather',
 		   defaults:{ width: 350},
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
			   fieldLabel: 'lotNo.',
			   readOnly: false,
			   emptyText: '请采集lotNo.',
			   value: record.data.PIM_MAC,
			   id:'lotNo',
			   allowBlank: false,
			   plugins: [Ext.create("Ext.ux.form.field.ClearButton")]
 		   },{
 			   margin: '5 0 0 5',
 			   xtype: 'textfield',
			   fieldLabel: 'Date code',
			   readOnly: false,
			   emptyText: '请采集Date code',
			   value: record.data.PIM_MAC,
			   id:'DateCode',
			   allowBlank: false,
			   plugins: [Ext.create("Ext.ux.form.field.ClearButton")]
 		   },{
 			   margin: '5 0 0 5',
 			   xtype: 'textfield',
			   fieldLabel: '备注',
			   readOnly: false,
			   //emptyText: '请采集Date code',
			   value: record.data.PIM_MAC,
			   id:'Remark',
			   //allowBlank: false,
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
 	   win.down('#lotNo').focus(false, 200);
    },
    clear:function(record){
    	var me = this;
    	var piid = Ext.getCmp('pi_id').value, iswcj = Ext.getCmp("iswcj").value;
		Ext.Ajax.request({
	   		url : basePath + 'scm/sale/clearProdioBarcode.action',
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