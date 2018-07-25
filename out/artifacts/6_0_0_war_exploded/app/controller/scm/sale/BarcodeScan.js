Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.BarcodeScan', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil', 'erp.util.RenderUtil'],
    views:[
     		'core.button.VastDeal','core.button.Delete','core.button.Close','core.button.TrayLabelPrint',
     		'core.form.Panel','core.button.Query', 'core.grid.ButtonColumn','core.button.OutLabelPrint'
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
    		'erpOutLabelPrintButton':{
    			click: function(btn) {
					var id=Ext.getCmp('pi_id').value;
					if(Ext.isEmpty(id)){
						showError("请录入出货单号再进行筛选!");
						return ;
					}
    				var reportName="OutLabel";
    				var condition = '{prodinout.pi_id}=' + id+ '';
    				me.print(id,reportName,condition);
    			}
    		}, 
    		'erpTrayLabelPrintButton':{
    			click: function(btn) {
					var id=Ext.getCmp('pi_id').value;
    				if(Ext.isEmpty(id)){
						showError("请录入出货单号再进行筛选!");
						return ;
					}
    				var reportName="TrayLabel";
    				var condition = '{prodinout.pi_id}=' + id+ '';
    				me.print(id,reportName,condition);
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
    print:function(id,reportName,condition){
    	var form=Ext.getCmp('form');
    	form.setLoading(true);//loading...
    	Ext.Ajax.request({
			url : basePath + 'scm/sale/printBarcode.action',
			params: {
				id: id,
				reportName:reportName
			},
			method : 'post',
			timeout: 360000,
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo) {
					form.setLoading(false);
					showError(res.exceptionInfo);
					return;
				}
				if(res.info.printtype=='pdf'){
					window.location.href=res.info.printUrl+'/print?reportname='+res.info.reportname+'&condition='+condition+'&whichsystem='+res.info.whichsystem+"&"+'defaultCondition=select * from prodinout where pi_id='+id;
				}else{
					var url = res.info.printUrl + '?reportfile=' + res.info.reportname + '&&rcondition='+condition+'&&company=&&sysdate=373FAE331D06E956870163DCB2A96EC7&&key=3D7595A98BFF809D5EEEA9668B47F4A5&&whichsystem='+res.info.whichsystem+'';		
					window.open(url,'_blank');
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
	onConfirm : function(piid, iswcj){
		var me = this, operator = Ext.getCmp('operator').getValue(), 
			lotNo =  Ext.getCmp('lotNo').value,
			dateCode =  Ext.getCmp('DateCode').value,
			remark =  Ext.getCmp('Remark').value;
		if(Ext.isEmpty(lotNo)){
			showError('请先采集lotNo.！');
			return;
		}
		if(operator.operator == 'get'){
			if(Ext.isEmpty(dateCode)){
				showError('请先采集 Date Code！');
				return;
			}
			me.FormUtil.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'scm/sale/insertProdioBarcode.action',
		   		params: {
		   			piid     : Ext.getCmp('piid').value,
		   			inoutno  : Ext.getCmp('inoutno').value,
		   			lotNo	 : lotNo,
		   			DateCode : DateCode,
		   			remark	 : remark,
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
	    				Ext.getCmp('gather').down('#lotNo').setValue('');
	    				Ext.getCmp('gather').down('#DateCode').setValue('');
	    				Ext.getCmp('gather').down('#Remark').setValue('');
	    				var yqty = Ext.getCmp('gather').down('#yqty').value;
	    				Ext.getCmp('gather').down('#yqty').setValue(Number(yqty)+Number(1));
	    				Ext.getCmp('gather').down('#lotNo').focus(false, 200);
	    				if(Number(Ext.getCmp('gather').down('#yqty').value) >= Number(Ext.getCmp('gather').down('#qty').value)){
	    					Ext.getCmp('gather').close();
	    				}
		   			}
		   		}
			}); 
		} else if(operator.operator == 'back'){ 
			me.FormUtil.getActiveTab().setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'scm/sale/deleteProdioBarcode.action',
		   		params: {
		   			piid     : Ext.getCmp('piid').value,
		   			inoutno  : Ext.getCmp('inoutno').value,
		   			lotNo	 : lotNo,
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
	    					Ext.getCmp('gather').down('#lotNo').setValue('');
		    				Ext.getCmp('gather').down('#DateCode').setValue('');
		    				Ext.getCmp('gather').down('#Remark').setValue('');
	    					var yqty = Ext.getCmp('gather').down('#yqty').value;
		    				Ext.getCmp('gather').down('#yqty').setValue(Number(yqty)-Number(1));
		    				Ext.getCmp('gather').down('#lotNo').focus(false, 200);
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