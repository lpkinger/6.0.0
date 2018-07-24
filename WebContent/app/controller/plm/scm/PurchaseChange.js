Ext.QuickTips.init();
Ext.define('erp.controller.plm.scm.PurchaseChange', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
   /* FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),*/
    views:[
      		'core.form.Panel','plm.scm.PurchaseChange','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print',
      		'core.button.ResAudit','core.button.Audit','core.button.Close','core.button.Delete','core.button.Update',
      		'core.button.DeleteDetail','core.button.ResSubmit',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.FormUtil = Ext.create('erp.util.FormUtil');
    	this.GridUtil = Ext.create('erp.util.GridUtil');
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
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
    				this.FormUtil.beforeSave(this);
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
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addPurchaseChange', '新增研发采购变更单', 'jsps/plm/scm/purchaseChange.jsp');
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
    				me.FormUtil.onSubmit(Ext.getCmp('pc_id').value);
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
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('pc_id').value);
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
    	    			   			console.log(localJson);
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
    			change: function(field){
//    				if(field != null && field != ''){
//    					me.getOldStore("pd_code='" + field.value + "'");
//    				}
    				if(field != null && field != ''){
    					Ext.getCmp('pc_delivery').setFieldStyle('background:#9BCD9B');
        				Ext.getCmp('pc_newdelivery').setValue(Ext.getCmp('pc_newdelivery').value || Ext.getCmp('pc_delivery').value);
        				Ext.getCmp('pc_paymentsid').setFieldStyle('background:#9BCD9B');
        				Ext.getCmp('pc_newpaymentsid').setValue(Ext.getCmp('pc_newpaymentsid').value || Ext.getCmp('pc_paymentsid').value);
        				Ext.getCmp('pc_vendid').setFieldStyle('background:#9BCD9B');
        				Ext.getCmp('pc_newvendid').setValue(Ext.getCmp('pc_newvendid').value || Ext.getCmp('pc_vendid').value);
        				Ext.getCmp('pc_vendcode').setFieldStyle('background:#9BCD9B');
        				Ext.getCmp('pc_newvendcode').setValue(Ext.getCmp('pc_newvendcode').value || Ext.getCmp('pc_vendcode').value);
        				Ext.getCmp('pc_currency').setFieldStyle('background:#9BCD9B');
        				Ext.getCmp('pc_newcurrency').setValue(Ext.getCmp('pc_newcurrency').value || Ext.getCmp('pc_currency').value);
        				Ext.getCmp('pc_rate').setFieldStyle('background:#9BCD9B');
        				Ext.getCmp('pc_newrate').setValue(Ext.getCmp('pc_newrate').value || Ext.getCmp('pc_rate').value);
    				}
    			},
    			blur: function(f){
    				
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
    						record.set('pcd_newqty', record.data.pcd_oldqty);
        					record.set('pcd_newprodid', record.data.pcd_prodid);
        					record.set('pcd_newprodcode', record.data.pcd_prodcode);
        					record.set('pcd_newbeipin', record.data.pcd_oldbeipin);
        					record.set('pcd_newprice', record.data.pcd_oldprice);
            	    		record.set('pcd_newdelivery', record.data.pcd_olddelivery);
            	    		record.set('pcd_newtaxrate', record.data.pcd_taxrate);
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
	}
});