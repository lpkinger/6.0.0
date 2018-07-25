Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.PurchaseAcceptNotify', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.PurchaseAcceptNotify','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.VastTurnAccept','core.button.BackAll'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			afterrender: function(grid){   			
					grid.setReadOnly(true);    				
    			}
    		},
             'erpVastTurnAcceptButton':{//确认接收，转为收料单
	             click:function(btn){
             		me.FormUtil.setLoading(true);
					var id = Ext.getCmp('pan_id').getValue();
					Ext.Ajax.request({
						url : basePath + 'scm/purchase/purchaseAcceptNotityTurnVerify.action',
						params: {
							id: id
						},
						method : 'post',
						callback : function(options,success,response){
							me.FormUtil.setLoading(false);
							var localJson = new Ext.decode(response.responseText);
							if(localJson.exceptionInfo){
								showError(localJson.exceptionInfo);return;
							}
    		    			if(localJson.success){
			    				if(localJson.log){
			    					showMessage("提示", localJson.log);
			    				}
    		    			}
						}
					});
             	},
             	afterrender:function(btn){
             		var status = Ext.getCmp("pan_statuscode");
             		if(status && status.value == 'DISAGREE'){
             			btn.setDisabled(true);
             		}
             	}
             },
             'erpBackAllButton':{//全部拒收
            	 click:function(btn){
            			warnMsg('该操作将拒收明细所有物料，确认拒收?', function(btn){
            				if(btn == 'yes'){
            					me.FormUtil.setLoading(true);
            					/*var id=Ext.getCmp('an_id').getValue();
            					Ext.Ajax.request({
            						url : basePath + 'scm/purchase/backAll.action',
            						params: {
            							id: id
            						},
            						method : 'post',
            						callback : function(options,success,response){
            							me.FormUtil.setLoading(false);
            							var localJson = new Ext.decode(response.responseText);
            							if(localJson.exceptionInfo){
            								showError(localJson.exceptionInfo);return;
            							}
            							if(localJson.success){
            								showMessage('提示', '拒收成功!', 1000);
            								window.location.reload();
            							} else {
            								delFailure();
            							}
            						}
            					});*/
            				}
            			});
            	 },
            	 afterrender:function(btn){
             		var status = Ext.getCmp("pan_statuscode");
             		if(status && (status.value == 'AGREE' || status.value == 'DISAGREE')){
             			btn.setDisabled(true);
             		}
             	}
             }    		
    	});
    }
});