Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.MachineNoScan', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'scm.sale.MachineNoScan','core.button.VastDeal','core.button.Delete','core.button.Close',
     		'core.form.Panel','core.button.Query', 'core.grid.ButtonColumn'
     	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	this.control({
    		'erpQueryButton' : {
    			click: function(btn) {
    				me.query();
    			}
    		},
    		'#machineno': {
    			specialkey: function(f, e){//按ENTER自动把摘要复制到下一行
    				var piid = Ext.getCmp('pi_id').value, iswcj = Ext.getCmp("iswcj").value;
    				if ([e.ENTER, e.RETURN, e.SPACE].indexOf(e.getKey()) > -1) {
    					Ext.defer(function(){
    						var val = e.target.value;
    						if(val){
        						me.onConfirm(piid, iswcj, val);
            				}
    					}, 50);
    				}
    			}
    		},
			'#confirm' : {
				click: function(btn) {
					var piid = Ext.getCmp('pi_id').value, iswcj = Ext.getCmp("iswcj").value;
					me.onConfirm(piid, iswcj);
				}
			}
    	});
    },
	query : function(){
		var me = this;
		var pi_inoutno = Ext.getCmp("pi_inoutno").value, piid = Ext.getCmp('pi_id').value,
			iswcj = Ext.getCmp("iswcj").value;
		if(Ext.isEmpty(pi_inoutno)){
			showError("请录入出货单号再进行筛选!");
			return ;
		}
		if(!Ext.isEmpty(piid)) {
			Ext.getCmp('grid').getStore().load({
				params: {
					piid: piid,
					iswcj:iswcj
				}
			});
		}
	},
	onConfirm : function(piid, iswcj, machineno){
		var me = this, operator = Ext.getCmp('operator').getValue(), 
			machineno = machineno || Ext.getCmp('machineno').value;
		if(Ext.isEmpty(machineno)){
			showError('请先采集机器号！');
			return;
		}
		if(Ext.isEmpty(prcode)){
			showError('请先指定产品编号！');
			return;
		}
		if(operator.operator == 'get'){
			me.FormUtil.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'scm/sale/insertProdioMac.action',
		   		params: {
		   			piid     : Ext.getCmp('piid').value,
		   			inoutno  : Ext.getCmp('inoutno').value,
		   			machineno: machineno,
		   			prcode	 : Ext.getCmp('prcode').value,
		   			qty      : Ext.getCmp('qty').value
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			me.FormUtil.getActiveTab().setLoading(false);
		   			var r = new Ext.decode(response.responseText);
		   			if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			}
	    			if(r.success){
	    				if(!Ext.isEmpty(piid)) {
	    					Ext.getCmp('grid').getStore().load({
	    						params: {
	    							piid: piid,
	    							iswcj:iswcj
	    						}
	    					});
	    				}
	    				showMessage('提示', '采集成功!', 1000);
	    				Ext.getCmp('gather').down('#machineno').setValue('');
	    				var yqty = Ext.getCmp('gather').down('#yqty').value;
	    				Ext.getCmp('gather').down('#yqty').setValue(Number(yqty)+Number(1));
	    				Ext.getCmp('gather').down('#machineno').focus(false, 200);
	    				if(Number(Ext.getCmp('gather').down('#yqty').value) >= Number(Ext.getCmp('gather').down('#qty').value)){
	    					Ext.getCmp('gather').close();
	    				}
		   			}
		   		}
			}); 
		} else if(operator.operator == 'back'){ 
			me.FormUtil.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'scm/sale/deleteProdioMac.action',
		   		params: {
		   			piid     : Ext.getCmp('piid').value,
		   			inoutno  : Ext.getCmp('inoutno').value,
		   			machineno: machineno,
		   			prcode	 : Ext.getCmp('prcode').value,
		   			qty      : Ext.getCmp('qty').value
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			me.FormUtil.getActiveTab().setLoading(false);
		   			var r = new Ext.decode(response.responseText);
		   			if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			}
	    			if(r.success){
	    				showMessage('提示', '取消成功!', 1000);
	    				if(!Ext.isEmpty(piid)) {
	    					Ext.getCmp('grid').getStore().load({
	    						params: {
	    							piid: piid,
	    							iswcj:iswcj
	    						}
	    					});
	    					Ext.getCmp('gather').down('#machineno').setValue('');
	    					var yqty = Ext.getCmp('gather').down('#yqty').value;
		    				Ext.getCmp('gather').down('#yqty').setValue(Number(yqty)-Number(1));
		    				Ext.getCmp('gather').down('#machineno').focus(false, 200);
		    				if(Number(Ext.getCmp('gather').down('#yqty').value) == 0){
		    					Ext.getCmp('gather').close();
		    				}
	    				}
		   			}
		   		}
			}); 
		}
	}
});