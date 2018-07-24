Ext.QuickTips.init();
Ext.define('erp.controller.b2b.sale.SaleDown', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','b2b.sale.SaleDown','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField','core.button.TurnSale',
      		'core.button.Save','core.button.Update','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
  				'core.button.Audit','core.button.Close','core.button.Delete','core.button.DeleteDetail','core.button.ResSubmit','core.button.TurnCustomer',
  				'core.button.ResAudit',	'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn'
      	],
    init:function(){
    	var me = this;
    	me.alloweditor = true;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			afterrender: function(grid){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}    				
    			},
    			summary:function(grid){
	    			var cols=Ext.Array.pluck(grid.columns, 'dataIndex');
	    			var column=Ext.Array.indexOf(cols,'sd_prodcode');
    				Ext.defer(function (){
    					 Ext.each(grid.store.data.items,function(record){
    						 if(!record.data.sd_prodcode){
    							 grid.plugins[0].startEditByPosition({
    								 row:record.index,
	          	    				 column:column
    							 });
        						 return false;
    						 }
    					 });
    				},200);
    			} 
    		}, 
    		'dbfindtrigger[name=sd_prodcode]':{
    			aftertrigger : function() {
    				var grid =Ext.getCmp('grid');
    				var cols=Ext.Array.pluck(grid.columns, 'dataIndex');
	    			var column=Ext.Array.indexOf(cols,'sd_prodcode');
    				Ext.each(grid.store.data.items,function(record){
						 if(!record.data.sd_prodcode){
						 	grid.plugins[0].startEditByPosition({
								row:record.index,
								column:column
							});
							grid.plugins[0].getActiveEditor().field.record=grid.getStore().getAt(record.index);
   						 	return false;
						 }
					});
				}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sa_statuscode');
    				if(status && status.value == 'TURNSA'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},   
    		'erpTurnCustomerButton': {   
    			beforerender:function(btn){
    				btn.setText("默认回复");
    			},
                 click: function(btn) {
                	var me = this.FormUtil;
     				var form = Ext.getCmp('form');
                	 Ext.Ajax.request({
     			   		url : basePath + form.replyUrl,
     			   		params: {
     			   			id: Ext.getCmp('sa_id').value,
     			   			caller:caller
     			   		},
     			   		method : 'post',
     			   		callback : function(options,success,response){
     			   			me.setLoading(false);
     			   			var localJson = new Ext.decode(response.responseText);
     		    			if(localJson.success){
     		    				//audit成功后刷新页面进入可编辑的页面 
     		    				showMessage('提示', '回复成功!', 1000);
     		    				window.location.reload();
     			   			} else {
     		    				if(localJson.exceptionInfo){
     		    	   				var str = localJson.exceptionInfo;
     		    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
     		    	   					str = str.replace('AFTERSUCCESS', '');
     		    	   					showMessage("提示", str, 1000);
     		    	   					auditSuccess(function(){
     		    	   						window.location.reload();
     		    	   					});
     		    	   				} else {
     		    	   					showError(str);return;
     		    	   				}
     		    	   			}
     		    			}
     			   		}
     				});
                 }
             },
    		'erpPrintButton': {
    			afterrender: function(btn){
    				var form = me.getForm(btn);
    				var id = Ext.getCmp(form.keyField).value;
    				btn.fireHandler = function(c) {
						var b = this, a = b.handler;
						b.fireEvent("click", b, c);
						if (a) {
							a.call(b.scope || b, b, c)
						}
						b.onBlur()
					};
					btn.handler=function(btn){
						Ext.Ajax.request({
							url : basePath + 'b2b/sale/printSaleDown.action',
							params: {
								id: id,
								caller: caller
							},
							method : 'post',
							callback : function(options,success,response){
								var localJson = new Ext.decode(response.responseText);
								if(localJson.success){
									var url = localJson.printurl;
									window.open(url,'_blank');
								} else {
									if(localJson.exceptionInfo){
										showError(localJson.exceptionInfo);return;
									}
								}
							}
						});
					};
    			}
    		},
    		'erpTurnSaleButton':{
    			click: function(btn){
    			  var said=Ext.getCmp('sa_id').value;
    				Ext.Ajax.request({
    	                url: basePath + 'b2b/sale/turnSale.action',
    	                params: {
    	                    id: said,
    	                    caller: caller
    	                },
    	                method: 'post',
    	                callback: function(opt, s, res) {
    	                    var r = new Ext.decode(res.responseText);
    	                    if (r.success) {    	                        
    	                        showMessage('提示', '转销售订单成功!', 1000);
  		    					var id = r.id;
    		    				var url = 'jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_idIS' + id + '&gridCondition=sd_saidIS' + id;
    		    				me.FormUtil.onAdd('Sale' + id, '销售订单' + id, url);
    		    			} else if (r.exceptionInfo) {
    	                        showError(r.exceptionInfo);
    	                    } else {
    	                        saveFailure();
    	                    }
    	                }
    	            });
    			}
    		},
    		 /**
 		    * 更改供应商回复信息
 		    */
           '#updatevendorbackInfo': {
               click: function(btn) {
	               var record = btn.ownerCt.ownerCt.getSelectionModel().getLastSelected();
	               me.UpdateVendorInfo(record);
	           }
     		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	 if (record.data.sd_id != 0 && record.data.sd_id != null && record.data.sd_id != '') {
             var btn = Ext.getCmp('updatevendorbackInfo');
             btn && btn.setDisabled(false);           
         }
    	 this.GridUtil.onGridItemClick(selModel, record);
    },
    UpdateVendorInfo: function(record) {
        win = this.getVendorInfoWindow(record);
        win.show();
    },
    getVendorInfoWindow: function(record) {
        var me = this;
        var date = record.data.sd_replydate;  
        var replyqty=record.data.sd_replyqty;
        var qty=record.data.sd_qty;      
        var num=qty-replyqty;
        return Ext.create('Ext.window.Window', {
            width: 430,
            height: 250,
            closeAction: 'destroy',
            cls: 'custom-blue',
            title: '<h1>回复信息</h1>',
            layout: {
                type: 'vbox'
            },
            items: [{
                width: '100%'
            },
            {
                margin: '5 0 0 5',
                xtype: 'datefield',
                fieldLabel: '回复交期',
                name: 'DELIVERYREPLY',
                format: 'Y-m-d',
                value:  date,
                id: 'DELIVERYREPLY'
            },
            {
                margin: '5 0 0 5',
                xtype: 'numberfield',
                fieldLabel: '回复数量(剩余)',
                name: 'QTYREPLY',
                hideTrigger: true,
                value: num,
                id: 'QTYREPLY'
            },
            {
                margin: '5 0 0 5',
                xtype: 'numberfield',
                fieldLabel: '已回复数',
                name: 'YREPLY',
                hideTrigger: true,
                editable:false,
                value: replyqty,
                id: 'YREPLY'
            },
            {
                margin: '5 0 0 5',
                xtype: 'textfield',
                columnidth: 0.4,
                fieldLabel: '回复明细',
                name: 'replydetail',
                value:record.data.sd_replydetail,
                id: 'replydetail'
            }
            ],
            buttonAlign: 'center',
            buttons: [{
                xtype: 'button',
                text: '保存',
                width: 60,
                iconCls: 'x-button-icon-save',
                handler: function(btn) {
                    var w = btn.up('window');
                    me.saveVendorInfo(w);
                    win.close();
                }
            },
            {
                xtype: 'button',
                columnWidth: 0.1,
                text: '关闭',
                width: 60,
                iconCls: 'x-button-icon-close',
                margin: '0 0 0 10',
                handler: function(btn) {
                    var win = btn.up('window');
                    win.close();
                }
            }]
        });
    },
    saveVendorInfo: function(w) {
        var deliveryreply = w.down('field[name=DELIVERYREPLY]').getValue();
        var qty = w.down('field[name=QTYREPLY]').getValue();
        grid = Ext.getCmp('grid'),
        record = grid.getSelectionModel().getLastSelected();
        if (!deliveryreply && !qty && !Ext.getCmp('replydetail').value) {
            showError('请先设置回复信息');
            return;
        } else {            
            var dd = {
                sd_id: record.data.sd_id,
                sd_replyqty: qty ? qty: 0,
                sd_replydate: deliveryreply ? Ext.Date.format(deliveryreply, 'Y-m-d') : null,                
                sd_replydetail:Ext.getCmp('replydetail').value
            };
            Ext.Ajax.request({
                url: basePath + 'b2b/sale/replyInfo.action',
                params: {
                    data: unescape(Ext.JSON.encode(dd)),
                    caller: caller
                },
                method: 'post',
                callback: function(opt, s, res) {
                    var r = new Ext.decode(res.responseText);
                    if (r.success) {
                        grid.GridUtil.loadNewStore(grid, {
                            caller: caller,
                            condition: gridCondition
                        });
                        showMessage('提示', '更新成功!', 1000);
                    } else if (r.exceptionInfo) {
                        showError(r.exceptionInfo);
                    } else {
                        saveFailure();
                    }
                }
            });
        }
    },
    
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});