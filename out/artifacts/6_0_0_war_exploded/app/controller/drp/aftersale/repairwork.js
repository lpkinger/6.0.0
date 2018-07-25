Ext.QuickTips.init();
Ext.define('erp.controller.drp.aftersale.repairwork', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','drp.aftersale.repairwork','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
      			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      			'core.button.ResSubmit','core.button.TurnStorage','core.button.TurnCheck','drp.aftersale.repairworkDet','core.button.TurnRepairOrder',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger','drp.aftersale.repairworkDet'
      	],
    init:function(){
    	var me = this;
    	me.allowinsert = true;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			itemdblclick: function(grid,record){
					var resultGrid = Ext.getCmp('repairworkDet');
					var gridParam = {caller: 'ProdIoutlin', condition: "pd_ordercode='"+Ext.getCmp('rw_code').value+"' and pd_orderdetno="+record.data.rwd_detno};
					resultGrid.loadNewStore(resultGrid,'common/singleGridPanel.action',gridParam,"");
				}
    		},
    		'repairworkDet':{
    			beforerender:function(grid){
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				//保存之前的一些前台的逻辑判定
    				this.beforeSave();
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('rw_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rw_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onUpdate(me);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addRepairwork', '新增采购收料单', 'jsps/drp/aftersale/repairwork.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			},
    			afterrender:function(btn){
    			}
    		},
            'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rw_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('rw_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rw_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('rw_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rw_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('rw_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('rw_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('rw_id').value);
    			}
    		},
    		'erpTurnRepairOrderButton': {
            	beforerender:function(btn){
            		btn.setText("转其它应收单");
            	},
                afterrender: function(btn){
                	var statu=Ext.getCmp('rw_statuscode').value;
                	var isTurn=Ext.getCmp('rw_isturnarbill').value;
    				if(statu != 'AUDITED'||isTurn=='已转其它应收'){
    					btn.hide();
    				}
    			},
                click: function(btn) {
                    Ext.MessageBox.confirm('提示', '确认要转其它应收单吗?', del);
                    function del(btn){
                        if(btn == 'yes'){
                        	Ext.Ajax.request({
                    	   		url : basePath + 'drp/TurnARBill.action',
                    	   		params: {id:Ext.getCmp('rw_id').value},
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

	beforeSave: function(){
		var mm = this.FormUtil;
		var form = Ext.getCmp('form');
		if(! mm.checkForm()){
			return;
		}
		if(form.keyField){
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			mm.getSeqId(form);
		}
		}
		mm.onSave([]);
	},
	beforeUpdate: function(){
		var mm = this.FormUtil;
		var form = Ext.getCmp('form');
		var s1 = mm.checkFormDirty(form);
		if(s1 == ''){
			showError($I18N.common.form.emptyData + '<br/>' + $I18N.common.grid.emptyDetail);
			return;
		}
		if(form && form.getForm().isValid()){
			//form里面数据
			var r = form.getValues(false, true);
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
			});
			if(!mm.contains(form.updateUrl, '?caller=', true)){
				form.updateUrl = form.updateUrl + "?caller=" + caller;
			}
			mm.update(r, []);
	}}
});