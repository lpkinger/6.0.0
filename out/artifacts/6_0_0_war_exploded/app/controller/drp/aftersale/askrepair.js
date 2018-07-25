Ext.QuickTips.init();
Ext.define('erp.controller.drp.aftersale.askrepair', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','drp.aftersale.askrepair','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      			'core.button.ResSubmit','core.button.TurnStorage','core.button.TurnCheck','core.button.Confirm',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger','core.button.TurnRepairOrder'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				//保存之前的一些前台的逻辑判定
    				//console.log(me.GridUtil);
    				me.FormUtil.beforeSave(me);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addAskRepair', '新增客户报修单', 'jsps/drp/aftersale/askrepair.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
            'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('cr_id').value);
    			}
    		},
    		'erpConfirmButton': {
    			afterrender: function(btn){
    				var statu = Ext.getCmp('cr_statuscode');
    				var isConfirm=Ext.getCmp('cr_confirmstatus').value;
    				if(statu && statu.value != 'AUDITED'||isConfirm=='已处理'){
					btn.hide();
    				}
			},
    			click: function(btn){    				
    				me.onConfirm(Ext.getCmp('cr_id').value);
    				
    			}
    		} ,
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('cr_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('cr_id').value);
    			}
    		},
            'erpTurnRepairOrderButton': {
            	beforerender:function(btn){
            		btn.setText("转分检信息");
            	},
                afterrender: function(btn){
                	var isConfirm=Ext.getCmp('cr_confirmstatus').value;
                	var isTurn=Ext.getCmp('cr_isturn').value
    				if(isConfirm != '已处理'||isTurn=='已转分检'){
    					btn.hide();
    				}
    			},
                click: function(btn) {
                    Ext.MessageBox.confirm('提示', '确认要转分检信息吗?', del);
                    function del(btn){
                        if(btn == 'yes'){
                    		var	grid = Ext.getCmp('grid');
                    		var jsonGridData = new Array();
                    		var form = Ext.getCmp('form');
                    		grid.getStore().each(function(item){//将grid里面各行的数据获取并拼成jsonGridData
                    			var data = Ext.clone(item.data);
                    			if(data[grid.necessaryField] != null && data[grid.necessaryField] != ""){
                    				if(grid.mainField && form && form.keyField){//例如，将pu_id的值赋给pd_puid
                    					data[grid.mainField] = Ext.getCmp(form.keyField).value;
                    				}
                    				Ext.each(grid.columns, function(c){
                    					if(c.xtype == 'datecolumn'){
                    						if(Ext.isDate(data[c.dataIndex])){
                    							data[c.dataIndex] = Ext.Date.toString(data[c.dataIndex]);//在这里把GMT日期转化成Y-m-d格式日期
                    						} else {
                    							data[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d');//如果用户没输入日期，或输入有误，就给个默认日期，
                    							//或干脆return；并且提示一下用户
                    						}
                    					} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
                    						if(data[c.dataIndex] == null || data[c.dataIndex] == ''){
                    							data[c.dataIndex] = '0';//也可以从data里面去掉这些字段
                    						} else {
                    							data[c.dataIndex] = "" + data[c.dataIndex];
                    						}
                    					}
                    				});
                    				data['cr_id']=Ext.getCmp('cr_id').value;
                    				data['cr_code']=Ext.getCmp('cr_code').value;
                    				data['cr_cucode']=Ext.getCmp('cr_cucode').value;
                    				data['cr_otherenname']=Ext.getCmp('cr_otherenname').value;
                    				jsonGridData.push(Ext.JSON.encode(data));
                    			}
                    		});
                    		//return jsonGridData;
                        	Ext.Ajax.request({
                    	   		url : basePath + 'drp/vastTurnPartCheck.action',
                    	   		params: {data:jsonGridData},
                    	   		method : 'post',
                    	   		callback : function(options,success,response){
                    	   			var localJson = new Ext.decode(response.responseText);
                        			if(localJson.success){
                        				saveSuccess(function(){
                        					//add成功后刷新页面进入可编辑的页面 
                        					showMessage('提示',localJson.log);
                        					window.location.reload();
                        				});
                    	   			} else if(localJson.exceptionInfo){
                    	   				var str = localJson.exceptionInfo;
                    	   				showError(str);
                    	   				} 
                            		}
                    		});
                        }
                    }
                }
            }
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	onConfirm: function(id){
		var form = Ext.getCmp('form');	
		Ext.Ajax.request({
	   		url : basePath + 'drp/confirmCustomerRepair.action',
	   		params: {
	   			id: id,
	   			caller:caller
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			//me.setLoading(false);
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				//audit成功后刷新页面进入可编辑的页面 
	   				window.location.reload();
	   			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", '确认成功');
    	   					window.location.reload();
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
	   		}
		});
	}
});