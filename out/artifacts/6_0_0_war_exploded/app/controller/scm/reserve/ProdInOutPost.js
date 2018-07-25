Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.ProdInOutPost', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.ProdInOutPost',
    		'core.button.Confirm','core.button.Close',
    		'core.form.MonthDateField','core.form.ConDateField','core.trigger.DbfindTrigger'
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
        				var datef = Ext.getCmp('date'), 
                			from = datef ? (datef.firstVal ? Ext.Date.toString(datef.firstVal) : null ): null, 
                			to = datef ? (datef.secondVal ? Ext.Date.toString(datef.secondVal) : null ): null;
                		var pclass = Ext.getCmp('pclass').value;
                		if(Ext.isEmpty(pclass)){
                			alert('请先选择单据类型.');
                		}
                		if (!Ext.isEmpty(from) && !Ext.isEmpty(to) ) {
                			this.confirmPost(from, to, pclass);
                		} else {
                			alert('请先选择制日期范围.');
                		}
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	confirmPost: function(from, to, pclass){
    		var me = this;
    		var mb = new Ext.window.MessageBox();
    	    mb.wait('正在过账','请稍后...',{
    		   interval: 10000, 
    		   duration: 1000000,
    		   increment: 20,
    		   scope: this
    		});
    		Ext.Ajax.request({
    			url : basePath + "scm/prodInOutPost.action",
    			params:{
    				caller : caller,
    				from : from,
    				to : to,
    				pclass : pclass
    			},
    			timeout: 120000,
    			method:'post',
    			callback:function(options,success,r){
    				mb.close();
    				var rs = Ext.decode(r.responseText);
					if(rs.exceptionInfo) {
						showError(rs.exceptionInfo);
					} else {
						Ext.Msg.alert("提示","过账成功！");
						window.location.reload();
					}
    			}
    		});
    	}
    });