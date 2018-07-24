Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.SalePrice', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.sale.SalePrice','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField','core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger', 'core.button.Scan','core.button.Abate','core.button.ResAbate','core.button.Sync'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: function(view,record){
    				me.itemclick(view,record);
    			}
			},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
					if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
						me.BaseUtil.getRandomNumber();//自动添加编号
					}
					var grid = Ext.getCmp('grid');
					var items = grid.store.data.items;
					var bool = true;
					if(Ext.Date.format(Ext.getCmp('sp_todate').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('sp_fromdate').value,'Y-m-d')){
						bool = false;
						showError('截止日期小于开始日期');return;
					}
					if(Ext.Date.format(Ext.getCmp('sp_todate').value,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
						bool = false;
						showError('截止日期小于当前日期');return;
					}
					Ext.each(items, function(item){
						if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
							if(item.data['spd_currency'] == null || item.data['spd_currency'] == ''){
								bool = false;
								showError('明细表第' + item.data['spd_detno'] + '行的币别为空');return;
							}
							if(item.data['spd_price'] == null || item.data['spd_price'] == '' || item.data['spd_price'] == '0'
									|| item.data['spd_price'] == 0){
								bool = false;
								showError('明细表第' + item.data['spd_detno'] + '行的价格为空或0');return;
							}
						}
					});
					if(bool){
						this.FormUtil.beforeSave(this);
					}
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('sp_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					var grid = Ext.getCmp('grid');
					var items = grid.store.data.items;
					var bool = true;
					if(Ext.Date.format(Ext.getCmp('sp_todate').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('sp_fromdate').value,'Y-m-d')){
						bool = false;
						showError('截止日期小于开始日期');return;
					}
					if(Ext.Date.format(Ext.getCmp('sp_todate').value,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
						bool = false;
						showError('截止日期小于当前日期');return;
					}
					Ext.each(items, function(item){
						if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
							if(item.data['spd_currency'] == null || item.data['spd_currency'] == ''){
								bool = false;
								showError('明细表第' + item.data['spd_detno'] + '行的币别为空');return;
							}
							if(item.data['spd_price'] == null || item.data['spd_price'] == '' || item.data['spd_price'] == '0'
									|| item.data['spd_price'] == 0){
								bool = false;
								showError('明细表第' + item.data['spd_detno'] + '行的价格为空或0');return;
							}
						}
					});
					if(bool){
						this.FormUtil.onUpdate(this);
					}
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addSalePrice', '新增销售单价', 'jsps/scm/sale/salePrice.jsp?whoami=SalePrice');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sp_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					var grid = Ext.getCmp('grid');
					var items = grid.store.data.items;
					var bool = true;
					if(Ext.Date.format(Ext.getCmp('sp_todate').value,'Y-m-d') < Ext.Date.format(Ext.getCmp('sp_fromdate').value,'Y-m-d')){
						bool = false;
						showError('截止日期小于开始日期');return;
					}
					if(Ext.Date.format(Ext.getCmp('sp_todate').value,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
						bool = false;
						showError('截止日期小于当前日期');return;
					}
					Ext.each(items, function(item){
						if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
							if(item.data['spd_currency'] == null || item.data['spd_currency'] == ''){
								bool = false;
								showError('明细表第' + item.data['spd_detno'] + '行的币别为空');return;
							}
							if(item.data['spd_price'] == null || item.data['spd_price'] == '' || item.data['spd_price'] == '0'
									|| item.data['spd_price'] == 0){
								bool = false;
								showError('明细表第' + item.data['spd_detno'] + '行的价格为空或0');return;
							}
						}
					});
					if(bool){
						me.FormUtil.onSubmit(Ext.getCmp('sp_id').value);
					}
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('sp_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('sp_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('sp_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					/*var status = Ext.getCmp('sp_statuscode');
					if(status && status.value != 'AUDITED'){
						
					}				
*/	
					btn.hide();
				}
				/*click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('sp_id').value);
				}*/
			},
			'erpResAbateButton':{
    			afterrender: function(btn){
    				Ext.getCmp('erpResAbateButton').setDisabled(true);
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected;
    				if(!record) {
    					return;
    				}
    				var ppdid = record.get('spd_id');
    				if(ppdid == null || ppdid == 0) {
    					return;
    				}
	    			Ext.Ajax.request({
	        			url : basePath + "scm/sale/resabatesaleprice.action",
	        			params:{
	        				id: ppdid
	        			},
	        			method:'post',
	        			callback:function(options,success,response){
	        				var localJson = new Ext.decode(response.responseText);
	            			if(localJson.success){
	            				Ext.Msg.alert("提示","操作成功！");
	            				window.location.reload();
	            			} else {
	            				if(localJson.exceptionInfo){
	            	   				var str = localJson.exceptionInfo;
	            	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	            	   					str = str.replace('AFTERSUCCESS', '');
	            	   					showError(str);
	            	   				} else {
	            	   					showError(str);return;
	            	   				}
	            	   			}
	            			}
	        			}
	        		});
	    		}
    		},
    		'erpAbateButton':{
    			afterrender: function(btn){
    				Ext.getCmp('erpAbateButton').setDisabled(true);
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected;
    				if(!record) {
    					return;
    				}
    				var ppdid = record.get('spd_id');
    				if(ppdid == null || ppdid == 0) {
    					return;
    				}
	    			Ext.Ajax.request({
	        			url : basePath + "scm/sale/abatesaleprice.action",
	        			params:{
	        				id: ppdid
	        			},
	        			method:'post',
	        			callback:function(options,success,response){
	        				var localJson = new Ext.decode(response.responseText);
	            			if(localJson.success){
	            				Ext.Msg.alert("提示","操作成功！");
	            				window.location.reload();
	            			} else {
	            				if(localJson.exceptionInfo){
	            	   				var str = localJson.exceptionInfo;
	            	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	            	   					str = str.replace('AFTERSUCCESS', '');
	            	   					showError(str);	            	   	
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
				click: function(btn){
					me.FormUtil.onPrint(Ext.getCmp('sp_id').value);
				}
			},
			'erpSyncButton': {
            	afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt, s = form.down('#sp_statuscode');
                    if (s.getValue() != 'AUDITED')
                        btn.hide();
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
	itemclick:function(view, record){
		this.GridUtil.onGridItemClick(view, record);
		var ppdid = record.get('spd_id'),
			statuscode = record.get('spd_statuscode');
		if (ppdid != null && ppdid != 0) {
			if (statuscode=='UNVALID') {
				Ext.getCmp('erpResAbateButton').setDisabled(false);
				Ext.getCmp('erpAbateButton').setDisabled(true);
			}else if(statuscode=='VALID'){
				Ext.getCmp('erpAbateButton').setDisabled(false);
				Ext.getCmp('erpResAbateButton').setDisabled(true);
			}
		}	
//		if(ppdid != null && ppdid != 0) {
//			Ext.getCmp('erpAbateButton').setDisabled(false);
//			Ext.getCmp('erpResAbateButton').setDisabled(false);
//		}
    }
});