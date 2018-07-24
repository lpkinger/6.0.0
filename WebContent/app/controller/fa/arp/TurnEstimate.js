Ext.QuickTips.init();
Ext.define('erp.controller.fa.arp.TurnEstimate', {
    extend: 'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'fa.arp.TurnEstimate','common.datalist.GridPanel','common.datalist.Toolbar',
      		'core.button.TurnEstimate','core.form.BtnDateField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpDatalistGridPanel': { 
    			afterrender: function(grid){
    				var t = grid.down('erpDatalistToolbar');
    				t.insert(t.items.items.length - 2, {
	    				xtype: 'erpTurnEstimateButton'
		    		});
    			}
    		},
    		'erpTurnEstimateButton': {
    			click: {
    				fn:function(btn){
        				me.turnEstimate(btn);
        			},
        			lock:2000
    			}
    		}
    	});
    }, 
    //点击 转应付暂估 按钮
    turnEstimate:function(btn){
    	warnMsg('确定全部转应付暂估吗?', function(b){
    		if(b == 'ok' || b == 'yes') {
		    	var grid = btn.ownerCt.ownerCt;
		    	grid.setLoading(true);
		    	Ext.Ajax.request({
			   		url : basePath + '/fa/EstimateController/turnEstimate.action',
			   		timeout: 120000,
			   		callback : function(options, success, response){
			   			grid.setLoading(false);
			   			var localJson = new Ext.decode(response.responseText);
		    			if(localJson.success){
		    				alert('转暂估成功!');
		    				window.location.reload();
		    			} else {
		    				if(localJson.exceptionInfo){
		    	   				var str = localJson.exceptionInfo;
		    	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){
		    	   					str = str.replace('AFTERSUCCESS', '');
		    	   					alert('转暂估成功!');
		    	    				window.location.reload();
		    	   				}
		    	   				showMessage("提示", str);return;
		    	   			}
		    			}
			   		}
				});
    		}
    	});
    }
});