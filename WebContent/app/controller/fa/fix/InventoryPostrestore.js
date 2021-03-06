Ext.QuickTips.init();
Ext.define('erp.controller.fa.fix.InventoryPostrestore', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fix.InventoryPostrestore', 'core.button.Confirm', 'core.button.Close'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpConfirmButton': {
        			click: function(btn){
        				this.confirm();
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	confirm: function(){
    		//次方法为点击确定的时候执行的操作
    		var form = Ext.getCmp('InventoryPostrestoreView');
    		//参数
    		var param ='';
    		Ext.Ajax.request({
    			//confirmUrl为在对应view js中创建此form时赋值的地址
    			url:basePath+form.confirmUrl,
    			params:{
    				param:param,
    				caller:caller
    			},
    			method:'post',
    			callback:function(options,success,response){
    				var localJson = new Ext.decode(response.responseText);
    				if(localJson.success){
    					//执行成功
    					Ext.Msg.alert('操作成功!');
    				}else{
    					//执行失败
    					Ext.Msg.alert('操作失败!');
    				}
    			}
    		});
    	}
    });