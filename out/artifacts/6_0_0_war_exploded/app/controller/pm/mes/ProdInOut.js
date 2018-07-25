Ext.QuickTips.init();
Ext.define('erp.controller.pm.mes.ProdInOut', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.mes.ProdInOut','core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField','core.form.FileField',
      		'core.button.Add','core.button.Submit','core.button.ResAudit', 'core.button.Post', 'core.button.ResPost','pm.mes.DisplayPanel',
  			'core.button.Audit','core.button.Print','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger', 'core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.Flow','core.button.PrintByCondition'
  	],
	init:function(){
		var me = this;
		this.control({
				'#setclash':{
					click:function(){
					var pi_id=Ext.getCmp('pi_id').value;
					warnMsg('是否重新设置冲减单据和数量', function(btn){
						if(btn == 'yes'){
							Ext.Ajax.request({//拿到grid的columns
					        	url : basePath + 'pm/mes/setProdIOClash.action',
					        	params: {
					        		id:pi_id
					        	},
					        	async:  false ,
					        	method : 'post',
					        	callback : function(options,success,response){
					        		var res = new Ext.decode(response.responseText);
					        		if(res.success){
					        			window.location.reload();
					        		} 
					        	}
							});
						}
					});
				}
			},
			'erpFormPanel':{
				afterrender:function(){
					var formCondition = getUrlParam('formCondition');//从url解析参数
					if(formCondition!=null){
						me.getClashqty(formCondition.replace(/IS/g,"="));
					}  
				}
			},
			'erpGridPanel2': {
    			itemclick: function(selModel, record){
    				if(!Ext.getCmp('grid').readOnly){
    					this.onGridItemClick(selModel, record);
    				}
    			}
    		},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('pd_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('pi_invostatuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('add' + caller, '新增出入库单', "jsps/pm/mes/prodInOut.jsp?whoami=" + caller);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_invostatuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
					var status = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'UNPOST') {
                        btn.hide();
                    }
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('pd_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_invostatuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
					var status = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'UNPOST') {
                        btn.hide();
                    }
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('pd_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_invostatuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
					var status = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'UNPOST') {
                        btn.hide();
                    }
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('pd_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('pi_invostatuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
					var status = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'UNPOST') {
                        btn.hide();
                    }
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('pd_id').value);
				}
			},
			'erpPostButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'UNPOST') {
                        btn.hide();
                    }
                },
                click: {
                	lock: 2000,
                	fn: function(btn) {
                		me.FormUtil.onPost(Ext.getCmp('pd_id').value);
	                }
                }
            },
            'erpResPostButton': {
                afterrender: function(btn) {
                    var status = Ext.getCmp('pi_statuscode');
                    if (status && status.value != 'POSTED') {
                        btn.hide();
                    }
                },
                click: {
                	lock: 2000,
                	fn: function(btn) {
                		me.FormUtil.onResPost(Ext.getCmp('pd_id').value);
                     }
                }
            },
			'erpPrintButton':{
    			click:function(btn){
//    				var reportName="ProdInOut";
//    				var condition="";
//    			    condition='{ProdInOut.pi_id}='+Ext.getCmp('pi_id').value;
//    				var id=Ext.getCmp('pi_id').value;
//    				me.FormUtil.onwindowsPrint2(id,reportName,condition);
    			}
    		},
			'field[name=mc_clashcode]': {
    			focus: function(t){
    				t.setHideTrigger(false);
    				t.setReadOnly(false);//用disable()可以，但enable()无效
    				var code = Ext.getCmp('pd_ordercode').value;
    				if(code == null || code == ''){
    					showError("请先选择制造单号!");
    					t.setHideTrigger(true);
    					t.setReadOnly(true);
    				} else {
    					t.dbBaseCondition = "si_makecode='" + code + "'";
    				}
    			}
    		},
    		'field[name=pd_jobcode]': {
    			beforetrigger: function(field) {
    				var code = Ext.getCmp('pd_ordercode').value, prcode = Ext.getCmp('pd_prodcode').value;
    				if(code == null || code == ''){
    					showError("请先选择制造单号!"); return;
    				} else {
    					if(prcode == null || prcode == ''){
    						showError("请先选择物料编号!"); return;
    					} else {
    						field.findConfig = "mc_makecode='" + code + "' and mc_prodcode='" + prcode + "'";
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
	getClashqty:function(con){
		var me=this;   
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + 'pm/mes/getClashInfo.action',
        	params: {
        		con:con
        	},
        	async:  false ,
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.success){
        			Ext.getCmp('mconmake').setValue(res.info.mconmake);
        			Ext.getCmp('mcremain').setValue(res.info.mcremain);
        			Ext.getCmp('clashqty').setValue(res.info.clashqty);
        			if(res.info.setclash==1){
        				Ext.getCmp('setclash').hidden=false;
        			}
        			if(res.info.saveclash==1){
        				Ext.getCmp('saveclash').hidden=false; 
            			Ext.getCmp('clashqty').setMaxValue(res.info.mcremain);
        			}
        		} 
        	}
		});
	}
});