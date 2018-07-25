Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.InventoryByCondition', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.InventoryByCondition','core.button.Confirm','core.button.Close','core.trigger.DbfindTrigger','core.form.Panel',
    		'core.trigger.MultiDbfindTrigger','core.trigger.AddDbfindTrigger','core.button.Save'
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
        				this.confirm();
        			}
        		}
        	});
        },
    	getForm: function(btn){
    		return btn.ownerCt.ownerCt;
    	},
    	confirm: function(){
    		var me = this;
    		this.BaseUtil.getActiveTab().setLoading(true);
    		var con=me.getCondition();
    		var whcodes='';
    		var thisvalue = Ext.getCmp('pr_whcode').value;
    		if(Ext.getCmp('pr_whcode').xtype == "adddbfindtrigger" || Ext.getCmp('pr_whcode').xtype == "multidbfindtrigger"){
				var arr=thisvalue.split('#');
				var l=arr.length;
				if(l>0){
					whcodes="'"+arr.join("','")+"'";
				}
    		}else{
    			whcodes="'"+thisvalue+"'";
    		}
    		Ext.Ajax.request({
    			url : basePath + "scm/reserve/inventoryByCondition.action",
    			params:{
    				method: Ext.getCmp('method').value,
    				whcode:	whcodes,
    				condition:con
    			},
    			method:'post',
    			timeout: 300000,
    			callback:function(options,success,response){
    				me.BaseUtil.getActiveTab().setLoading(false);
    				var localJson = new Ext.decode(response.responseText);
        			if(localJson.success){
        				Ext.Msg.alert("提示", localJson.log);
        			} else {
        				if(localJson.exceptionInfo){
        	   				var str = localJson.exceptionInfo;
        	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
        	   					str = str.replace('AFTERSUCCESS', '');
        	   					showError(str);
        	   					Ext.Msg.alert("提示",localJson.log);
        	   				} else {
        	   					showError(str);return;
        	   				}
        	   			}
        			}
    			}
    		});
    	},
    	getCondition:function(){
    		var form=Ext.getCmp('form');
    		var conditionArr= new Array();
    		var condition='';
    		Ext.each(form.items.items,function(item){
    			var logicField = item.logic,field = item.name,thisvalue = Ext.getCmp(field).value;
    			if (thisvalue !=null && thisvalue != "" && logicField != null) {
    				if (item.xtype == "condatefield") {
    					var firstValue=Ext.Date.format(Ext.getCmp(field).firstVal, 'Y-m-d');
    					var secondValue=Ext.Date.format(Ext.getCmp(field).secondVal, 'Y-m-d');
    						conditionArr.push("trunc("+logicField+") between to_date('"+firstValue+"','yyyy-mm-dd') and " +
    								"to_date('"+secondValue+"','yyyy-mm-dd')");
    				}else if(item.xtype == "datefield"){
    					thisvalue = Ext.Date.format(thisvalue, 'Y-m-d');
    					conditionArr.push("trunc("+logicField+")<=to_date('"+thisvalue+"','yyyy-mm-dd')");
    				}else if(item.xtype == "adddbfindtrigger" || item.xtype == "multidbfindtrigger"){
    						var arr=thisvalue.split('#');
    						var l=arr.length;
    						if(l>0){
    							conditionArr.push(logicField+" in ('"+arr.join("','")+"')");
    						}
    				}else if(item.xtype == "textareafield"){
    					conditionArr.push(logicField+" in ('"+thisvalue.split("\n").join("','")+"')");
    				}else if (item.xtype == "conmonthdatefield"){
    					thisvalue=thisvalue.replace(/BETWEEN/g,'');
    					thisvalue=thisvalue.replace(/AND/g,',');
    					var a=thisvalue.substring(0,7);
    					var b=thisvalue.substring(10,thisvalue.length);
    					conditionArr.push(logicField+" >="+a+ " and "+ logicField+ "<="+b);
    				} else{
    					conditionArr.push(logicField+"='"+thisvalue+"'");
    				}
    			} 
    		});
    		condition= conditionArr.join(' and '); 
    		return  condition;
    	}
    });