Ext.QuickTips.init();
Ext.define('erp.controller.b2b.sale.SaleDownChange', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','b2b.sale.SaleDownChange','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.form.MultiField','core.button.TurnSale',
      		'core.button.Save','core.button.Update','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.Confirm',
  				'core.button.Audit','core.button.Close','core.button.Delete','core.button.DeleteDetail','core.button.ResSubmit','core.button.TurnCustomer',
  				'core.button.ResAudit',	'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.grid.YnColumn'
      	],
    init:function(){
    	var me = this;
    	me.alloweditor = true;
    	this.control({ 
    		'erpGridPanel2': { 
    			afterrender: function(grid){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    			},
    			itemclick: this.onGridItemClick
    		},    		
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}, 
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('sc_id').value);    				
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('sc_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('sc_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('sc_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('sc_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('sc_id').value);
    			}
    		} ,
    		'erpConfirmButton': {
    		   afterrender: function(btn){
				   btn.setText("直接回复");
				   var status = Ext.getCmp('sc_statuscode');
   				   if(status && status.value != 'ENTERING'){
   					btn.hide();
   				   }
			   },
    			click: function(btn){    				
    				me.onConfirm(Ext.getCmp('sc_id').value);    				
    			}
    		} 
    		 /**
 		    * 更改供应商回复信息
 		    */
           
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
		var isagreed= Ext.getCmp('sc_agreed').value;
		if(!isagreed){
			showError("回复信息不能为空！");return;
		}		
		var remark= Ext.getCmp('sc_replyremark').value;
		Ext.Ajax.request({
	   		url : basePath + form.confirmUrl,
	   		params: {
	   			id: id,
	   			agreed:isagreed,
	   			remark:remark
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){	   		
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){    				
    				showMessage("提示", '回复成功');
	   					window.location.reload();	   								
	   			} else {
    				if(localJson.exceptionInfo){
    	   				var str = localJson.exceptionInfo;
    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
    	   					str = str.replace('AFTERSUCCESS', '');
    	   					showMessage("提示", '回复成功');    	   					
    	   						window.location.reload();    	   					
    	   				} else {
    	   					showError(str);return;
    	   				}
    	   			}
    			}
	   		}
		});
	},
});