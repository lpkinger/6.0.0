Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.AvgCostAccount', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.reserve.AvgCostAccount','core.button.TurnCostChange','core.button.ResultScan',
    		'core.button.BOMCost','core.button.Close','core.form.MonthDateField'
    	] ,
    	init:function(){
        	var me = this;
        	this.control({         		
        		'erpCloseButton': {
        			click: function(btn){
        				me.FormUtil.onClose();
        			}
        		},
        		'erpBOMCostButton': {
        			click: function(btn){
						Ext.Msg.confirm('提示', '确定开始成本计算？', function(btn) {
							if(btn == 'yes') {
								me.confirm();
							}
						});
        			}
        		},
        		'erpTurnCostChangeButton': {
        			click: function(btn){
						Ext.Msg.confirm('提示', '确认要产生成本调整单？', function(btn) {
							if(btn == 'yes') {
								me.turnCostChange();
							}
						});
        			}
        		},
        		'monthdatefield': {
        			afterrender: function(f) {
        				this.getCurrentMonth(f, "MONTH-P");
        			}
        		},
        		'erpResultScanButton': {
        			click: function(btn){
    					url = 'jsps/common/query.jsp?whoami=ProdWeightedAverage!Query&pwm_yearmonth=' + Ext.getCmp('date').value;
	    				me.FormUtil.onAdd('addProdWeightedAverage', '物料加权平均结果查询', url);
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
    	    mb.wait('正在计算中','请稍后...',{
    		   interval: 60000, //bar will move fast!
    		   duration: 1800000,
    		   increment: 20,
    		   scope: this,
    		});
    		Ext.Ajax.request({
    			url : basePath + "scm/reserve/countAvgCost.action",
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
    	turnCostChange: function(){
    		var me = this;
    		me.FormUtil.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
				url : basePath + 'scm/reserve/turnCostChange.action',
				params:{
	    			param:{date:Ext.getCmp('date').value},
	    		},
			   	method : 'post',
	   			callback : function(options,success,response){
	   			   	me.FormUtil.getActiveTab().setLoading(false);
	   		   		var r = new Ext.decode(response.responseText);
	   		   		if(r.exceptionInfo){
	   		   			showError(r.exceptionInfo);
	   		   		}
	   		   		if(r.success){
	   		   			if(r.content && r.content.pi_id){
	   	    				showMessage("提示", "转入成功，成本调整单号: <a href=\"javascript:openUrl2('jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!CostChange&formCondition=pi_idIS" + r.content.pi_id
	   	    							 + "&gridCondition=pd_piidIS" + r.content.pi_id + "','成本调整单','pi_id'," + r.content.pi_id
	   	    							 + ")\">" + r.content.pi_inoutno + "</a>");
	   	    			}
	   	    			window.location.reload();
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