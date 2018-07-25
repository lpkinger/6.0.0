Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.PurchaseChange', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'core.button.PrintByCondition','core.form.Panel','scm.purchase.PurchaseChange','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print',
      		'core.button.ResAudit','core.button.Audit','core.button.Close','core.button.Delete','core.button.Update',
      		'core.button.DeleteDetail','core.button.ResSubmit','core.form.FileField', 'core.button.ResetSync',
			'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.FormUtil = Ext.create('erp.util.FormUtil');
    	this.GridUtil = Ext.create('erp.util.GridUtil');
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'erpFormPanel': {
    			afterload: function(form){
    				var main = getUrlParam('main');
					formCondition = getUrlParam('formCondition');
					if(main&&!formCondition){
						me.FormUtil.autoDbfind(caller, 'pc_purccode', main);
					}
    			}
    		},
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick,
    			reconfigure: function(grid, store, columns){
					var detail = getUrlParam('detail');
					gridCondition = getUrlParam('gridCondition');
					if(detail&&!gridCondition){
						var pu_code = Ext.getCmp("pc_purccode").value;
						if(!pu_code){
							showError("请先选择采购单!");
							return;
						}
    					detail += " and pu_code = '"+pu_code+"'";
						me.GridUtil.autoDbfind(grid, 'pcd_pddetno', detail);
						grid.store.each(function(record){
							me.updateDetail(record);
						});
					}
				}
    		},
    		'field[name=pc_purccode]': {
				afterrender:function(f){
					f.setFieldStyle({
						'color': 'blue'
	 				});
	 				f.focusCls = 'mail-attach';
	 				var c = Ext.Function.bind(me.openInvoice, me);
	 				Ext.EventManager.on(f.inputEl, {
	 					mousedown : c,
	 					scope: f,
	 					buffer : 100
	 				});
				}
	    	},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item) {
			            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
			            	if (Ext.isEmpty(item.data['pcd_newdelivery'])) {
			                    item.set('pcd_newdelivery', Ext.getCmp('pcd_olddelivery'));
			                }
			            }
			        });
			        if (bool){
			        	if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
	    					me.BaseUtil.getRandomNumber();//自动添加编号
	    				}
	    				this.FormUtil.beforeSave(this);
			        }
    			}
    		},
    		'field[name=pc_newcurrency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=pc_indate]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item) {
			            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
			            	if (Ext.isEmpty(item.data['pcd_newdelivery'])) {
			                    item.set('pcd_newdelivery', Ext.getCmp('pcd_olddelivery'));
			                }
			            }
			        });
			        if(bool)
    					this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPurchaseChange', '新增采购变更单', 'jsps/scm/purchase/purchaseChange.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				Ext.each(items, function(item) {
			            if (item.dirty && !Ext.isEmpty(item.data['pcd_prodcode'])) {
			                if (Ext.isEmpty(item.data['pcd_newdelivery'])) {
			                    item.set('pcd_newdelivery', Ext.getCmp('pcd_olddelivery'));
			                }
			            }
			        });
			        var id = Ext.getCmp('pc_id').value, check = Ext.getCmp('pc_needvendcheck');
			        /*if(check && (!check.getValue() || check.getValue() == '0')) {
			        	var c = confirm('需要将变更信息传给供应商确认？\n选择【确定】后，只有在供应商同意的情况下才会变更；\n否则请选择【取消】执行下一步操作');
			        	if(c) {
			        		me.needCheck(id, function(){
			        			me.FormUtil.onSubmit(id);
			        		});
			        	} else
			        		me.FormUtil.onSubmit(id);
			        } else*/
			        me.FormUtil.onSubmit(id, true);	
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				var bool = true;
    				Ext.each(items, function(item) {
			            if (item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != "") {
			            	if (Ext.isEmpty(item.data['pcd_newdelivery'])) {
			                    item.set('pcd_newdelivery', Ext.getCmp('pcd_olddelivery'));
			                }
			            }
			        });
			        if(bool)
    					me.FormUtil.onAudit(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pc_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click:function(btn){
					var reportName="PURCChange";
					var condition='{PurchaseChange.pc_id}='+Ext.getCmp('pc_id').value+'';
					var id=Ext.getCmp('pc_id').value;
					me.FormUtil.onwindowsPrint2(id,reportName,condition);
    			}
    		},
    		'erp2PurcButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				warnMsg("确定要转入采购单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/purchase/purcchangeToPurchase.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('pc_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/scm/purchase/purchase.jsp?formCondition=pu_id=" + id + "&gridCondition=pd_puid=" + id;
    	    		    					me.FormUtil.onAdd('Purchase' + id, '采购单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		'textfield[name=pc_purccode]': {
    			aftertrigger: function(field){
    				var form=field.ownerCt;
    				if(!Ext.isEmpty(field.getValue())){
    					if(form.down('#pc_newdelivery') && form.down('#pc_delivery'))
    					Ext.getCmp('pc_newdelivery').setValue(Ext.getCmp('pc_delivery').value);
        				Ext.getCmp('pc_newpaymentscode').setValue(Ext.getCmp('pc_paymentscode').value);
        				Ext.getCmp('pc_newpayments').setValue(Ext.getCmp('pc_payments').value);
        				Ext.getCmp('pc_newcurrency').setValue(Ext.getCmp('pc_currency').value);
        				Ext.getCmp('pc_newapvendcode').setValue(Ext.getCmp('pc_apvendcode').value);
        				Ext.getCmp('pc_newapvendname').setValue(Ext.getCmp('pc_apvendname').value);
        				Ext.getCmp('pc_newrate').setValue(Ext.getCmp('pc_rate').value);
        				if(Ext.getCmp('pc_pukind')&&Ext.getCmp('pc_newpukind')){
        					Ext.getCmp('pc_newpukind').setValue(Ext.getCmp('pc_pukind').value);
        				}
    				}
    			}
    		},
    		'dbfindtrigger[name=pcd_pddetno]': {
    			afterrender: function(t){
    				t.gridKey = "pc_purccode";
    				t.mappinggirdKey = "pu_code";
    				t.gridErrorMessage = "请先选择采购单!";
    			},
    			aftertrigger: function(t){
    				if(t.value != null && t.value != ''){
    					if(t.owner) {
    						var record = t.owner.selModel.lastSelected;
    						me.updateDetail(record);
    						/*record.set('pcd_newqty', record.data.pcd_oldqty);
        					record.set('pcd_newprodcode', record.data.pcd_prodcode);
        					record.set('pcd_newbeipin', record.data.pcd_oldbeipin);
        					record.set('pcd_newprice', record.data.pcd_oldprice);
            	    		record.set('pcd_newdelivery', record.data.pcd_olddelivery);
            	    		record.set('pcd_newtaxrate', record.data.pcd_taxrate);*/
    					}
    				}
    			}
    		},
    		'multidbfindtrigger[name=pcd_pddetno]': {
    			afterrender: function(t){
    				t.gridKey = "pc_purccode";
    				t.mappinggirdKey = "pu_code";
    				t.gridErrorMessage = "请先选择采购单!";
    			},
    			aftertrigger: function(t){
    				if(t.value != null && t.value != ''){
    					if(t.owner) {
    						var record = t.owner.selModel.lastSelected;
    						me.updateDetail(record);
    						/*record.set('pcd_newqty', record.data.pcd_oldqty);
        					record.set('pcd_newprodcode', record.data.pcd_prodcode);
        					record.set('pcd_newbeipin', record.data.pcd_oldbeipin);
        					record.set('pcd_newprice', record.data.pcd_oldprice);
            	    		record.set('pcd_newdelivery', record.data.pcd_olddelivery);
            	    		record.set('pcd_newtaxrate', record.data.pcd_taxrate);*/
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
	getOldStore: function(condition){
		var me = this;
		var grid = Ext.getCmp('grid');
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/singleGridPanel.action",
        	params: {
        		caller: "Purchase",
        		condition: condition
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.BaseUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = [];
        		if(!res.data || res.data.length == 2){
        			return;
        		} else {
        			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        		}
        	    Ext.Array.each(grid.store.data.items, function(item, index){
        	    	if(index < data.length){
        	    		item.set('pcd_oldqty', data[index].pd_qty);
        	    		item.set('pcd_prodid', data[index].pd_prodid);
        	    		item.set('pcd_prodcode', data[index].pd_prodcode);
        	    		item.set('pcd_oldbeipin', data[index].pd_beipin);
        	    		item.set('pcd_oldprice', data[index].pd_price);
        	    		item.set('pcd_olddelivery', data[index].pd_delivery);
        	    		item.set('pcd_taxrate', data[index].pd_rate);
        	    	}
        		});
        	}
        });
	},
	needCheck: function(id, callback) {
		Ext.Ajax.request({
			url: basePath + 'scm/purchase/change/needcheck.action',
			params: {
				changeId: id
			},
			callback: function(ops, s, r) {
				s && (callback.call(null));
			}
		});
	},
	openInvoice: function(e, el, obj) {
		var f = obj.scope, form = f.ownerCt,
			i = form.down('#pc_purcid');
		if(i && i.value) {
			url = 'jsps/scm/purchase/purchase.jsp?formCondition=pu_idIS' + i.value + '&gridCondition=pd_puidIS' + i.value;
			openUrl(url);
		}
	},
	updateDetail: function(record){
		record.set('pcd_newqty', record.data.pcd_oldqty);
		record.set('pcd_newprodcode', record.data.pcd_prodcode);
		record.set('pcd_newbeipin', record.data.pcd_oldbeipin);
		record.set('pcd_newprice', record.data.pcd_oldprice);
		record.set('pcd_newdelivery', record.data.pcd_olddelivery);
		record.set('pcd_newtaxrate', record.data.pcd_taxrate);
	}
});