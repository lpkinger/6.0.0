Ext.QuickTips.init();
Ext.define('erp.controller.hr.wage.calc.Wagecalculate', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.wage.calc.Wagecalculate',
    		'core.button.Confirm','core.button.Close','core.form.MonthDateField','core.button.Delete'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpDeleteButton':{
					click:function(){
						me.ondelete();
					}        		
        		},
        		'erpConfirmButton': {
        			click: function(btn){
        				confirm('确定开始工资报表计算？') && me.confirm();
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	ondelete:function(){
			Ext.MessageBox.confirm('提示',
			'确认删除'+Ext.getCmp('date').value+'的工资报表吗？',function(t){
				if (t=='yes') {
	    		Ext.Ajax.request({
	    			url : basePath + "hr/wage/report/delete.action",
	    			params:{
	    				date:Ext.getCmp('date').value
	    			},
	    			method:'post',
	    			callback:function(options,success,response){
						var rs = Ext.decode(response.responseText);
						if (rs.success) {
							Ext.Msg.alert('提示', Ext.getCmp('date').value+'年月的工资报表删除成功');
						}	    				
	    			}
	    		});					
					 
				}else if(t=='no'){
					return;
				}			
    		});
    		
    		
    	},
    	confirm: function(){
    		var begin = new Date().getTime();
    		var mb = new Ext.window.MessageBox();
    	    mb.wait('正在核算中','请稍后...',{
    		   interval: 60000, //bar will move fast!
    		   duration: 1800000,
    		   increment: 20,
    		   scope: this
    		});
    		Ext.Ajax.request({
    			url : basePath + "hr/wage/report/calculate.action",
    			params:{
//    				param:{date:Ext.getCmp('date').value}
    				date:Ext.getCmp('date').value
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
    	}
    });