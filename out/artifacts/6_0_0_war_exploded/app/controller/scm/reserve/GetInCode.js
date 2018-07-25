Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.GetInCode', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.GetInCode',
    		'core.button.Confirm','core.button.Close','core.trigger.DbfindTrigger'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpConfirmButton': {
        			click: function(btn){
        				if(Ext.getCmp('whi_clientcode').value==""||Ext.getCmp('whi_clientcode').value==null){
        					showError("请先选择委托方！");
        					return false;
        				}
        				/*me.BaseUtil.getRandomNumber('Warehouseing', 11, 'code');*/
        				/*console.log(Ext.getCmp('code').value);*/
    					me.insertWarehouseing(Ext.getCmp('whi_clientcode').value,Ext.getCmp('whi_clientname').value,Ext.getCmp('whi_amount').value,Ext.getCmp('whi_prefix').value);
        				
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	insertWarehouseing:function(whi_clientcode,whi_clientname,whi_amount,whi_freefix){
    		Ext.Ajax.request({
            	url : basePath + 'scm/reserve/createWarehouseing.action',
            	params: {
            		whi_clientcode:whi_clientcode,
            		whi_clientname:whi_clientname,
            		whi_amount:whi_amount,
            		whi_freefix:whi_freefix,
            		caller: 'Warehouseing'
            	},
            	method : 'post',
            	async:false,
            	callback : function(options,success,response){
            		var res = new Ext.decode(response.responseText);
            		if(res.exceptionInfo != null){
            			showError(res.exceptionInfo);
            			return false;
            		}
            		Ext.getCmp('code').setValue(res.code);
            		showMessage("提示", '获取成功！');
            	}
            });
    	},
    });