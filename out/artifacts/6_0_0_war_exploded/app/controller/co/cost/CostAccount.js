Ext.QuickTips.init();
Ext.define('erp.controller.co.cost.CostAccount', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'co.cost.CostAccountForm','co.cost.CostAccount','core.button.BOMVastCost',
    		'core.button.Confirm','core.button.Close','core.form.MonthDateField'
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
        				// confirm box modify
						// zhuth 2018-2-1
						Ext.Msg.confirm('提示', '确定开始成本计算？', function(btn) {
							if(btn == 'yes') {
								me.confirm();
							}
						});
        			}
        		},
        		'erpBOMVastCostButton': {
        			click: function(btn){
        				// confirm box modify
						// zhuth 2018-2-1
						Ext.Msg.confirm('提示', '确认要计算当前月份成本表里产品的BOM成本？', function(btn) {
							if(btn == 'yes') {
								me.productCost();
							}
						});
        			}
        		},
        		'monthdatefield': {
        			afterrender: function(f) {
        				this.getCurrentMonth(f, "MONTH-T");
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	confirm: function(){
    		var begin = new Date().getTime();
    		var mb = new Ext.window.MessageBox();
    	    mb.wait('正在核算中','请稍后...',{
    		   interval: 60000, //bar will move fast!
    		   duration: 1800000,
    		   increment: 20,
    		   scope: this,
    		});
    		Ext.Ajax.request({
    			url : basePath + "co/cost/countCost.action",
    			params:{
    				param:{date:Ext.getCmp('date').value},
    			},
    			method:'post',
    			timeout: 2400000,
    			callback:function(options,success,response){
    				var end = new Date().getTime();
    				mb.close();
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				Ext.Msg.alert("提示","操作成功！耗时" + Ext.Number.toFixed((end-begin)/60000,2) +"分钟");
        			} else {
        				if(localJson.exceptionInfo){
        	   				var str = localJson.exceptionInfo;
        	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
        	   					str = str.replace('AFTERSUCCESS', '');
        	   					showError(str);
        	   					postSuccess(function(){
        	   						window.location.reload();
        	    				});
        	   				} else {
        	   					showError(str);return;
        	   				}
        	   			}
        			}
    			}
    		});
    	},
    	productCost: function(){
    		var begin = new Date().getTime();
    		var mb = new Ext.window.MessageBox();
    	    mb.wait('正在计算中','请稍后...',{
    		   interval: 60000, //bar will move fast!
    		   duration: 1800000,
    		   increment: 20,
    		   scope: this,
    		});
    		Ext.Ajax.request({
    			url : basePath + "co/cost/countStepCost.action",
    			params:{
    				param:{date:Ext.getCmp('date').value},
    			},
    			method:'post',
    			timeout: 2400000,
    			callback:function(options,success,response){
    				var end = new Date().getTime();
    				mb.close();
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				Ext.Msg.alert("提示","操作成功！耗时" + Ext.Number.toFixed((end-begin)/60000,2) +"分钟");
        			} else {
        				if(localJson.exceptionInfo){
        	   				var str = localJson.exceptionInfo;
        	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
        	   					str = str.replace('AFTERSUCCESS', '');
        	   					showError(str);
        	   					postSuccess(function(){
        	   						window.location.reload();
        	    				});
        	   				} else {
        	   					showError(str);return;
        	   				}
        	   			}
        			}
    			}
    		});
    	},
    	getCurrentMonth: function(f, type) {
        	Ext.Ajax.request({
        		url: basePath + 'fa/getMonth.action',
        		params: {
        			type: type
        		},
        		callback: function(opt, s, r) {
        			var rs = Ext.decode(r.responseText);
        			if(rs.data) {
        				f.setValue(rs.data.PD_DETNO);
        			}
        		}
        	});
        }
    });